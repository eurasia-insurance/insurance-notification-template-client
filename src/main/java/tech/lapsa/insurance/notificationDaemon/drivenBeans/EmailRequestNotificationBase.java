package tech.lapsa.insurance.notificationDaemon.drivenBeans;

import static tech.lapsa.insurance.notificationDaemon.drivenBeans.Constants.*;

import java.util.Locale;
import java.util.Properties;

import javax.annotation.Resource;

import com.lapsa.insurance.domain.Request;

import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.MailMessageBuilder;
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

    @Override
    protected void sendWithModel(final TextModel textModel, final T request) {
	try {
	    final Locale locale = locale(request);

	    final MailMessageBuilder template = mailFactory()
		    .newMailBuilder();

	    final String subject = TextFactory.newTextTemplateBuilder() //
		    .buildFromPattern(getSubjectTemplate().regular(locale)) //
		    .merge(textModel) //
		    .asString();
	    template.withSubject(subject);

	    final String body = TextFactory.newTextTemplateBuilder() //
		    .buildFromInputStream(getBodyTemplate().getResourceAsStream(locale)) //
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
