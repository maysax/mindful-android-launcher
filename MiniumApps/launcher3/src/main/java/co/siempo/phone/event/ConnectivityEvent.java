package co.siempo.phone.event;

/**
 * Created by Shahab on 5/2/2017.
 */

public class ConnectivityEvent {
    public static final int AIRPLANE = 0;
    public static final int WIFI = 1;

    private int state;

    public ConnectivityEvent(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
