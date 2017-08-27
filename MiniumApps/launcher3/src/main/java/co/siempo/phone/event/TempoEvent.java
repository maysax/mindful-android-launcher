package co.siempo.phone.event;

/**
 * Created by Shahab on 2/26/2017.
 */

@SuppressWarnings("ALL")
public class TempoEvent {

    private boolean isStarting;

    public TempoEvent(boolean isStarting) {
        this.isStarting = isStarting;
    }

    public boolean isStarting() {
        return isStarting;
    }
}
