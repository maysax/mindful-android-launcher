package co.siempo.phone.event;

/**
 * Created by Shahab on 8/12/2016.
 * Used for the tourch enable/disable.
 */
public class AppInstalledEvent {

    private boolean isAppInstalled;
    private int installedOrRemoved;

    public AppInstalledEvent(int installedOrRemoved) {
        this.installedOrRemoved = installedOrRemoved;
    }

    public AppInstalledEvent(boolean isAppInstalled) {
        this.isAppInstalled = isAppInstalled;
    }

    public boolean isAppInstalledSuccessfully() {
        return isAppInstalled;
    }

    public int getInstalledOrRemoved() {
        return installedOrRemoved;
    }
}
