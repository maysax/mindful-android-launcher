package co.minium.launcher3.notification;

/**
 * Created by itc on 20/02/17.
 */

public class Notification {
    private String _text;
    private boolean _status;
    private String _time;
    private NotificationContactModel notificationContactModel;
    private int notificationType;
    public Notification(NotificationContactModel notificationContactModel, String _text, String _time, boolean _status) {
        this.notificationContactModel = notificationContactModel;
        this._text = _text;
        this._status = _status;
        this._time = _time;
    }

    public String get_text() {
        return _text;
    }

    public void set_text(String _text) {
        this._text = _text;
    }

    public boolean is_status() {
        return _status;
    }

    public void set_status(boolean _status) {
        this._status = _status;
    }

    public String get_time() {
        return _time;
    }

    public void set_time(String _time) {
        this._time = _time;
    }

    public NotificationContactModel getNotificationContactModel() {
        return notificationContactModel;
    }

    public void setNotificationContactModel(NotificationContactModel notificationContactModel) {
        this.notificationContactModel = notificationContactModel;
    }
}
