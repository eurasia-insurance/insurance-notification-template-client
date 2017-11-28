package tech.lapsa.insurance.notifier.beans.mdb;

import static tech.lapsa.insurance.notifier.beans.Constants.*;

import java.util.Locale;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import com.lapsa.insurance.domain.Request;
import com.lapsa.insurance.domain.RequesterData;
import com.lapsa.insurance.domain.policy.PolicyRequest;

import tech.lapsa.insurance.notifier.beans.NotificationMessages;
import tech.lapsa.insurance.notifier.beans.NotificationTemplates;
import tech.lapsa.insurance.notifier.beans.qualifiers.QRecipientUser;
import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.MailMessageBuilder;

@MessageDriven(mappedName = JNDI_JMS_DEST_NEW_POLICY_USER_EMAIL)
public class NewPolicyUserEmailDrivenBean extends EmailRequestNotificationBase<PolicyRequest> {

    @Inject
    @QRecipientUser
    protected MailFactory mailFactory;

    public NewPolicyUserEmailDrivenBean() {
	super(PolicyRequest.class);
    }

    @Override
    protected MailFactory mailFactory() {
	return mailFactory;
    }

    @Override
    protected MailMessageBuilder recipients(final MailMessageBuilder builder, final Request request)
	    throws MailBuilderException {
	final RequesterData requester = request.getRequester();
	return builder.withTORecipient(requester.getEmail(), requester.getName());
    }

    @Override
    protected Locale locale(final Request request) {
	return request.getRequester().getPreferLanguage().getLocale();
    }

    @Override
    protected NotificationMessages getSubjectTemplate() {
	return NotificationMessages.POLICY_USER_EMAIL_SUBJECT;
    }

    @Override
    protected NotificationTemplates getBodyTemplate() {
	return NotificationTemplates.NEW_POLICY_USER_EMAIL_TEMPLATE;
    }
}
