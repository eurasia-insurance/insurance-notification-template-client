package tech.lapsa.insurance.notifier.beans.mdb;

import static tech.lapsa.insurance.shared.jms.InsuranceDestinations.*;

import java.util.Locale;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import com.lapsa.insurance.domain.Request;
import com.lapsa.insurance.domain.casco.CascoRequest;
import com.lapsa.international.localization.LocalizationLanguage;

import tech.lapsa.insurance.notifier.beans.NotificationMessages;
import tech.lapsa.insurance.notifier.beans.NotificationTemplates;
import tech.lapsa.insurance.notifier.beans.qualifiers.QRecipientCompany;
import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.MailMessageBuilder;

@MessageDriven(mappedName = NOTIFIER_NEW_CASCO_COMPANY_EMAIL)
public class NewCascoCompanyEmailDrivenBean extends EmailRequestNotificationBase<CascoRequest> {

    @Inject
    @QRecipientCompany
    protected MailFactory mailFactory;

    public NewCascoCompanyEmailDrivenBean() {
	super(CascoRequest.class);
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
	return NotificationMessages.CASCO_COMPANY_EMAIL_SUBJECT;
    }

    @Override
    protected NotificationTemplates getBodyTemplate() {
	return NotificationTemplates.NEW_CASCO_COMPANY_EMAIL_TEMPLATE;
    }
}
