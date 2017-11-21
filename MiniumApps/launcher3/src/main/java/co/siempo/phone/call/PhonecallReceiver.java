package co.siempo.phone.call;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;

import co.siempo.phone.event.NotificationTrayEvent;
import de.greenrobot.event.EventBus;
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
    private static final String TAG = "PhoneCallReceiver";
    int currentProfile = -1;
    AudioManager audioManager;
    NotificationManager notificationManager;
    public static boolean isCallRunning = false;
    SharedPreferences sharedPref;
    boolean isAppDefaultOrFront = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        sharedPref =
                context.getSharedPreferences("Launcher3Prefs", 0);
        currentProfile = sharedPref.getInt("getCurrentProfile", 0);

        if (intent != null) {
            Log.d(TAG, "Phone Call Receiver :: " + intent.getAction() + currentProfile);
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                if (intent.getExtras() != null && intent.getExtras().containsKey("android.intent.extra.PHONE_NUMBER")) {
                    savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
                    isCallRunning = true;
                    EventBus.getDefault().post(new NotificationTrayEvent(true));
                    CoreApplication.getInstance().changeProfileToNormalMode();
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
        if (CoreApplication.getInstance().getMediaPlayer() != null) {
            CoreApplication.getInstance().getMediaPlayer().stop();
            CoreApplication.getInstance().setmMediaPlayer(null);
            CoreApplication.getInstance().getVibrator().cancel();
        }
    }

    private void changeDeviceMode() {
        isAppDefaultOrFront = sharedPref.getBoolean("isAppDefaultOrFront", false);
        if (isAppDefaultOrFront) {
            int currentModeDeviceMode = sharedPref.getInt("getCurrentProfile", 0);
            if (currentModeDeviceMode == 0) {
                CoreApplication.getInstance().changeProfileToSilentMode();
            } else if (currentModeDeviceMode == 1) {
                CoreApplication.getInstance().changeProfileToVibrateMode();
            } else if (currentModeDeviceMode == 2) {
                CoreApplication.getInstance().changeProfileToSilentMode();
            }
            Log.d(TAG, "changeDeviceMode : currentModeDeviceMode - isAppDefaultOrFront" + currentModeDeviceMode + " :: isAppDefaultOrFront " + isAppDefaultOrFront);
        } /*else {
            Log.d(TAG, "changeDeviceMode : currentModeDeviceMode - isAppDefaultOrFront -1 :: isAppDefaultOrFront " + isAppDefaultOrFront);
            CoreApplication.getInstance().changeProfileToNormalMode();
        }*/

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
    public void onCallStateChanged(Context context, int state, String number) {
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
                CoreApplication.getInstance().setCallisRunning(true);
                callStartTime = new Date();
                savedNumber = number;
                isAppDefaultOrFront = sharedPref.getBoolean("isAppDefaultOrFront", false);
                if (isAppDefaultOrFront) {
                    if (currentProfile == 0 && !isCallRunning) {
                        CoreApplication.getInstance().playAudio();
                        isCallRunning = true;
                    }
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
                    CoreApplication.getInstance().setCallisRunning(false);
                    onOutgoingCallStarted(context, number, callStartTime);
                } else {
                    isIncoming = true;
                    callStartTime = new Date();
                    CoreApplication.getInstance().setCallisRunning(true);
                    onIncomingCallAnswered(context, savedNumber, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:

                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    CoreApplication.getInstance().setCallisRunning(false);
                    onMissedCall(context, savedNumber, callStartTime);
                    changeDeviceMode();
                } else if (isIncoming) {
                    CoreApplication.getInstance().setCallisRunning(false);
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                } else {
                    CoreApplication.getInstance().setCallisRunning(false);
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                    changeDeviceMode();
                }
                if (CoreApplication.getInstance().getMediaPlayer() != null) {
                    CoreApplication.getInstance().getMediaPlayer().stop();
                    CoreApplication.getInstance().setmMediaPlayer(null);
                    CoreApplication.getInstance().getVibrator().cancel();
                }
                isCallRunning = false;
                break;
        }
        lastState = state;
    }
}
