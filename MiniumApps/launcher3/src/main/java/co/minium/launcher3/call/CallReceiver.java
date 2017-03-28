package co.minium.launcher3.call;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.lang.reflect.Method;
import java.util.Date;

import co.minium.launcher3.app.Launcher3App;
import co.minium.launcher3.app.Launcher3Prefs_;
import co.minium.launcher3.db.DaoSession;
import co.minium.launcher3.db.TableNotificationSms;
import co.minium.launcher3.util.VibrationUtils;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;

@EReceiver
public class CallReceiver extends PhonecallReceiver {

    @SystemService
    TelephonyManager telephonyManager;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @Bean
    VibrationUtils vibration;

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Tracer.d("onIncomingCallStarted()");


        if(launcherPrefs.isPauseActive().get())
            rejectCalls(ctx, number, start);
        /*else if (prefs.isNotificationSchedulerEnabled().get()) {
            if (prefs.notificationSchedulerSupressCalls().get()) {
                rejectCalls(ctx, number, start);
            } else {
                vibration.callVibration();
            }

        }*/
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
        saveCall(number, start);
    }

    private void rejectCalls(Context ctx, String number, Date start) {
        try {
            //new MissedCallItem(number, start, 0).save();

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

    private void saveCall(String address, Date date) {
        DaoSession daoSession = ((Launcher3App) CoreApplication.getInstance()).getDaoSession();
        CallStorageDao callStorageDao = daoSession.getCallStorageDao();

        CallStorage sms = new CallStorage();
        sms.setTitle(address);
        sms.set_date(date);
        callStorageDao.insert(sms);
    }
}
