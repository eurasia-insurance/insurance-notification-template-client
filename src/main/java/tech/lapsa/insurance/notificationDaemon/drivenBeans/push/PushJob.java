package tech.lapsa.insurance.notificationDaemon.drivenBeans.push;

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

    public PushJob(final PushMessage message, final PushEndpoint endpoint) {
	this.message = message;
	this.endpoint = endpoint;
    }

    public PushJob(final PushMessage message, final PushEndpoint endpoint, final Properties factoryProperties) {
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

    public void setMessage(final PushMessage message) {
	this.message = message;
    }

    public PushEndpoint getEndpoint() {
	return endpoint;
    }

    public void setEndpoint(final PushEndpoint endpoint) {
	this.endpoint = endpoint;
    }

    public Properties getFactoryProperties() {
	return factoryProperties;
    }

    public void setFactoryProperties(final Properties factoryProperties) {
	this.factoryProperties = factoryProperties;
    }
}
