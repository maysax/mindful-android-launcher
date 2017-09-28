package co.siempo.phone.event;

/**
 * Created by Shahab on 8/12/2016.
 * Used for the tourch enable/disable.
 */
public class TourchOnOff {

    private boolean isRunning;

    public TourchOnOff(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
