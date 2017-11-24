package tech.lapsa.insurance.notifier.beans;

import static tech.lapsa.insurance.notifier.beans.Constants.*;

import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;

import com.lapsa.insurance.domain.Request;
import com.lapsa.insurance.domain.casco.CascoRequest;
import com.lapsa.insurance.domain.policy.PolicyRequest;

import tech.lapsa.insurance.notifier.NotificationChannel;
import tech.lapsa.insurance.notifier.NotificationRecipientType;
import tech.lapsa.insurance.notifier.NotificationRequestStage;
import tech.lapsa.insurance.notifier.Notifier;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.javax.jms.JmsClientFactory;

@Stateless
public class NotifierBean implements Notifier {

    @Inject
    private JmsClientFactory jmsFactory;

    @Resource(name = JNDI_JMS_DEST_NEW_POLICY_COMPANY_EMAIL)
    private Destination newPolicyCompanyEmail;

    @Resource(name = JNDI_JMS_DEST_NEW_INSURANCE_COMPANY_PUSH)
    private Destination newInsuranceCompanyPush;

    @Resource(name = JNDI_JMS_DEST_NEW_CALLBACK_COMPANY_PUSH)
    private Destination newCallbackCompanyPush;

    @Resource(name = JNDI_JMS_DEST_NEW_POLICY_USER_EMAIL)
    private Destination newPolicyUserEmail;

    @Resource(name = JNDI_JMS_DEST_NEW_CASCO_COMPANY_EMAIL)
    private Destination newCascoCompanyEmail;

    @Resource(name = JNDI_JMS_DEST_NEW_CASCO_USER_EMAIL)
    private Destination newCascoUserEmail;

    @Resource(name = JNDI_JMS_DEST_REQUEST_PAID_COMPANY_EMAIL)
    private Destination requestPaidCompanyEmail;

    @Override
    public NotificationBuilder newNotificationBuilder() {
	return new NotificationBuilderImpl();
    }

    private final class NotificationBuilderImpl implements NotificationBuilder {

	private NotificationChannel channel;
	private NotificationRecipientType recipientType;
	private NotificationRequestStage event;
	private Request request;

	private NotificationBuilderImpl() {
	}

	@Override
	public NotificationBuilder withChannel(final NotificationChannel channel) {
	    this.channel = MyObjects.requireNonNull(channel, "channel");
	    return this;
	}

	@Override
	public NotificationBuilder withRecipient(final NotificationRecipientType recipientType) {
	    this.recipientType = MyObjects.requireNonNull(recipientType, "recipientType");
	    return this;
	}

	@Override
	public NotificationBuilder withEvent(final NotificationRequestStage event) {
	    this.event = MyObjects.requireNonNull(event, "event");
	    return this;
	}

	@Override
	public NotificationBuilder forEntity(final Request request) {
	    this.request = MyObjects.requireNonNull(request, "request");
	    return this;
	}

	@Override
	public Notification build() {
	    MyObjects.requireNonNull(request, "request");
	    final Destination destination = resolveDestination();

	    return new NotificationImpl(destination);
	}

	private Destination resolveDestination() {
	    MyObjects.requireNonNull(request, "request");
	    MyObjects.requireNonNull(event, "event");
	    MyObjects.requireNonNull(recipientType, "recipientType");
	    MyObjects.requireNonNull(channel, "channel");

	    switch (event) {
	    case NEW_REQUEST:
		switch (channel) {
		case EMAIL:
		    switch (recipientType) {
		    case COMPANY:
			if (request instanceof PolicyRequest)
			    return newPolicyCompanyEmail;
			if (request instanceof CascoRequest)
			    return newCascoCompanyEmail;
			break;
		    case REQUESTER:
			if (request instanceof PolicyRequest)
			    return newPolicyUserEmail;
			if (request instanceof CascoRequest)
			    return newCascoUserEmail;
			break;
		    default:
		    }
		case PUSH:
		    // TODO DEBUG : Push disabled temporary. Need to debug
		    // switch (recipientType) {
		    // case COMPANY:
		    // if (request instanceof InsuranceRequest)
		    // return newInsuranceCompanyPush;
		    // if (request instanceof CallbackRequest)
		    // return newCallbackCompanyPush;
		    // break;
		    // default:
		    // }
		    break;
		default:
		}
		break;
	    case REQUEST_PAID:
		switch (channel) {
		case EMAIL:
		    switch (recipientType) {
		    case COMPANY:
			return requestPaidCompanyEmail;
		    default:
			break;
		    }
		default:
		    break;
		}
		break;
	    default:
		break;
	    }
	    throw new IllegalStateException(String.format(
		    "Can't resolve Destination for channel '%2$s' recipient '%3$s' stage '%1$s'",
		    event, // 1
		    channel, // 2
		    recipientType // 3
	    ));
	}

	private final class NotificationImpl implements Notification {

	    private final Destination destination;
	    private final Request request;
	    private boolean sent = false;
	    private Consumer<Notification> onSuccess;

	    private NotificationImpl(final Destination destination) {
		this.destination = MyObjects.requireNonNull(destination, "destination");
		request = MyObjects.requireNonNull(NotificationBuilderImpl.this.request, "request");
	    }

	    @Override
	    public Notification onSuccess(Consumer<Notification> onSuccess) {
		this.onSuccess = MyObjects.requireNonNull(onSuccess, "onSuccess");
		return this;
	    }

	    @Override
	    public void send() {
		if (sent)
		    throw new IllegalStateException("Already sent");
		try {
		    jmsFactory.createSender(destination).send(request);
		    sent = true;
		    if (MyObjects.nonNull(onSuccess))
			onSuccess.accept(this);
		} catch (final RuntimeException e) {
		    throw new RuntimeException("Failed to assign a notification task", e);
		}
	    }
	}
    }

}
