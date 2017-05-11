package co.siempo.phone.call;

/**
 * Created by Shahab on 5/11/2017.
 */

public class CallEvent {

    private CallEventType type;

    public CallEvent(CallEventType type) {
        this.type = type;
    }

    public CallEventType getType() {
        return type;
    }
}
