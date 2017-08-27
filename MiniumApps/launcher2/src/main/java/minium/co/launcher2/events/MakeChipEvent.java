package minium.co.launcher2.events;

/**
 * Created by Shahab on 5/2/2016.
 */
public class MakeChipEvent {

    private int start;
    private int end;
    private String text;

    public MakeChipEvent(int start, int end, String text) {
        this.start = start;
        this.end = end;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
