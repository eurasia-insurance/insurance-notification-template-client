package tech.lapsa.insurance.notifier.beans;

import java.io.InputStream;
import java.util.Locale;

import tech.lapsa.java.commons.io.MyResources;
import tech.lapsa.java.commons.localization.LocalizedElement;

public enum NotificationTemplates implements LocalizedElement {
    NEW_CASCO_COMPANY_EMAIL_TEMPLATE, //
    NEW_CASCO_USER_EMAIL_TEMPLATE, //
    NEW_POLICY_COMPANY_EMAIL_TEMPLATE, //
    NEW_POLICY_USER_EMAIL_TEMPLATE, //
    INUSNRANCE_PAID_COMPANY_EMAIL_TEMPLATE, //
    //
    ;

    public InputStream getResourceAsStream(final Locale locale) {
	return MyResources.getAsStream(this.getClass(), regular(locale));
    }
}
