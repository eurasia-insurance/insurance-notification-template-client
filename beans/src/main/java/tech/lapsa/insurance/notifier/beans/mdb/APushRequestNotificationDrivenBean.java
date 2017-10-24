package tech.lapsa.insurance.notifier.beans.mdb;

import static tech.lapsa.insurance.notifier.beans.Constants.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.lapsa.insurance.domain.Request;
import com.lapsa.pushapi.core.PushChannel;
import com.lapsa.pushapi.core.PushSubscriber;
import com.lapsa.pushapi.services.PushEndpoint;
import com.lapsa.pushapi.services.PushFactory;
import com.lapsa.pushapi.services.PushFactoryBuilderSPI;
import com.lapsa.pushapi.services.PushFactoryException;
import com.lapsa.pushapi.services.PushMessage;

import tech.lapsa.insurance.notifier.beans.NotificationMessages;
import tech.lapsa.insurance.notifier.beans.mdb.push.PushJob;
import tech.lapsa.java.commons.logging.MyLogger;
import tech.lapsa.lapsa.text.TextFactory;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder.TextModel;
import tech.lapsa.patterns.dao.NotFound;
import tech.lapsa.push.dao.PushChannelDAO;
import tech.lapsa.push.dao.PushSubscriberDAO;

public abstract class APushRequestNotificationDrivenBean<T extends Request> extends ARequestNotificationDrivenBean<T> {

    APushRequestNotificationDrivenBean(final Class<T> objectClazz) {
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

    @Resource(name = JNDI_JMS_CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    @Resource(name = JNDI_JMS_DEST_PUSH_JOBS)
    private Destination pushJobDestination;

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
	    String pushChannelId = configurationProperties.getProperty(getChannelIdConfigurationProperty(), null);
	    try {
		pchannel = pushChannelDAO.getById(pushChannelId);
	    } catch (NotFound e) {
		throw new EJBException(String.format("Failed to initialize CDI-bean %1$s. Invalid channelId %2$s",
			this.getClass().getName(), pushChannelId), e);
	    }
	}

	{
	    String pushUrl = configurationProperties.getProperty(getPushURLConfigurationProperty(), null);
	    try {
		url = new URL(pushUrl);
	    } catch (MalformedURLException e1) {
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

    @Override
    protected void sendWithModel(TextModel textModel, T request) {

	Locale locale = locale(request);

	String title = TextFactory.newTextTemplateBuilder() //
		.buildFromPattern(getTitleMessageBudnle().regular(locale)) //
		.merge(textModel) //
		.asString();

	String body = TextFactory.newTextTemplateBuilder() //
		.buildFromPattern(getBodyMessageBudnle().regular(locale)) //
		.merge(textModel) //
		.asString();

	PushMessage message = new PushMessage(title, body, url);

	try (Connection connection = connectionFactory.createConnection()) {
	    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    MessageProducer producer = session.createProducer(pushJobDestination);

	    List<PushSubscriber> subscribers = pushSubscriberDAO.findByChannel(pchannel);
	    for (PushSubscriber psubscr : subscribers) {
		PushEndpoint ep = factory.createEndpoint(psubscr.getEndpoint(), psubscr.getUserPublicKey(),
			psubscr.getUserAuth());
		PushJob job = new PushJob(message, ep, factoryProperties);
		Message msg = session.createObjectMessage(job);
		producer.send(msg);
	    }
	} catch (PushFactoryException e) {
	    throw new RuntimeException(String.format("Something goes wrong during the push process"), e);
	} catch (JMSException e) {
	    throw new RuntimeException("Failed assign a push job");
	}
    }

}
