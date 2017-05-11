package co.siempo.phone.applist;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.UserManager;
import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.siempo.phone.kiss.utils.UserHandle;
import minium.co.core.log.LogConfig;

/**
 * Created by tkb on 2017-04-27.
 */
@EBean
public class AppListLoader extends AsyncTaskLoader<List<ApplistDataModel>> {

    List<ApplistDataModel> mModels;

    @SystemService
    UserManager userManager;

    @SystemService
    LauncherApps launcherApps;

    public AppListLoader(Context context) {
        super(context);
    }

    @Override
    public List<ApplistDataModel> loadInBackground() {
        System.out.println("DataListLoader.loadInBackground");
        return getApplicationsList();
//        return getInstalledAppList(getContext());
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<ApplistDataModel> listOfData) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (listOfData != null) {
                onReleaseResources(listOfData);
            }
        }
        List<ApplistDataModel> oldApps = listOfData;
        mModels = listOfData;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(listOfData);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mModels != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mModels);
        }


        if (takeContentChanged() || mModels == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<ApplistDataModel> apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mModels != null) {
            onReleaseResources(mModels);
            mModels = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<ApplistDataModel> apps) {
    }


    @Trace(tag = LogConfig.LOG_TAG)
    ArrayList<ApplistDataModel> getInstalledAppList(Context context) {
        long start = System.currentTimeMillis();

        ArrayList<ApplistDataModel> arrayList = new ArrayList<>();

        ApplistDataModel applistDataModel;
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pkgManager = context.getPackageManager();
        final List pkgAppsList = pkgManager.queryIntentActivities(mainIntent, 0);

        Log.i(LogConfig.LOG_TAG, String.format("#1 duration in ms: %d", (System.currentTimeMillis() - start)));
        start = System.currentTimeMillis();

        for (Object object : pkgAppsList) {
            applistDataModel = new ApplistDataModel();

            ResolveInfo info = (ResolveInfo) object;
            Drawable icon = pkgManager.getApplicationIcon(info.activityInfo.applicationInfo);

            Log.i(LogConfig.LOG_TAG, String.format("#2 duration in ms: %d", (System.currentTimeMillis() - start)));
            start = System.currentTimeMillis();

            String strPackageName = info.activityInfo.applicationInfo.packageName;

            Log.i(LogConfig.LOG_TAG, String.format("#3 duration in ms: %d", (System.currentTimeMillis() - start)));
            start = System.currentTimeMillis();

            final String title = (String) pkgManager.getApplicationLabel(info.activityInfo.applicationInfo);

            Log.i(LogConfig.LOG_TAG, String.format("#4 duration in ms: %d", (System.currentTimeMillis() - start)));
            start = System.currentTimeMillis();

            applistDataModel.setName(title);
            applistDataModel.setIcon(icon);
            applistDataModel.setPackageName(strPackageName);

            arrayList.add(applistDataModel);
        }

        Log.i(LogConfig.LOG_TAG, String.format("#5 duration in ms: %d", (System.currentTimeMillis() - start)));
        start = System.currentTimeMillis();

        Collections.sort(arrayList, new Comparator<ApplistDataModel>() {
            @Override
            public int compare(ApplistDataModel lhs, ApplistDataModel rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.getName(), rhs.getName());
            }
        });

        Log.i(LogConfig.LOG_TAG, String.format("#6 duration in ms: %d", (System.currentTimeMillis() - start)));

        return arrayList;
    }

    @Trace(tag = LogConfig.LOG_TAG)
    ArrayList<ApplistDataModel> getApplicationsList() {
        ArrayList<ApplistDataModel> apps = new ArrayList<>();
        for (android.os.UserHandle profile : userManager.getUserProfiles()) {
            UserHandle user = new UserHandle(userManager.getSerialNumberForUser(profile), profile);
            for (LauncherActivityInfo activityInfo : launcherApps.getActivityList(null, profile)) {
                ApplicationInfo appInfo = activityInfo.getApplicationInfo();
                ApplistDataModel app = new ApplistDataModel();

                app.setName(activityInfo.getLabel().toString());

                app.setPackageName(appInfo.packageName);
                app.setActivityName(activityInfo.getName());

                // Wrap Android user handle in opaque container that will work across
                // all Android versions
                app.setUserHandle(user);

                apps.add(app);

            }
        }
        return apps;
    }
}
