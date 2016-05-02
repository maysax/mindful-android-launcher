package minium.co.launcher2.events;

/**
 * Created by Shahab on 5/2/2016.
 */
public class SearchTextChangedEvent {

    String text;

    public SearchTextChangedEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
