package co.siempo.phone.event;

/**
 * Created by Shahab on 3/2/2017.
 */

@SuppressWarnings("ALL")
public class MindfulMorgingEventStart {

    private int startPosition;

    public MindfulMorgingEventStart(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getStartPosition() {
        return startPosition;
    }
}
