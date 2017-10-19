package tech.lapsa.insurance.notifier.beans.mdb.push;

import static tech.lapsa.insurance.notifier.beans.Constants.*;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.MessageDriven;
import javax.jms.MessageListener;

import com.lapsa.pushapi.services.PushEndpointNotValid;
import com.lapsa.pushapi.services.PushFactoryBuilderSPI;
import com.lapsa.pushapi.services.PushFactoryException;
import com.lapsa.pushapi.services.PushSendError;
import com.lapsa.pushapi.services.PushSender;

import tech.lapsa.javax.jms.ObjectConsumerListener;

@MessageDriven(mappedName = JNDI_JMS_DEST_PUSH_JOBS)
public class PushJobHandlerDrivenBean extends ObjectConsumerListener<PushJob> implements MessageListener {

    protected PushJobHandlerDrivenBean() {
	super(PushJob.class);
    }

    private Logger logger = Logger.getLogger(PushJobHandlerDrivenBean.class.getPackage().getName());

    @Override
    protected void accept(PushJob job) {

	Instant b = Instant.now();

	PushSender sender = null;
	try {
	    sender = PushFactoryBuilderSPI.getInstance() //
		    .builder() //
		    .buildFactory(job.getFactoryProperties()).createSender();
	} catch (PushFactoryException e) {
	    logger.log(Level.WARNING, "ERROR SENDER INITIALIZATION", e);
	}

	if (sender != null)
	    try {
		logger.info(String.format("SENDING %1$s...", job));
		sender.send(job.getMessage(), job.getEndpoint());
		Duration d = Duration.between(b, Instant.now());
		logger.info(String.format("SUCCESSFULY SENT IN %2$.3f SEC %1$s", //
			job, // 1
			(double) d.toNanos() / 1000000000)// 2
		);
	    } catch (PushEndpointNotValid e) {
		Duration d = Duration.between(b, Instant.now());
		logger.log(Level.INFO, String.format("ENDPOINT IS NOT VALID %1$s (%2$.3f seconds)", //
			job.getEndpoint(), // 1
			(double) d.toNanos() / 1000000000)// 2
		);
	    } catch (PushSendError e) {
		Duration d = Duration.between(b, Instant.now());
		logger.log(Level.WARNING, String.format("SEND ERROR %1$s (%2$.3f seconds)", //
			job, // 1
			(double) d.toNanos() / 1000000000)// 2
			, e);
	    }
    }
}
