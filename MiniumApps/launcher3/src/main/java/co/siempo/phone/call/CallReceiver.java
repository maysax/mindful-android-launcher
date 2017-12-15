package co.siempo.phone.call;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.lang.reflect.Method;
import java.util.Date;

import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.util.VibrationUtils;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.Tracer;

@EReceiver
public class CallReceiver extends co.siempo.phone.call.PhonecallReceiver {

    @SystemService
    TelephonyManager telephonyManager;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @Bean
    VibrationUtils vibration;


    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Tracer.d("onIncomingCallStarted()");
        if ((launcherPrefs.isPauseActive().get() && !launcherPrefs.isPauseAllowCallsChecked().get()) ||
                (launcherPrefs.isTempoActive().get() && !launcherPrefs.tempoAllowCalls().get())) {
            rejectCalls();
        }
    }


    @Override
    protected void onIncomingCallAnswered(Context context, String number, Date start) {
        Tracer.d("onOutgoingCallStarted()");
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Tracer.d("onOutgoingCallStarted()");
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Tracer.d("onIncomingCallEnded()");
        vibration.cancel();

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Tracer.d("onOutgoingCallEnded()");
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Tracer.d("onMissedCall()");

    }

    private void rejectCalls() {
        try {
            Class c = Class.forName(telephonyManager.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(telephonyManager);

            telephonyService.silenceRinger();
            telephonyService.endCall();

        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
            CoreApplication.getInstance().logException(e);
        }
    }
}
