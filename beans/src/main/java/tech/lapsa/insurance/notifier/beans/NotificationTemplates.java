package tech.lapsa.insurance.notifier.beans;

import java.io.InputStream;
import java.util.Locale;

import tech.lapsa.java.commons.localization.LocalizedElement;
import tech.lapsa.java.commons.resources.Resources;

public enum NotificationTemplates implements LocalizedElement {
    NEW_CASCO_COMPANY_EMAIL_TEMPLATE, //
    NEW_CASCO_USER_EMAIL_TEMPLATE, //
    NEW_POLICY_COMPANY_EMAIL_TEMPLATE, //
    NEW_POLICY_USER_EMAIL_TEMPLATE, //
    INUSNRANCE_PAID_COMPANY_EMAIL_TEMPLATE, //
    //
    ;

    public InputStream getResourceAsStream(Locale locale) {
	return Resources.getAsStream(this.getClass(), regular(locale));
    }
}
