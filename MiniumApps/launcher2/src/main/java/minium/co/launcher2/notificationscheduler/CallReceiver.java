package minium.co.launcher2.notificationscheduler;

import android.content.Context;

import org.androidannotations.annotations.EReceiver;

import java.util.Date;

import minium.co.core.log.Tracer;
import minium.co.launcher2.model.MissedCallItem;

@EReceiver
public class CallReceiver extends PhonecallReceiver {

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Tracer.d("onIncomingCallStarted()");
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Tracer.d("onOutgoingCallStarted()");
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Tracer.d("onIncomingCallEnded()");
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Tracer.d("onOutgoingCallEnded()");
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Tracer.d("onMissedCall()");
        MissedCallItem callItem = new MissedCallItem(number, start, 0);
        callItem.save();
    }
}
