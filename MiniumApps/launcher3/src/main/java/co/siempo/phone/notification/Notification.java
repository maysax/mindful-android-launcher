package co.siempo.phone.notification;

/**
 * Created by itc on 20/02/17.
 */

public class Notification {
    private Long id;
    private String _text;
    private boolean _status;
    private String _time;
    private NotificationContactModel notificationContactModel;
    private int notificationType;
    private String number;

    public Notification(NotificationContactModel notificationContactModel, Long id, String number, String _text, String _time, boolean _status, int notificationType) {
        this.notificationContactModel = notificationContactModel;
        this.id = id;
        this._text = _text;
        this._status = _status;
        this._time = _time;
        this.notificationType = notificationType;
        this.number = number;
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

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
