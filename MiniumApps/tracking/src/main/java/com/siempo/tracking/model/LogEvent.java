package com.siempo.tracking.model;

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

    public LogEvent setEffect(String effect) {
        data.setEffect(effect);
        return this;
    }

    public LogEvent setUsage(String usage) {
        data.setUsage(usage);
        return this;
    }

    public LogEvent setLastUsed(String lastUsed) {
        data.setLastUsed(lastUsed);
        return this;
    }

    public LogEvent setPkg(String pkg) {
        data.setPkg(pkg);
        return this;
    }

    public LogEvent setPostedOn(String postedOn) {
        data.setPostedOn(postedOn);
        return this;
    }

    public LogEvent setText(CharSequence text) {
        data.setText(text == null ? "" : text.toString());
        return this;
    }

    public LogEvent setDuration(String duration) {
        data.setDuration(duration);
        return this;
    }

    public LogEvent setFrom(String from) {
        data.setFrom(from);
        return this;
    }

    public LogEvent setTo(String to) {
        data.setTo(to);
        return this;
    }

    public class Data {
        private String lastUsed;
        private String effect;
        private String usage;
        private String pkg;
        private String postedOn;
        private String text;
        private String duration;
        private String from;
        private String to;

        public Data() {
        }

        public String getLastUsed() {
            return lastUsed;
        }

        public void setLastUsed(String lastUsed) {
            this.lastUsed = lastUsed;
        }

        public String getEffect() {
            return effect;
        }

        public void setEffect(String effect) {
            this.effect = effect;
        }

        public String getUsage() {
            return usage;
        }

        public void setUsage(String usage) {
            this.usage = usage;
        }

        public String getPkg() {
            return pkg;
        }

        public void setPkg(String pkg) {
            this.pkg = pkg;
        }

        public String getPostedOn() {
            return postedOn;
        }

        public void setPostedOn(String postedOn) {
            this.postedOn = postedOn;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }
    }
}


