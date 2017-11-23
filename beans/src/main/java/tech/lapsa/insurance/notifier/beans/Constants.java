package tech.lapsa.insurance.notifier.beans;

public final class Constants {

    private Constants() {
    }

    public static final String JNDI_MAIL_COMPANY = "insurance/mail/messaging/CompanyNotification";
    public static final String JNDI_MAIL_USER = "insurance/mail/messaging/UserNotification";

    public static final String JNDI_RESOURCE_CONFIGURATION = "insurance/resource/messaging/Configuration";

    public static final String JNDI_JMS_DEST_NEW_CASCO_COMPANY_EMAIL = "insurance/jms/messaging/cascoNewCompanyEmail";
    public static final String JNDI_JMS_DEST_NEW_CASCO_USER_EMAIL = "insurance/jms/messaging/cascoNewUserEmail";
    public static final String JNDI_JMS_DEST_NEW_POLICY_COMPANY_EMAIL = "insurance/jms/messaging/policyNewCompanyEmail";
    public static final String JNDI_JMS_DEST_NEW_POLICY_USER_EMAIL = "insurance/jms/messaging/policyNewUserEmail";
    public static final String JNDI_JMS_DEST_NEW_INSURANCE_COMPANY_PUSH = "insurance/jms/messaging/newInsuranceCompanyPush";
    public static final String JNDI_JMS_DEST_NEW_CALLBACK_COMPANY_PUSH = "insurance/jms/messaging/newCallbackCompanyPush";
    public static final String JNDI_JMS_DEST_REQUEST_PAID_COMPANY_EMAIL = "insurance/jms/messaging/requestPaidCompanyEmail";

    public static final String JNDI_JMS_DEST_PUSH_JOBS = "insurance/jms/messaging/pushJobs";

    public static final String PROPERTY_INSTANCE_VERB = "mesenger.instance.verb";
    public static final String PROPERTY_COMPANY_PUSH_CHANNEL_ID = "mesenger.push.channel-id";
    public static final String PROPERTY_COMPANY_PUSH_URL = "mesenger.push.url";
}
