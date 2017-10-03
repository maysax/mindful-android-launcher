package minium.co.core.event;

/**
 * Created by Shahab on 8/12/2016.
 * Used for the tourch enable/disable.
 */
public class AppInstalledEvent {

    private boolean isAppInstalled;

    public AppInstalledEvent(boolean isAppInstalled) {
        this.isAppInstalled = isAppInstalled;
    }

    public boolean isRunning() {
        return isAppInstalled;
    }
}
