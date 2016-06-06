package minium.co.launcher2.model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Shahab on 6/6/2016.
 */
public enum ActionItem {

    CALL ("Call", true, ""),
    TEXT ("Text", true, ""),
    NOTE ("Note", true, ""),
    CONTACT ("Contact", true, ""),
    DATA ("Data", false, "");

    private boolean isChips;

    private String actionText;

    private String extra;


    ActionItem(String actionText, boolean isChips, String extra) {
        this.actionText = actionText;
        this.isChips = isChips;
        this.extra = extra;
    }

    public boolean isChips() {
        return isChips;
    }

    public String getActionText() {
        return actionText;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
