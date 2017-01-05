package com.itconquest.tracking.event;

/**
 * Created by Shahab on 1/5/2017.
 */

public class CheckVersionEvent {

    private int version;

    public CheckVersionEvent(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
