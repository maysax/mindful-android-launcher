package minium.co.launcher2.messages;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.androidannotations.annotations.EService;

/**
 * Created by Shahab on 7/29/2016.
 */
@EService
public class HeadlessSmsSendService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
