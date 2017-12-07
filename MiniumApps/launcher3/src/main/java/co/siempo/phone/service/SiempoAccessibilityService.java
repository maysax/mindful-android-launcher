package co.siempo.phone.service;

import android.accessibilityservice.AccessibilityService;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;


public class SiempoAccessibilityService extends AccessibilityService {

    public String packageName = "";
    public static String activityName = "";
    private final String TAG = "Accessibility";

    AudioManager audioManager;
    private NotificationManager notificationManager;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (event != null && event.getPackageName() != null && event.getClass() != null) {
            if (event.getEventType() == TYPE_WINDOW_STATE_CHANGED) {
                try {
                    ComponentName componentName = new ComponentName(event.getPackageName().toString(), event.getClassName().toString());
                    ActivityInfo activityInfo = getActivityInfo(componentName);
                    boolean isActivity = activityInfo != null;
                    if (isActivity) {
                        packageName = activityInfo.packageName;
                        activityName = componentName.flattenToShortString();
                    }
                    Log.d(TAG, "Package Name::" + packageName);
                    Log.d(TAG, "Activity Name::" + activityName);
                } catch (Exception e) {
                    e.printStackTrace();
                }


//                if (!PackageUtil.isSiempoLauncher(this) && !packageName.equalsIgnoreCase(getPackageName())) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
//                            && !notificationManager.isNotificationPolicyAccessGranted()) {
//                    } else {
//                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//                    }
//                }

            }
        }
    }

    @Override
    public void onInterrupt() {

    }


    private ActivityInfo getActivityInfo(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

}