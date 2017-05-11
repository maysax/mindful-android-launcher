package co.siempo.phone.applist;

import android.content.Context;
import android.provider.Telephony;

/**
 * Created by Shahab on 5/11/2017.
 */

public class AppUtil {

    public static boolean isDefaultSmsApp(Context context, String packageName) {
        return packageName.equals(Telephony.Sms.getDefaultSmsPackage(context));
    }
}
