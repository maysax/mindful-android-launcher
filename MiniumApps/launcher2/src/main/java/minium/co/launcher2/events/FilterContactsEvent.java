package minium.co.launcher2.events;

/**
 * Created by Shahab on 5/2/2016.
 */
public class FilterContactsEvent {

    String text;

    public FilterContactsEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
