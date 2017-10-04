package co.siempo.phone.event;

/**
 * Created by Rajesh Jadi on 8/12/2016.
 * Used for the torch enable/disable.
 */
public class TorchOnOff {

    private boolean isRunning;

    public TorchOnOff(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
