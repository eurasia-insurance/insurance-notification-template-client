package tech.lapsa.insurance.notifier.beans;

public final class Constants {

    private Constants() {
    }

    public static final String JNDI_MAIL_COMPANY = "insurance/mail/messaging/CompanyNotification";
    public static final String JNDI_MAIL_USER = "insurance/mail/messaging/UserNotification";

    public static final String JNDI_RESOURCE_CONFIGURATION = "insurance/resource/messaging/Configuration";

    public static final String PROPERTY_INSTANCE_VERB = "mesenger.instance.verb";
    public static final String PROPERTY_COMPANY_PUSH_CHANNEL_ID = "mesenger.push.channel-id";
    public static final String PROPERTY_COMPANY_PUSH_URL = "mesenger.push.url";
}
