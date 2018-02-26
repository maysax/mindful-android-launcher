package co.siempo.phone.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import co.siempo.phone.utils.PackageUtil;

/**
 * Created by rajeshjadi on 23/2/18.
 */

public class BitmapWorkerTask extends AsyncTask<Void, Void, Void> {
    // Decode image in background.
    String name;
    Context context;

    public BitmapWorkerTask(Context context, String name) {
        this.context = context;
        this.name = name;
    }

    @Override
    protected Void doInBackground(Void... params) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(name, PackageManager.GET_META_DATA);
            Drawable drawable = appInfo.loadIcon(context.getPackageManager());
            Bitmap bitmap = PackageUtil.drawableToBitmap(drawable);
            Log.d("Rajesh ", appInfo.packageName + "---" + bitmap);
            CoreApplication.getInstance().addBitmapToMemoryCache(appInfo.packageName, bitmap);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
