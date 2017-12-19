package co.siempo.phone.util;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;


import co.siempo.phone.service.SiempoNotificationListener_;
import minium.co.core.log.Tracer;
import minium.co.core.util.ServiceUtils;

/**
 * Created by Shahab on 1/12/2017.
 */

public class PermissionUtil {

    public static final int NOTIFICATION_ACCESS = 0;
    public static final int USAGE_STATISTICS = 1;
    public static final int DRAWING_OVER_OTHER_APPS = 2;
    public static final int APP_PERMISSION = 3;
    public static final int CONTACT_PERMISSION = 4;
    public static final int CALL_PHONE_PERMISSION = 5;
    public static final int SEND_SMS_PERMISSION = 6;
    public static final int CAMERA_PERMISSION = 7;
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 8;

    private Context context;

    public PermissionUtil(Context context) {
        this.context = context;
    }

    public boolean isAllPermissionGiven() {

        return hasAppPermissions()
                && canDrawOverlays()
                && isEnabled()
                && checkUserStatPermission();
    }

    public boolean hasGiven(int permission) {
        switch (permission) {
            case NOTIFICATION_ACCESS:
                return isEnabled();
            case USAGE_STATISTICS:
                return checkUserStatPermission();
            case DRAWING_OVER_OTHER_APPS:
                return canDrawOverlays();
            case APP_PERMISSION:
                return hasAppPermissions();
            case CONTACT_PERMISSION:
                return hasAppPermissions(Manifest.permission.READ_CONTACTS);
            case CALL_PHONE_PERMISSION:
                return hasAppPermissions(Manifest.permission.CALL_PHONE);
            case SEND_SMS_PERMISSION:
                return hasAppPermissions(Manifest.permission.SEND_SMS);
            case CAMERA_PERMISSION:
                return hasAppPermissions(Manifest.permission.CAMERA);
            case WRITE_EXTERNAL_STORAGE_PERMISSION:
                return hasAppPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);


        }
        return false;
    }

    private boolean hasAppPermissions() {
        int appPermissions = PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            appPermissions = context.checkSelfPermission(Manifest.permission.READ_CONTACTS) +
                    context.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) +
                    context.checkSelfPermission(Manifest.permission.READ_CALL_LOG) +
                    context.checkSelfPermission(Manifest.permission.WRITE_CALL_LOG) +
                    context.checkSelfPermission(Manifest.permission.CALL_PHONE) +
                    context.checkSelfPermission(Manifest.permission.SEND_SMS) +
                    context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) +
                    context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) +
                    context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) +
                    context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) +
                    context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        return appPermissions == PackageManager.PERMISSION_GRANTED;
    }


    private boolean hasAppPermissions(String strPermission) {
        int appPermissions = PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            appPermissions = context.checkSelfPermission(strPermission);
        }

        return appPermissions == PackageManager.PERMISSION_GRANTED;
    }

    private boolean canDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }


    /**
     * @return True if {@link SiempoNotificationListener_} is enabled.
     */
    private boolean isEnabled() {
        return ServiceUtils.isNotificationListenerServiceRunning(context, SiempoNotificationListener_.class);
    }

    private boolean checkUserStatPermission() {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            Tracer.d("Usage stat permission: " + mode);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            Tracer.d("Usage stat permission: " + e.getMessage());
            return false;
        }
    }

}
