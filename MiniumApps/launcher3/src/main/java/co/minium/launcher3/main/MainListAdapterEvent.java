package co.minium.launcher3.main;

/**
 * Created by Shahab on 2/17/2017.
 */

public class MainListAdapterEvent {

    private int dataSize;

    public MainListAdapterEvent(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getDataSize() {
        return dataSize;
    }
}
