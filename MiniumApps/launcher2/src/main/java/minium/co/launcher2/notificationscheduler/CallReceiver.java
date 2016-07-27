package minium.co.launcher2.notificationscheduler;

import android.content.Context;

import org.androidannotations.annotations.EReceiver;

import java.util.Date;

import minium.co.core.log.Tracer;

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
    }
}
