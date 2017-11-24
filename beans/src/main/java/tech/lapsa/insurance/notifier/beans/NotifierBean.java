package tech.lapsa.insurance.notifier.beans;

import static tech.lapsa.insurance.notifier.beans.Constants.*;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.Destination;

import com.lapsa.insurance.domain.Request;
import com.lapsa.insurance.domain.casco.CascoRequest;
import com.lapsa.insurance.domain.policy.PolicyRequest;

import tech.lapsa.insurance.notifier.Notification;
import tech.lapsa.insurance.notifier.Notifier;
import tech.lapsa.javax.jms.JmsClientFactory;

@Stateless
public class NotifierBean implements Notifier {

    @Inject
    private JmsClientFactory jmsFactory;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void send(final Notification notification) {
	final Destination destination = resolveDestination(notification);
	jmsFactory.createSender(destination).send(notification.getRequest());
    }

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

    private Destination resolveDestination(Notification notification) {
	final Request request = notification.getRequest();
	switch (notification.getEvent()) {
	case NEW_REQUEST:
	    switch (notification.getChannel()) {
	    case EMAIL:
		switch (notification.getRecipientType()) {
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
	    switch (notification.getChannel()) {
	    case EMAIL:
		switch (notification.getRecipientType()) {
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
		notification.getEvent(), // 1
		notification.getChannel(), // 2
		notification.getRecipientType() // 3
	));
    }
}
