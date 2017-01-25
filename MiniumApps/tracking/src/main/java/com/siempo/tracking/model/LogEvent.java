package com.siempo.tracking.model;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Shahab on 1/20/2017.
 */

public class LogEvent {
    private String time;
    private String name;
    private Data data;

    public enum EventType {
        STARTED, SCREEN_TAP, SCREEN_ON, POWER, HOME, APP_USAGE, NOTIFICATION
    }

    private LogEvent() {
        time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.US).format(System.currentTimeMillis());
        data = new Data();
    }

    public LogEvent(EventType type) {
        this();

        switch (type) {

            case STARTED:
                name = "Tracking started";
                break;
            case SCREEN_TAP:
                name = "Touch event";
                break;
            case SCREEN_ON:
                name = "Screen event";
                break;
            case POWER:
                name = "Power event";
                break;
            case HOME:
                name = "Home event";
                break;
            case APP_USAGE:
                name = "App usage event";
                break;
            case NOTIFICATION:
                name = "Notification event";
                break;
        }
    }




    public class Data {
        private String lastUsed;
        private String effect;
        private String usage;

        public Data() {
        }

        public Data(String lastUsed, String effect, String usage) {
            this.lastUsed = lastUsed;
            this.effect = effect;
            this.usage = usage;
        }

        public String getLastUsed() {
            return lastUsed;
        }

        public String getEffect() {
            return effect;
        }

        public String getUsage() {
            return usage;
        }
    }
}


