package co.minium.launcher3.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.lang.reflect.Method;

import co.minium.launcher3.app.Launcher3Prefs_;

/**
 * Created by Shahab on 3/28/2017.
 */
@EReceiver
public class PhoneStateReceiver extends BroadcastReceiver {
    private String blockingNumber = "XX-XXX-XXXXX";

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        //blocking sms for matched number

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();
            Object messages[] = (Object[]) bundle.get("pdus");
            SmsMessage smsMessage[] = new SmsMessage[messages.length];

            for (int n = 0; n < messages.length; n++) {
                smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
            }

            final String numberSms = smsMessage[0].getOriginatingAddress();
            final String messageSms = smsMessage[0].getDisplayMessageBody();
            long dateTimeSms = smsMessage[0].getTimestampMillis();

//block sms if number is matched to our blocking number
            if(launcherPrefs.isPauseActive().get()){
                abortBroadcast();
            }
        }

        else if (intent.getAction().equals("android.intent.action.PHONE_STATE")){
            final String numberCall = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);;

//reject call if number is matched to our blocking number
            //if(numberCall.equals(blockingNumber)){
            //    disconnectPhoneItelephony(context);
            //}
        }
    }

    // Keep this method as it is
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void disconnectPhoneItelephony(Context context) {
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
}
