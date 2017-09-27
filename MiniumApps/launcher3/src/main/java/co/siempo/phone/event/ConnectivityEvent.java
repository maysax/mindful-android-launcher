package co.siempo.phone.event;

/**
 * Created by Shahab on 5/2/2017.
 */


public class ConnectivityEvent {
    public static final int AIRPLANE = 0;
    public static final int WIFI = 1;
    public static final int NETWORK = 2;
    public static final int BATTERY = 3;
    public static final int BLE = 4;
    public static final int DND = 5;

    private int state;
    private int value;
    private String type;

    public ConnectivityEvent(int state, int value) {
        this.state = state;
        this.value = value;
    }

    public ConnectivityEvent(int state, int value,String type) {
        this.state = state;
        this.value = value;
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public int getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
