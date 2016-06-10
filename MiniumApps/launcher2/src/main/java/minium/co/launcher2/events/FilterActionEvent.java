package minium.co.launcher2.events;

/**
 * Created by Shahab on 5/2/2016.
 */
public class FilterActionEvent {

    String text;

    public FilterActionEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
