package co.siempo.phone.service;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.view.accessibility.AccessibilityEvent;

import org.androidannotations.annotations.SystemService;

import co.siempo.phone.util.PackageUtil;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;


public class SiempoAccessibilityService extends AccessibilityService {

    public static String packageName = "";

    AudioManager audioManager;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        if (event.getEventType() == TYPE_WINDOW_STATE_CHANGED) {
            ComponentName componentName = new ComponentName(event.getPackageName().toString(), event.getClassName().toString());
            ActivityInfo activityInfo = getActivityInfo(componentName);
            boolean isActivity = activityInfo != null;
            if (isActivity) {
                packageName = activityInfo.packageName;
                String activityName = componentName.flattenToShortString();
            }

            if (!PackageUtil.isSiempoLauncher(this) && !packageName.equalsIgnoreCase(getPackageName())) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
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