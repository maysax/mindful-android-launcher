package co.siempo.phone.battery;

/**
 * Created by shahab on 3/16/16.
 */
public class BatteryChangeEvent {

    private int level;

    public BatteryChangeEvent(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
