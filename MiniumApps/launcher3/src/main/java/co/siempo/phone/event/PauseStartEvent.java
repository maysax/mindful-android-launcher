package co.siempo.phone.event;

/**
 * Created by Shahab on 3/2/2017.
 */

@SuppressWarnings("ALL")
public class PauseStartEvent {

    private int maxMillis;

    public PauseStartEvent(int maxMillis) {
        this.maxMillis = maxMillis;
    }

    public int getMaxMillis() {
        return maxMillis;
    }
}
