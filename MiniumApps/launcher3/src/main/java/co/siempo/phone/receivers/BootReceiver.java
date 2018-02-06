package co.siempo.phone.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import co.siempo.phone.app.Constants;

/**
 * Created by hardik on 18/1/18.
 */

public class BootReceiver extends BroadcastReceiver {
    SharedPreferences sharedPref;
    private String TAG="BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"Boot complete");
        sharedPref =
                context.getSharedPreferences("Launcher3Prefs", 0);
        sharedPref.edit().putBoolean(Constants.CALL_RUNNING,false).commit();
    }
}
