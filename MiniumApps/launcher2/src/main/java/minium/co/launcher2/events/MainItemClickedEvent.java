package minium.co.launcher2.events;

/**
 * Created by Shahab on 4/29/2016.
 */
public class MainItemClickedEvent {

    String text;
    int position;

    public MainItemClickedEvent(String text, int position) {
        this.text = text;
        this.position = position;
    }

    public String getText() {
        return text;
    }

    public int getPosition() {
        return position;
    }
}
