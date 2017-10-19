package tech.lapsa.insurance.notifier.beans.mdb.push;

import java.io.Serializable;
import java.util.Properties;

import com.lapsa.pushapi.services.PushEndpoint;
import com.lapsa.pushapi.services.PushMessage;

public class PushJob implements Serializable {
    private static final long serialVersionUID = -179229368136732614L;

    private PushMessage message;
    private PushEndpoint endpoint;
    private Properties factoryProperties;

    public PushJob() {
    }

    public PushJob(PushMessage message, PushEndpoint endpoint) {
	this.message = message;
	this.endpoint = endpoint;
    }

    public PushJob(PushMessage message, PushEndpoint endpoint, Properties factoryProperties) {
	this.message = message;
	this.endpoint = endpoint;
	this.factoryProperties = factoryProperties;
    }

    @Override
    public String toString() {
	return message + " TO " + endpoint;
    }
    
    public PushMessage getMessage() {
	return message;
    }

    public void setMessage(PushMessage message) {
	this.message = message;
    }

    public PushEndpoint getEndpoint() {
	return endpoint;
    }

    public void setEndpoint(PushEndpoint endpoint) {
	this.endpoint = endpoint;
    }

    public Properties getFactoryProperties() {
	return factoryProperties;
    }

    public void setFactoryProperties(Properties factoryProperties) {
	this.factoryProperties = factoryProperties;
    }
}
