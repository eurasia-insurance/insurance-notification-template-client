package tech.lapsa.insurance.notifier;

import javax.ejb.Local;

@Local
public interface Notifier {

    void send(Notification notification);
}
