package tech.lapsa.insurance.notificationDaemon.template;

import java.util.Locale;

import javax.ejb.Local;
import javax.ejb.Remote;

import tech.lapsa.java.commons.exceptions.IllegalArgument;

public interface InsuranceTemplateProvider {

    @Local
    public interface InsuranceTemplateProviderLocal extends InsuranceTemplateProvider {
    }

    @Remote
    public interface InsuranceTemplateProviderRemote extends InsuranceTemplateProvider {
    }

    String getMessage(NotificationMessages message, Locale locale) throws IllegalArgument;

    String getTemplate(NotificationTemplates message, Locale locale) throws IllegalArgument;

}
