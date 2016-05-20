package minium.co.launcher2.flow;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.service.notification.NotificationListenerService;

import org.androidannotations.annotations.EService;

import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 5/16/2016.
 */
@EService
public class FlowNotificationService extends NotificationListenerService {

    private int currentFilter = INTERRUPTION_FILTER_ALL;

    //In the Service I use this to enable and disable silent mode(or priority...)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean start = intent.getBooleanExtra("start", false);
        if(start)
        {
            currentFilter = getCurrentInterruptionFilter();
            Tracer.d("Starting service");

            //Check if at least Lollipop, otherwise use old method
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                requestInterruptionFilter(INTERRUPTION_FILTER_NONE);
            else{
                AudioManager am = (AudioManager) getBaseContext().getSystemService(AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        }
        else
        {
            Tracer.d("Stopping service");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                requestInterruptionFilter(currentFilter);
            else{
                AudioManager am = (AudioManager) getBaseContext().getSystemService(AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
        }
        return super.onStartCommand(intent, flags, startId);

    }

    @Override public void onListenerConnected() {
        Tracer.d("onListenerConnected()");
    }
    @Override public void onListenerHintsChanged(int hints) {
        Tracer.d("onListenerHintsChanged(" + hints + ')');
    }

    @Override
    public void onInterruptionFilterChanged(int interruptionFilter) {
        Tracer.d("onInterruptionFilterChanged(" + interruptionFilter + ')');
    }
}
