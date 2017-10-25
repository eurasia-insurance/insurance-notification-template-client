package tech.lapsa.insurance.notifier.beans;

import java.io.InputStream;
import java.util.Locale;

import tech.lapsa.java.commons.localization.LocalizedElement;

public enum NotificationTemplates implements LocalizedElement {
    NEW_CASCO_COMPANY_EMAIL_TEMPLATE, //
    NEW_CASCO_USER_EMAIL_TEMPLATE, //
    NEW_POLICY_COMPANY_EMAIL_TEMPLATE, //
    NEW_POLICY_USER_EMAIL_TEMPLATE, //
    INUSNRANCE_PAID_COMPANY_EMAIL_TEMPLATE, //
    //
    ;

    public InputStream getResourceAsStream(Locale locale) {

	InputStream result = null;
	String name = regular(locale);

	while (name.startsWith("/")) {
	    name = name.substring(1);
	}

	ClassLoader classLoader = Thread.currentThread()
		.getContextClassLoader();

	if (classLoader == null) {
	    classLoader = this.getClass().getClassLoader();
	    result = classLoader.getResourceAsStream(name);
	} else {
	    result = classLoader.getResourceAsStream(name);

	    if (result == null) {
		classLoader = this.getClass().getClassLoader();
		if (classLoader != null)
		    result = classLoader.getResourceAsStream(name);
	    }
	}
	return result;
    }
}
