package co.siempo.phone.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;

import co.siempo.phone.app.Constants;
import co.siempo.phone.event.NotificationTrayEvent;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.utils.PackageUtil;
import de.greenrobot.event.EventBus;


public abstract class PhoneCallReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneCallReceiver";
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber = "";  //because the passed incoming is only valid in ringing
    private static boolean isCallRunning = false;
    public TelephonyManager telephonyManager;
    private int tempoType;
    private Context mContext;
    private int currentProfile = -1;
    private SharedPreferences sharedPref, droidPref;
    private boolean isAppDefaultOrFront = false;
    private AudioManager audioManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        mContext = context;
        sharedPref = context.getSharedPreferences("Launcher3Prefs", 0);
        droidPref = context.getSharedPreferences("DroidPrefs", 0);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int sound = 0;
        if (audioManager != null) {
            sound = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        }
        Tracer.d("VolumeCheck Call Coming", sound);
        changeSoundProfile(true);
        if (intent != null) {
            Log.d(TAG, "Phone Call Receiver :: " + intent.getAction() + currentProfile);
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                if (intent.getExtras() != null && intent.getExtras().containsKey("android.intent.extra.PHONE_NUMBER")) {
                    savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
                    sharedPref.edit().putBoolean(Constants.CALL_RUNNING, true).apply();
                    isCallRunning = true;
                    EventBus.getDefault().post(new NotificationTrayEvent(true));
                }
            } else {
                EventBus.getDefault().post(new NotificationTrayEvent(true));
                String stateStr = "", number = "";
                int state = 0;
                if (intent.getExtras() != null && intent.getExtras().containsKey(TelephonyManager.EXTRA_STATE)) {
                    stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                }
                if (intent.getExtras() != null && intent.getExtras().containsKey(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
                    number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                }
                if (stateStr != null) {
                    if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        state = TelephonyManager.CALL_STATE_IDLE;
                    } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        state = TelephonyManager.CALL_STATE_OFFHOOK;
                    } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        state = TelephonyManager.CALL_STATE_RINGING;
                    }
                }
                onCallStateChanged(context, state, number);
            }
        }
    }


    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        changeSoundProfile(false);
    }


    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    protected void onMissedCall(Context ctx, String number, Date start) {

    }

    protected void onIncomingCallAnswered(Context context, String number, Date start) {

    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    private void onCallStateChanged(Context context, int state, String number) {
        if (TextUtils.isEmpty(number)) {
            return;
        }
        if (lastState == state) {
            //No change, debounce extras

            return;
        }

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                isAppDefaultOrFront = sharedPref.getBoolean("isAppDefaultOrFront", false);
                if (isAppDefaultOrFront) {
                    if (currentProfile == 0 && !isCallRunning) {
                        changeSoundProfile(true);
                        sharedPref.edit().putBoolean(Constants.CALL_RUNNING, true).apply();
                        isCallRunning = true;
                    }
                }
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                changeSoundProfile(false);
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, number, callStartTime);
                } else {
                    isIncoming = true;
                    callStartTime = new Date();
                    onIncomingCallAnswered(context, savedNumber, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:

                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                } else {
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                sharedPref.edit().putBoolean(Constants.CALL_RUNNING, false).apply();
                changeSoundProfile(false);
                isCallRunning = false;
                break;
        }
        lastState = state;
    }

    private void changeSoundProfile(boolean isIncreaseSound) {
        if (PackageUtil.isSiempoLauncher(mContext)) {
            tempoType = droidPref.getInt("tempoType", 0);
            if (isIncreaseSound) {
                Tracer.d("VolumeCheck Call Coming When call comes");
                if (tempoType == 1 || tempoType == 2) {
                    int sound = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
                    int soundMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
                    Tracer.d("VolumeCheck Call Coming + user sound", sound);
                    Tracer.d("VolumeCheck Call Coming + max sound", soundMax);
                    if (sound == 1
                            && (audioManager.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE || audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT)) {
                        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, soundMax, 0);
                        Tracer.d("VolumeCheck Call Coming Update Sound");
                    }
                }
            } else {
                Tracer.d("VolumeCheck Call Coming When call disconnected or miscall");
                if (tempoType == 1 || tempoType == 2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 1, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    Tracer.d("VolumeCheck Call Coming Update Sound");
                }
            }
        }
    }
}
