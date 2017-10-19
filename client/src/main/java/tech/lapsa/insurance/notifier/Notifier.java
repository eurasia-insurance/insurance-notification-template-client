package tech.lapsa.insurance.notifier;

import javax.ejb.Local;

import com.lapsa.insurance.domain.Request;

@Local
public interface Notifier {

    default void assignRequestNotification(NotificationChannel channel, NotificationRecipientType recipientType,
	    NotificationRequestStage stage, Request request) {
	newNotificationBuilder() //
		.withChannel(channel) //
		.withEvent(stage) //
		.withRecipient(recipientType) //
		.forRequest(request) //
		.build() //
		.send();
    }

    NotificationBuilder newNotificationBuilder();

    interface NotificationBuilder {

	NotificationBuilder withChannel(NotificationChannel channel);

	NotificationBuilder withRecipient(NotificationRecipientType recipientType);

	NotificationBuilder withEvent(NotificationRequestStage stage);

	NotificationBuilder forRequest(Request request);

	Notification build();

	interface Notification {

	    void send();

	}

    }
}
