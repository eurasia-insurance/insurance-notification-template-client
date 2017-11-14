package tech.lapsa.insurance.notifier.beans.mdb.push;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

import javax.jms.MessageListener;

import com.lapsa.pushapi.services.PushEndpointNotValid;
import com.lapsa.pushapi.services.PushFactoryBuilderSPI;
import com.lapsa.pushapi.services.PushFactoryException;
import com.lapsa.pushapi.services.PushSendError;
import com.lapsa.pushapi.services.PushSender;

import tech.lapsa.java.commons.logging.MyLogger;
import tech.lapsa.javax.jms.ObjectConsumerListener;

//TODO DEBUG : Push disabled temporary. Need to debug
//@MessageDriven(mappedName = JNDI_JMS_DEST_PUSH_JOBS)
public class PushJobHandlerDrivenBean extends ObjectConsumerListener<PushJob> implements MessageListener {

    protected PushJobHandlerDrivenBean() {
	super(PushJob.class);
    }

    private static final MyLogger logger = MyLogger.newBuilder() //
	    .withNameOf(PushJobHandlerDrivenBean.class) //
	    .build();

    @Override
    protected void accept(PushJob job, Properties properties) {

	Instant b = Instant.now();

	PushSender sender = null;
	try {
	    sender = PushFactoryBuilderSPI.getInstance() //
		    .builder() //
		    .buildFactory(job.getFactoryProperties()).createSender();
	} catch (PushFactoryException e) {
	    logger.WARNING.log(e, "ERROR SENDER INITIALIZATION");
	}

	if (sender != null)
	    try {
		logger.INFO.log("SENDING %1$s...", job);
		sender.send(job.getMessage(), job.getEndpoint());
		Duration d = Duration.between(b, Instant.now());
		logger.INFO.log("SUCCESSFULY SENT IN %2$.3f SEC %1$s", //
			job, // 1
			(double) d.toNanos() / 1000000000// 2
		);
	    } catch (PushEndpointNotValid e) {
		Duration d = Duration.between(b, Instant.now());
		logger.INFO.log("ENDPOINT IS NOT VALID %1$s (%2$.3f seconds)", //
			job.getEndpoint(), // 1
			(double) d.toNanos() / 1000000000// 2
		);
	    } catch (PushSendError e) {
		Duration d = Duration.between(b, Instant.now());
		logger.WARNING.log(e, "SEND ERROR %1$s (%2$.3f seconds)", //
			job, // 1
			(double) d.toNanos() / 1000000000// 2
		);
	    }
    }
}
