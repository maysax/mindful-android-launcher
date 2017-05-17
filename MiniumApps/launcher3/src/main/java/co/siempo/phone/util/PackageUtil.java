package co.siempo.phone.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

/**
 * Created by Shahab on 5/17/2017.
 */

public class PackageUtil {

    public static boolean isCallPackage(String pkg) {
        return pkg.contains("telecom") || pkg.contains("dialer");
    }

    public static boolean isMsgPackage(String pkg) {
        return pkg.contains("messaging");
    }

    public static boolean isSiempoLauncher(Context context) {
        Intent intent= new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo defaultLauncher = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String defaultLauncherStr = defaultLauncher.activityInfo.packageName;
        return defaultLauncherStr.equals(context.getPackageName());
    }

    public static boolean isSiempo(String pkg) {
        return pkg.contains("siempo");
    }
}
