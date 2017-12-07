package tech.lapsa.insurance.notificationDaemon.drivenBeans;

import static tech.lapsa.insurance.notificationDaemon.drivenBeans.Constants.*;

import java.util.Locale;
import java.util.Properties;

import javax.annotation.Resource;

import com.lapsa.insurance.domain.CallbackRequest;
import com.lapsa.insurance.domain.InsuranceRequest;
import com.lapsa.insurance.domain.Request;
import com.lapsa.insurance.domain.casco.CascoRequest;
import com.lapsa.insurance.domain.policy.PolicyRequest;

import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.javax.jms.service.JmsReceiverServiceDrivenBean;
import tech.lapsa.javax.jms.service.JmsSkipValidation;
import tech.lapsa.lapsa.text.TextFactory;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder.TextModel;

@JmsSkipValidation
public abstract class RequestNotificationBase<T extends Request> extends JmsReceiverServiceDrivenBean<T> {

    RequestNotificationBase(final Class<T> objectClazz) {
	super(objectClazz);
    }

    protected abstract Locale locale(Request request);

    @Resource(lookup = JNDI_RESOURCE_CONFIGURATION)
    private Properties configurationProperties;

    @Override
    public void receiving(final T request, final Properties properties) {
	MyObjects.requireNonNull(request, "request");

	final TextModelBuilder builder = TextFactory.newModelBuilder() //
		.withLocale(locale(request)) //
		.bind("instanceVerb", configurationProperties.getProperty(PROPERTY_INSTANCE_VERB, "")) //
		.bind("request", request) //
		.bind("requester", request.getRequester());

	if (request instanceof InsuranceRequest) {
	    final InsuranceRequest insuranceRequest = (InsuranceRequest) request;
	    builder.bind("insuranceRequest", insuranceRequest) //
		    .bind("product", insuranceRequest.getProduct()) //
		    .bind("obtaining", insuranceRequest.getObtaining()) //
		    .bind("payment", insuranceRequest.getPayment());
	}

	if (request instanceof PolicyRequest) {
	    final PolicyRequest policyRequest = (PolicyRequest) request;
	    builder.bind("policyRequest", policyRequest) //
		    .bind("policy", policyRequest.getPolicy());
	}

	if (request instanceof CascoRequest) {
	    final CascoRequest cascoRequest = (CascoRequest) request;
	    builder.bind("cascoRequest", cascoRequest) //
		    .bind("casco", cascoRequest.getCasco());
	}

	if (request instanceof CallbackRequest) {
	    final CallbackRequest callbackRequest = (CallbackRequest) request;
	    builder.bind("callbackRequest", callbackRequest);
	}

	final TextModel textModel = builder.build();
	sendWithModel(textModel, request);
    }

    protected abstract void sendWithModel(TextModel textModel, T request);
}
