package tech.lapsa.insurance.notifier.beans.mdb;

import static tech.lapsa.insurance.notifier.beans.Constants.*;

import java.util.Locale;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import com.lapsa.insurance.domain.Request;
import com.lapsa.insurance.domain.policy.PolicyRequest;
import com.lapsa.international.localization.LocalizationLanguage;

import tech.lapsa.insurance.notifier.beans.NotificationMessages;
import tech.lapsa.insurance.notifier.beans.NotificationTemplates;
import tech.lapsa.insurance.notifier.beans.qualifiers.QRecipientCompany;
import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.MailMessageBuilder;

@MessageDriven(mappedName = JNDI_JMS_DEST_NEW_POLICY_COMPANY_EMAIL)
public class NewPolicyCompanyEmailDrivenBean extends AEmailRequestNotificationDrivenBean<PolicyRequest> {

    @Inject
    @QRecipientCompany
    protected MailFactory mailFactory;

    public NewPolicyCompanyEmailDrivenBean() {
	super(PolicyRequest.class);
    }

    @Override
    protected MailFactory mailFactory() {
	return mailFactory;
    }

    @Override
    protected MailMessageBuilder recipients(MailMessageBuilder builder, Request request) throws MailBuilderException {
	return builder.withDefaultRecipient();
    }

    @Override
    protected Locale locale(Request request) {
	return LocalizationLanguage.RUSSIAN.getLocale();
    }

    @Override
    protected NotificationMessages getSubjectTemplate() {
	return NotificationMessages.POLICY_COMPANY_EMAIL_SUBJECT;
    }

    @Override
    protected NotificationTemplates getBodyTemplate() {
	return NotificationTemplates.NEW_POLICY_COMPANY_EMAIL_TEMPLATE;
    }
}
