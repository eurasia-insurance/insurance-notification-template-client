package tech.lapsa.insurance.notificationDaemon.drivenBeans;

import static tech.lapsa.insurance.shared.jms.InsuranceDestinations.*;

import java.util.Locale;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import com.lapsa.insurance.domain.InsuranceRequest;
import com.lapsa.insurance.domain.Request;
import com.lapsa.insurance.domain.crm.User;
import com.lapsa.international.localization.LocalizationLanguage;

import tech.lapsa.insurance.notificationDaemon.resources.QRecipientCompany;
import tech.lapsa.insurance.notificationDaemon.template.NotificationMessages;
import tech.lapsa.insurance.notificationDaemon.template.NotificationTemplates;
import tech.lapsa.java.commons.function.MyStrings;
import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.MailMessageBuilder;

@MessageDriven(mappedName = NOTIFIER_REQUEST_PAID_COMPANY_EMAIL)
public class RequestPaidCompanyEmailDrivenBean extends EmailRequestNotificationBase<InsuranceRequest> {

    @Inject
    @QRecipientCompany
    protected MailFactory mailFactory;

    public RequestPaidCompanyEmailDrivenBean() {
	super(InsuranceRequest.class);
    }

    @Override
    protected MailFactory mailFactory() {
	return mailFactory;
    }

    @Override
    protected MailMessageBuilder recipients(final MailMessageBuilder builder, final Request request)
	    throws MailBuilderException {
	final User a = request.getAcceptedBy();
	if (a != null && MyStrings.nonEmpty(a.getEmail()))
	    return builder.withTORecipient(a.getEmail(), a.getName());
	return builder.withDefaultRecipient();
    }

    @Override
    protected Locale locale(final Request request) {
	return LocalizationLanguage.RUSSIAN.getLocale();
    }

    @Override
    protected NotificationMessages getSubjectTemplate() {
	return NotificationMessages.INUSNRANCE_PAID_COMPANY_EMAIL_SUBJECT;
    }

    @Override
    protected NotificationTemplates getBodyTemplate() {
	return NotificationTemplates.INUSNRANCE_PAID_COMPANY_EMAIL_TEMPLATE;
    }
}
