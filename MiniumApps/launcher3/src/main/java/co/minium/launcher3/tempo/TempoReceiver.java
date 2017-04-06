package co.minium.launcher3.tempo;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.greendao.query.CountQuery;

import co.minium.launcher3.app.Launcher3Prefs_;
import co.minium.launcher3.call.CallStorageDao;
import co.minium.launcher3.db.DBUtility;
import co.minium.launcher3.db.TableNotificationSms;
import co.minium.launcher3.db.TableNotificationSmsDao;
import co.minium.launcher3.util.AudioUtils;
import minium.co.core.util.UIUtils;

/**
 * Created by Shahab on 3/30/2017.
 */
@EReceiver
public class TempoReceiver extends BroadcastReceiver {

    @SystemService
    NotificationManager notificationManager;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    public static final String KEY_IS_TEMPO = "isTempo";

    TableNotificationSmsDao smsDao;
    CallStorageDao callStorageDao;

    public TempoReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (launcherPrefs.isPauseActive().get()) {
            // pass
        } else if (launcherPrefs.isTempoActive().get()) {
            // show notifications
            smsDao = DBUtility.getNotificationDao();
            callStorageDao = DBUtility.getCallStorageDao();

            long smsCount = smsDao.queryBuilder().count();
            long callCount = callStorageDao.queryBuilder().count();

            if (smsCount + callCount > 0) {
                AudioUtils.playnotification(context);
            }
        } else {
            // pass
        }
    }
}
