package tech.lapsa.insurance.notificationDaemon.drivenBeans;

import static tech.lapsa.insurance.notificationDaemon.drivenBeans.Constants.*;

import java.util.Locale;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;

import com.lapsa.insurance.domain.Request;

import tech.lapsa.insurance.notificationDaemon.template.NotificationMessages;
import tech.lapsa.insurance.notificationDaemon.template.NotificationTemplates;
import tech.lapsa.insurance.notificationDaemon.template.InsuranceTemplateProvider.InsuranceTemplateProviderRemote;
import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.lapsa.mail.MailBuilderException;
import tech.lapsa.lapsa.mail.MailException;
import tech.lapsa.lapsa.mail.MailFactory;
import tech.lapsa.lapsa.mail.MailMessageBuilder;
import tech.lapsa.lapsa.text.TextFactory;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder.TextModel;

public abstract class EmailRequestNotificationBase<T extends Request> extends RequestNotificationBase<T> {

    EmailRequestNotificationBase(final Class<T> cls) {
	super(cls);
    }

    protected abstract MailFactory mailFactory();

    protected abstract MailMessageBuilder recipients(MailMessageBuilder builder, Request request)
	    throws MailBuilderException;

    protected abstract NotificationMessages getSubjectTemplate();

    protected abstract NotificationTemplates getBodyTemplate();

    @Resource(lookup = JNDI_RESOURCE_CONFIGURATION)
    private Properties configurationProperties;

    @EJB
    private InsuranceTemplateProviderRemote templates;

    @Override
    protected void sendWithModel(final TextModel textModel, final T request) {
	try {
	    final Locale locale = locale(request);

	    final MailMessageBuilder template = mailFactory()
		    .newMailBuilder();

	    final String subjectTemplate;
	    try {
		subjectTemplate = templates.getMessage(getSubjectTemplate(), locale);
	    } catch (IllegalArgument e) {
		// it should not happens
		throw new EJBException(e.getMessage());
	    }

	    final String subject = TextFactory.newTextTemplateBuilder() //
		    .buildFromPattern(subjectTemplate) //
		    .merge(textModel) //
		    .asString();
	    template.withSubject(subject);

	    final String bodyTemplate;
	    try {
		bodyTemplate = templates.getTemplate(getBodyTemplate(), locale);
	    } catch (IllegalArgument e) {
		// it should not happens
		throw new EJBException(e.getMessage());
	    }

	    final String body = TextFactory.newTextTemplateBuilder() //
		    .buildFromPattern(bodyTemplate) //
		    .merge(textModel) //
		    .asString();
	    template.withHtmlPart(body);

	    recipients(template, request)
		    .build()
		    .send();

	} catch (final MailException e) {
	    throw new RuntimeException("Failed to create or send email", e);
	}
    }

}
