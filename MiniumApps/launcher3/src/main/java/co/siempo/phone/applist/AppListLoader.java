package co.siempo.phone.applist;

import android.Manifest;
import android.content.AsyncTaskLoader;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.Trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;

/**
 * Created by tkb on 2017-04-27.
 */
@EBean
public class AppListLoader extends AsyncTaskLoader<List<ApplistDataModel>> {

    List<ApplistDataModel> mModels;

    public AppListLoader(Context context) {
        super(context);
    }

    @Override
    public List<ApplistDataModel> loadInBackground() {
        System.out.println("DataListLoader.loadInBackground");
        return getInstalledAppList(getContext());
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override public void deliverResult(List<ApplistDataModel> listOfData) {
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
    @Override protected void onStartLoading() {
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
    @Override protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(List<ApplistDataModel> apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
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
    protected void onReleaseResources(List<ApplistDataModel> apps) {}


    @Trace(tag = LogConfig.LOG_TAG)
    ArrayList<ApplistDataModel> getInstalledAppList(Context context)
    {

        ArrayList<ApplistDataModel> arrayList = new ArrayList<>();

        ApplistDataModel applistDataModel;
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pkgManager = context.getPackageManager();
        final List pkgAppsList = pkgManager.queryIntentActivities( mainIntent, 0);
        for (Object object : pkgAppsList)
        {
            applistDataModel = new ApplistDataModel();

            ResolveInfo info = (ResolveInfo) object;
            Drawable icon    = pkgManager.getApplicationIcon(info.activityInfo.applicationInfo);
            String strPackageName  = info.activityInfo.applicationInfo.packageName;
            final String title 	= (String) pkgManager.getApplicationLabel(info.activityInfo.applicationInfo);

            applistDataModel.setName(title);
            applistDataModel.setIcon(icon);
            applistDataModel.setPackageName(strPackageName);

            arrayList.add(applistDataModel);
        }

        Collections.sort(arrayList, new Comparator<ApplistDataModel>() {
            @Override
            public int compare(ApplistDataModel lhs, ApplistDataModel rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.getName(), rhs.getName());
            }
        });
        return arrayList;
    }
}
