package co.siempo.phone.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;

import co.siempo.phone.R;
import co.siempo.phone.service.SiempoAccessibilityService;
import co.siempo.phone.util.PackageUtil;
import minium.co.core.app.CoreApplication;

/**
 * Created by Shahab on 7/27/2016.
 */

public abstract class PhonecallReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber = "";  //because the passed incoming is only valid in ringing

    int currentProfile = -1;
    AudioManager audioManager;
    static boolean isCallisRunning = false;
    SharedPreferences sharedPref;

    @Override
    public void onReceive(Context context, Intent intent) {
        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        sharedPref =
                context.getSharedPreferences("Launcher3Prefs", 0);
        currentProfile = sharedPref.getInt("getCurrentProfile", 0);

        if (intent != null) {
            Log.d("Raja", "Raja" + intent.getAction() + currentProfile);
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                if (intent.getExtras() != null && intent.getExtras().containsKey("android.intent.extra.PHONE_NUMBER")) {
                    savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
                    isCallisRunning = true;
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            } else {
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
                Log.d("Testing State", "" + state);
                onCallStateChanged(context, state, number);
            }
        }
    }


    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        if (CoreApplication.getInstance().getMediaPlayer() != null) {
            CoreApplication.getInstance().getMediaPlayer().stop();
            CoreApplication.getInstance().setmMediaPlayer(null);
            CoreApplication.getInstance().getVibrator().cancel();
        }
    }

    private void changeDeviceMode(Context context) {
        if (PackageUtil.isSiempoLauncher(context)
                || SiempoAccessibilityService.packageName.equalsIgnoreCase(context.getPackageName())) {
            int currentModeDeviceMode = sharedPref.getInt("getCurrentProfile", 0);
            if (currentModeDeviceMode == 0) {
                sharedPref.edit().putInt("getCurrentProfile", 1).apply();
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            } else if (currentModeDeviceMode == 1) {
                sharedPref.edit().putInt("getCurrentProfile", 2).apply();
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else if (currentModeDeviceMode == 2) {
                sharedPref.edit().putInt("getCurrentProfile", 0).apply();
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        }else{
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }

    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    protected void onMissedCall(Context ctx, String number, Date start) {

    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                if (currentProfile == 0 && !isCallisRunning) {
                    CoreApplication.getInstance().playAudio();
                    isCallisRunning = true;
                }
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (CoreApplication.getInstance().getMediaPlayer() != null) {
                    CoreApplication.getInstance().getMediaPlayer().stop();
                    CoreApplication.getInstance().setmMediaPlayer(null);
                    CoreApplication.getInstance().getVibrator().cancel();
                }
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
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
                    changeDeviceMode(context);
                }
                if (CoreApplication.getInstance().getMediaPlayer() != null) {
                    CoreApplication.getInstance().getMediaPlayer().stop();
                    CoreApplication.getInstance().setmMediaPlayer(null);
                    CoreApplication.getInstance().getVibrator().cancel();
                }
                isCallisRunning = false;
                break;
        }
        lastState = state;
    }
}
