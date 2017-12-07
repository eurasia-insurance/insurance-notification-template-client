package tech.lapsa.insurance.notifierDaemon.drivenBeans;

import static tech.lapsa.insurance.notifierDaemon.drivenBeans.Constants.*;
import static tech.lapsa.insurance.shared.jms.InsuranceDestinations.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.inject.Inject;

import com.lapsa.insurance.domain.Request;
import com.lapsa.pushapi.core.PushChannel;
import com.lapsa.pushapi.core.PushSubscriber;
import com.lapsa.pushapi.services.PushFactory;
import com.lapsa.pushapi.services.PushFactoryBuilderSPI;
import com.lapsa.pushapi.services.PushMessage;

import tech.lapsa.insurance.notifierDaemon.drivenBeans.push.PushJob;
import tech.lapsa.java.commons.logging.MyLogger;
import tech.lapsa.javax.jms.client.JmsDestination;
import tech.lapsa.javax.jms.client.JmsEventNotificatorClient;
import tech.lapsa.lapsa.text.TextFactory;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder.TextModel;
import tech.lapsa.patterns.dao.NotFound;
import tech.lapsa.push.dao.PushChannelDAO;
import tech.lapsa.push.dao.PushSubscriberDAO;

public abstract class PushRequestNotificationBase<T extends Request> extends RequestNotificationBase<T> {

    PushRequestNotificationBase(final Class<T> objectClazz) {
	super(objectClazz);
    }

    private final MyLogger logger = MyLogger.newBuilder() //
	    .withNameOf(this.getClass()) //
	    .build();

    protected abstract NotificationMessages getTitleMessageBudnle();

    protected abstract NotificationMessages getBodyMessageBudnle();

    protected abstract String getChannelIdConfigurationProperty();

    protected abstract String getPushURLConfigurationProperty();

    @Resource(lookup = JNDI_RESOURCE_CONFIGURATION)
    private Properties configurationProperties;

    @Inject
    private PushChannelDAO pushChannelDAO;

    @Inject
    private PushSubscriberDAO pushSubscriberDAO;

    private PushChannel pchannel;

    private URL url;

    private Properties factoryProperties;

    private PushFactory factory;

    @PostConstruct
    public void init() {

	{
	    final String pushChannelId = configurationProperties.getProperty(getChannelIdConfigurationProperty(), null);
	    try {
		pchannel = pushChannelDAO.getById(pushChannelId);
	    } catch (final NotFound e) {
		throw new EJBException(String.format("Failed to initialize CDI-bean %1$s. Invalid channelId %2$s",
			this.getClass().getName(), pushChannelId), e);
	    }
	}

	{
	    final String pushUrl = configurationProperties.getProperty(getPushURLConfigurationProperty(), null);
	    try {
		url = new URL(pushUrl);
	    } catch (final MalformedURLException e1) {
		logger.WARN.log(e1, "Push channel id %1$s is not a valid URL resource", pushUrl);
	    }
	}

	{
	    factoryProperties = new Properties();
	    if (pchannel != null && pchannel.getGcmApiKey() != null)
		factoryProperties.setProperty("gcm.key", pchannel.getGcmApiKey());
	    factory = PushFactoryBuilderSPI.getInstance().builder().buildFactory(factoryProperties);
	}
    }

    @Inject
    @JmsDestination(NOTIFIER_PUSH_JOBS)
    private JmsEventNotificatorClient<PushJob> pushJobNotificatorClient;

    @Override
    protected void sendWithModel(final TextModel textModel, final T request) {

	final Locale locale = locale(request);

	final String title = TextFactory.newTextTemplateBuilder() //
		.buildFromPattern(getTitleMessageBudnle().regular(locale)) //
		.merge(textModel) //
		.asString();

	final String body = TextFactory.newTextTemplateBuilder() //
		.buildFromPattern(getBodyMessageBudnle().regular(locale)) //
		.merge(textModel) //
		.asString();

	final PushMessage message = new PushMessage(title, body, url);

	final List<PushSubscriber> subscribers = pushSubscriberDAO.findByChannel(pchannel);
	subscribers.stream() //
		.map(x -> factory.createEndpoint(x.getEndpoint(), x.getUserPublicKey(), x.getUserAuth())) //
		.map(x -> new PushJob(message, x, factoryProperties)) //
		.forEach(pushJobNotificatorClient::eventNotify);
    }
}
