package co.siempo.phone.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import co.siempo.phone.service.SiempoDndService;

/**
 * Created by Shahab on 5/17/2017.
 */

public class PackageUtil {

    public static boolean isCallPackage(String pkg) {
        return pkg.contains("telecom") || pkg.contains("dialer");
    }

    public static boolean isMsgPackage(String pkg) {
        return pkg.contains("messaging") || pkg.contains("com.android.mms");
    }

    public static boolean isCalenderPackage(String pkg) {
        return pkg.contains("com.google.android.calendar") || pkg.contains("com.android.calendar");
    }

    public static boolean isSiempoLauncher(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo defaultLauncher = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String defaultLauncherStr = defaultLauncher.activityInfo.packageName;
        return defaultLauncherStr.equals(context.getPackageName());
    }

    public static boolean isSiempo(String pkg) {
        return pkg.contains("siempo");
    }

    public static boolean isSiempoBlocker(int notifId) {
        return notifId == SiempoDndService.NOTIFICATION_ID;
    }

    public static int getIdByPackage(String pkg) {
        int ret = 0;
        for (int i = 0; i < pkg.length(); i++) {
            ret += ((i + 1) * pkg.charAt(i));
        }
        return ret;
    }
}
