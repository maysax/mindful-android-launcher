package com.siempo.tracking.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shahab on 1/20/2017.
 */

public class LogEvent {
    protected String time;
    protected String name;

    public class Data {
        protected String lastUsed;
        protected String effect;
        protected String usage;

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


