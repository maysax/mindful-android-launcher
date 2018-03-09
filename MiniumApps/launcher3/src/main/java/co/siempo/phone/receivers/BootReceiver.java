package co.siempo.phone.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import co.siempo.phone.utils.PrefSiempo;

/**
 * Created by hardik on 18/1/18.
 */

public class BootReceiver extends BroadcastReceiver {
    SharedPreferences sharedPref;
    private String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Boot complete");
        PrefSiempo.getInstance(context).write(PrefSiempo.CALL_RUNNING, false);
    }
}
