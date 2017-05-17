package co.siempo.phone.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.StatusBarNotificationStorage;
import co.siempo.phone.db.StatusBarNotificationStorageDao;
import co.siempo.phone.util.PackageUtil;
import minium.co.core.log.Tracer;
import minium.co.core.util.UIUtils;

/**
 * Created by Shahab on 4/21/2017.
 */

@EBean
public class NotificationRetreat {

    private Context context;

    @SystemService
    NotificationManager notificationManager;

    public NotificationRetreat(Context context) {
        this.context = context;
    }

    public void retreat() {
        if (!PackageUtil.isSiempoLauncher(context)) {
            Tracer.d("Default launcher is not siempo");
            StatusBarNotificationStorageDao statusStorageDao = DBUtility.getStatusStorageDao();
            List<StatusBarNotificationStorage> list = statusStorageDao.queryBuilder()
                    .orderDesc(StatusBarNotificationStorageDao.Properties.PostTime).build().list();

            Tracer.d("Number of saved notifications: " + list.size());
            for (StatusBarNotificationStorage s : list) {
                notificationBuilder(s);
            }

            statusStorageDao.deleteAll();
        } else {
            Tracer.d("Default launcher is siempo");
        }
    }



    private String getAppName(String packageName) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {}
        final String title = (String)((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");
        return title;
    }

    private void notificationBuilder(StatusBarNotificationStorage storage) {

        try {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_siempo_notification)
                            .setContentTitle(getAppName(storage.getPackageName()))
                            .setContentText(storage.getContent())
                    .setAutoCancel(true);
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = context.getPackageManager().getLaunchIntentForPackage(storage.getPackageName());

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            //stackBuilder.addParentStack(ResultActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            // mId allows you to update the notification later on.
            notificationManager.notify(storage.getId().intValue(), mBuilder.build());
        } catch (Exception e) {
            Tracer.e(e,e.getMessage());
        }
    }
}
