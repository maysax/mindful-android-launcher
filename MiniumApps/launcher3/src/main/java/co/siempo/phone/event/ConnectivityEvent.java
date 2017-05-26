package co.siempo.phone.event;

/**
 * Created by Shahab on 5/2/2017.
 */

public class ConnectivityEvent {
    public static final int AIRPLANE = 0;
    public static final int WIFI = 1;
    public static final int NETWORK = 2;
    public static final int BATTERY = 3;

    private int state;
    private int value;

    public ConnectivityEvent(int state, int value) {
        this.state = state;
        this.value = value;
    }

    public int getState() {
        return state;
    }

    public int getValue() {
        return value;
    }
}
