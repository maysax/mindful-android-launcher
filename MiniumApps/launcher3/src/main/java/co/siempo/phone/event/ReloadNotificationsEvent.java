package co.siempo.phone.event;


public class ReloadNotificationsEvent {

    private boolean isReload;

    public ReloadNotificationsEvent(boolean isReload) {
        this.isReload = isReload;
    }

    public boolean isStarting() {
        return isReload;
    }
}
