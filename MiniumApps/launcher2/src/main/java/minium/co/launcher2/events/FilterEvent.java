package minium.co.launcher2.events;

/**
 * Created by Shahab on 7/12/2016.
 */
public class FilterEvent {
    private String txt;

    public FilterEvent(String txt) {
        this.txt = txt;
    }

    public String getText() {
        return txt;
    }
}
