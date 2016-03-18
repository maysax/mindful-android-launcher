package minium.co.launcher.battery;

/**
 * Created by shahab on 3/16/16.
 */
public class BatteryChangeEvent {

    private float batteryPct;

    public BatteryChangeEvent(float batteryPct) {
        this.batteryPct = batteryPct;
    }

    public float getBatteryPct() {
        return batteryPct;
    }
}
