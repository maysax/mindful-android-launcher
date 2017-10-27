package co.siempo.phone.event;

import co.siempo.phone.SiempoNotificationBar.OnGoingCallData;

/**
 * Created by hardik on 25/10/17.
 */

public class OnGoingCallEvent {

    private OnGoingCallData callData;

    public OnGoingCallEvent(OnGoingCallData callData) {
        this.callData = callData;
    }

    public OnGoingCallData getCallData(){
        return callData;
    }
}
