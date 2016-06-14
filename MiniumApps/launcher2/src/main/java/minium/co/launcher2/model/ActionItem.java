package minium.co.launcher2.model;

/**
 * Created by Shahab on 6/6/2016.
 */
public enum ActionItem {

    CALL ("Call", true, true, ""),
    TEXT ("Text", true, true, ""),
    NOTE ("Note", true, true, ""),
    CONTACT ("", true, false, ""),
    EMPTY("", false, false, ""),
    DATA ("", false, false, "");

    private boolean isChips;

    private String actionText;

    private String extra;

    private boolean isCompleted;

    ActionItem(String actionText, boolean isChips, boolean isCompleted, String extra) {
        this.actionText = actionText;
        this.isChips = isChips;
        this.isCompleted = isCompleted;
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

    public ActionItem setExtra(String extra) {
        this.extra = extra;
        return this;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public ActionItem setActionText(String actionText) {
        this.actionText = actionText;
        return this;
    }

    public void addActionText(char ch) {
        this.actionText += ch;
    }

    public void removeActionText() {
        this.actionText = actionText.substring(0, this.actionText.length() - 1);
    }
}
