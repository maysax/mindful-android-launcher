package co.siempo.phone.msg;

/**
 * Created by Shahab on 3/21/2017.
 */

@SuppressWarnings("ALL")
public class SmsEvent {

    private SmsEventType type;

    public SmsEvent(SmsEventType type) {
        this.type = type;
    }

    public SmsEventType getType() {
        return type;
    }
}
