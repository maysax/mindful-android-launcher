package co.siempo.phone.receivers;

import android.content.Context;

import java.lang.reflect.Method;
import java.util.Date;

import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.interfaces.ITelephony;
import co.siempo.phone.log.Tracer;

public class CallReceiver extends PhoneCallReceiver {


    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Tracer.i("onIncomingCallStarted()");

    }


    @Override
    protected void onIncomingCallAnswered(Context context, String number, Date start) {
        Tracer.i("onOutgoingCallStarted()");
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Tracer.i("onOutgoingCallStarted()");
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Tracer.i("onIncomingCallEnded()");
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Tracer.i("onOutgoingCallEnded()");
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Tracer.i("onMissedCall()");

    }

//    private void rejectCalls() {
//        try {
//            Class c = Class.forName(telephonyManager.getClass().getName());
//            Method m = c.getDeclaredMethod("getITelephony");
//            m.setAccessible(true);
//            ITelephony telephonyService = (ITelephony) m.invoke(telephonyManager);
//
//            telephonyService.silenceRinger();
//            telephonyService.endCall();
//
//        } catch (Exception e) {
//            Tracer.e(e, e.getMessage());
//            CoreApplication.getInstance().logException(e);
//        }
//    }
}
