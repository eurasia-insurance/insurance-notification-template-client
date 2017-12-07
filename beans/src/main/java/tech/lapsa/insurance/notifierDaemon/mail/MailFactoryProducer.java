package tech.lapsa.insurance.notifier.beans.producers;

import static tech.lapsa.insurance.notifier.beans.Constants.*;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.mail.Session;

import tech.lapsa.insurance.notifier.beans.qualifiers.QRecipientCompany;
import tech.lapsa.insurance.notifier.beans.qualifiers.QRecipientUser;
import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.impl.SessionMailFactory;

@Singleton
public class MailFactoryProducer {

    @Resource(mappedName = JNDI_MAIL_COMPANY)
    private Session companyMailSession;

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
