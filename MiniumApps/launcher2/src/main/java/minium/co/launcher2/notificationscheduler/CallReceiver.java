package minium.co.launcher2.notificationscheduler;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.lang.reflect.Method;
import java.util.Date;

import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.launcher2.model.MissedCallItem;

@EReceiver
public class CallReceiver extends PhonecallReceiver {

    @SystemService
    TelephonyManager telephonyManager;

    @Pref
    DroidPrefs_ prefs;

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Tracer.d("onIncomingCallStarted()");

        if(prefs.isFlowRunning().get() || prefs.isNotificationSchedulerEnabled().get())
            rejectCalls(ctx, number, start);
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

    private void rejectCalls(Context ctx, String number, Date start) {
        try {
            new MissedCallItem(number, start, 0).save();

            Class c = Class.forName(telephonyManager.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(telephonyManager);

            telephonyService.silenceRinger();
            telephonyService.endCall();

        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }
}
