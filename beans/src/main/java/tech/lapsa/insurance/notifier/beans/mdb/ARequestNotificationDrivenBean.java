package tech.lapsa.insurance.notifier.beans.mdb;

import static tech.lapsa.insurance.notifier.beans.Constants.*;

import java.util.Locale;
import java.util.Properties;

import javax.annotation.Resource;

import com.lapsa.insurance.domain.CallbackRequest;
import com.lapsa.insurance.domain.InsuranceRequest;
import com.lapsa.insurance.domain.Request;
import com.lapsa.insurance.domain.casco.CascoRequest;
import com.lapsa.insurance.domain.policy.PolicyRequest;

import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.javax.jms.ObjectConsumerListener;
import tech.lapsa.lapsa.text.TextFactory;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder.TextModel;

public abstract class ARequestNotificationDrivenBean<T extends Request> extends ObjectConsumerListener<T> {

    ARequestNotificationDrivenBean(final Class<T> objectClazz) {
	super(objectClazz);
    }

    protected abstract Locale locale(Request request);

    @Resource(lookup = JNDI_RESOURCE_CONFIGURATION)
    private Properties configurationProperties;

    @Override
    protected void accept(T request) {
	MyObjects.requireNonNull(request, "request");

	TextModelBuilder builder = TextFactory.newModelBuilder() //
		.withLocale(locale(request)) //
		.bind("instanceVerb", configurationProperties.getProperty(PROPERTY_INSTANCE_VERB, "")) //
		.bind("request", request) //
		.bind("requester", request.getRequester());

	if (request instanceof InsuranceRequest) {
	    InsuranceRequest insuranceRequest = (InsuranceRequest) request;
	    builder.bind("insuranceRequest", insuranceRequest) //
		    .bind("product", insuranceRequest.getProduct()) //
		    .bind("obtaining", insuranceRequest.getObtaining()) //
		    .bind("payment", insuranceRequest.getPayment());
	}

	if (request instanceof PolicyRequest) {
	    PolicyRequest policyRequest = (PolicyRequest) request;
	    builder.bind("policyRequest", policyRequest) //
		    .bind("policy", policyRequest.getPolicy());
	}

	if (request instanceof CascoRequest) {
	    CascoRequest cascoRequest = (CascoRequest) request;
	    builder.bind("cascoRequest", cascoRequest) //
		    .bind("casco", cascoRequest.getCasco());
	}

	if (request instanceof CallbackRequest) {
	    CallbackRequest callbackRequest = (CallbackRequest) request;
	    builder.bind("callbackRequest", callbackRequest);
	}

	TextModel textModel = builder.build();
	sendWithModel(textModel, request);
    }

    protected abstract void sendWithModel(TextModel textModel, T request);
}
