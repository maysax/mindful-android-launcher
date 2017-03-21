package co.minium.launcher3.event;

/**
 * Created by Shahab on 3/2/2017.
 */

public class MindfulMorgingEventStart {

    private int maxMillis;

    public MindfulMorgingEventStart(int maxMillis) {
        this.maxMillis = maxMillis;
    }

    public int getMaxMillis() {
        return maxMillis;
    }
}
