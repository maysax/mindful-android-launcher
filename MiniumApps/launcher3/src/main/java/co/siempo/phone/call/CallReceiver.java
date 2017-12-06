package co.siempo.phone.call;

import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.ITelephony;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import java.lang.reflect.Method;
import java.util.Date;
import co.siempo.phone.SiempoNotificationBar.OnGoingCallData;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.event.NewNotificationEvent;
import co.siempo.phone.event.OnGoingCallEvent;
import co.siempo.phone.notification.NotificationUtility;
import co.siempo.phone.util.VibrationUtils;
import de.greenrobot.event.EventBus;
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
        saveOnGoingCall(number, start,0,"Incoming call");
        if ((launcherPrefs.isPauseActive().get() && !launcherPrefs.isPauseAllowCallsChecked().get()) ||
                (launcherPrefs.isTempoActive().get() && !launcherPrefs.tempoAllowCalls().get())) {
            rejectCalls(ctx, number, start);
        }
        // Below logic will use in future
        /*else if (prefs.isNotificationSchedulerEnabled().get()) {
            if (prefs.notificationSchedulerSupressCalls().get()) {
                rejectCalls(ctx, number, start);
            } else {
                vibration.callVibration();
            }

        }*/
    }


    @Override
       protected void onIncomingCallAnswered(Context context, String number, Date start) {
        Tracer.d("onOutgoingCallStarted()");
        saveOnGoingCall(number, start,1 , "Ongoing call");
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Tracer.d("onOutgoingCallStarted()");
        saveOnGoingCall(number, start,3, "Ongoing call");
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Tracer.d("onIncomingCallEnded()");
        removeOngoingCall(number, start,2, "Ongoing call");
        vibration.cancel();
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Tracer.d("onOutgoingCallEnded()");
        removeOngoingCall(number, start,4, "Ongoing call");
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Tracer.d("onMissedCall()");
        removeOngoingCall(number, start, 5,"Ongoing call");
        saveCall(number, start);

    }

    private void rejectCalls(Context ctx, String number, Date start) {
        try {
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

    // Below code will use for further devlopment
    private void saveCall(String address, Date date) {
//        try {
//            TableNotificationSmsDao notificationSmsDao = DBUtility.getNotificationDao();
//
//            TableNotificationSms sms = new TableNotificationSms();
//            sms.set_contact_title(address);
//            sms.set_date(date);
//            sms.setNotification_date(System.currentTimeMillis());
//            sms.set_message(NotificationUtility.MISSED_CALL_TEXT);
//            sms.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_CALL);
//            long id = notificationSmsDao.insert(sms);
//            sms.setId(id);
//            EventBus.getDefault().post(new NewNotificationEvent(sms));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    // Keep this method as it is
    private void disconnectPhoneITelephony(Context context) {
        try {

            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Below code will use for further development
    private void saveOnGoingCall(String address, Date date, int id,String message) {
//        try {
//            OnGoingCallData onGoingCall = new OnGoingCallData();
//            onGoingCall.setId(id);
//            onGoingCall.set_contact_title(address);
//            onGoingCall.set_date(date);
//            onGoingCall.set_isCallRunning(true);
//            onGoingCall.set_message(message);
//            onGoingCall.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_ONGOING_CALL);
//            EventBus.getDefault().post(new OnGoingCallEvent(onGoingCall));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    // Below code will use for further development
    private void removeOngoingCall(String address, Date date, int id, String message){
//        try {
//            OnGoingCallData onGoingCall = new OnGoingCallData();
//            onGoingCall.setId(id);
//            onGoingCall.set_contact_title(address);
//            onGoingCall.set_date(date);
//            onGoingCall.set_isCallRunning(false);
//            onGoingCall.set_message(message);
//            onGoingCall.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_ONGOING_CALL);
//            EventBus.getDefault().post(new OnGoingCallEvent(onGoingCall));
//            }
//        catch (Exception e){
//            e.printStackTrace();
//            }
         }
}
