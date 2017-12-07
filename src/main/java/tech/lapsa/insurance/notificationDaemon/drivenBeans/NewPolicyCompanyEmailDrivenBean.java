package tech.lapsa.insurance.notificationDaemon.drivenBeans;

import static tech.lapsa.insurance.shared.jms.InsuranceDestinations.*;

import java.util.Locale;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import com.lapsa.insurance.domain.Request;
import com.lapsa.insurance.domain.policy.PolicyRequest;
import com.lapsa.international.localization.LocalizationLanguage;

import tech.lapsa.insurance.notificationDaemon.resources.QRecipientCompany;
import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.MailMessageBuilder;

@MessageDriven(mappedName = NOTIFIER_NEW_POLICY_COMPANY_EMAIL)
public class NewPolicyCompanyEmailDrivenBean extends EmailRequestNotificationBase<PolicyRequest> {

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
    protected MailMessageBuilder recipients(final MailMessageBuilder builder, final Request request)
	    throws MailBuilderException {
	return builder.withDefaultRecipient();
    }

    @Override
    protected Locale locale(final Request request) {
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
