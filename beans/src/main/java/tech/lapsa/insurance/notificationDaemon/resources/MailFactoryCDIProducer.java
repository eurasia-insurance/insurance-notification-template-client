package tech.lapsa.insurance.notificationDaemon.resources;

import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.mail.Session;

import tech.lapsa.lapsa.mail.MailBuilderException;
import tech.lapsa.lapsa.mail.MailFactory;
import tech.lapsa.lapsa.mail.impl.SessionMailFactory;

@Dependent
public class MailFactoryCDIProducer {

    public static final String JNDI_MAIL_COMPANY = "insurance/mail/messaging/CompanyNotification";
    public static final String JNDI_MAIL_USER = "insurance/mail/messaging/UserNotification";

    @Resource(mappedName = JNDI_MAIL_COMPANY)
    private Session companyMailSession;

    // TODO FEAUTURE : implement using injection point and accuring JNDI_PATH
    // from annotation
    @Produces
    @QRecipientCompany
    public MailFactory companyMailFactory() throws MailBuilderException {
	return new SessionMailFactory(companyMailSession);
    }

    @Resource(mappedName = JNDI_MAIL_USER)
    private Session userMailSession;

    @Produces
    @QRecipientUser
    public MailFactory userMailFactory() throws MailBuilderException {
	return new SessionMailFactory(userMailSession);
    }
}
