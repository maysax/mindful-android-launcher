package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity {



    public final static String GOOGLE_URL = "https://play.google.com/store/apps/details?id=";
    public static final String ERROR = "error";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new FetchCategoryTask().execute();
    }

    private class FetchCategoryTask extends AsyncTask<Void, Void, Void> {

        private final String TAG = FetchCategoryTask.class.getSimpleName();
        private PackageManager pm;
//        private ActivityUtil mActivityUtil;

        @Override
        protected Void doInBackground(Void... errors) {
            String category;
//            pm = getPackageManager();
//            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
//            Iterator<ApplicationInfo> iterator = packages.iterator();
//            while (iterator.hasNext()) {
//                ApplicationInfo packageInfo = iterator.next();
//                String query_url = GOOGLE_URL + packageInfo.packageName;
//                Log.i(TAG, query_url);
//                category = getCategory(query_url);
//                if(!TextUtils.isEmpty(category) && !category.equalsIgnoreCase("error")) {
//                    Log.d("hardikkamothi","Application name >>>>>>>>>>>>>>  :::"+packageInfo.name);
//                    Log.d("hardikkamothi", "Category is :::" + category);
//                }
//            }



            List<ResolveInfo> installedPackageList;
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            installedPackageList = getPackageManager().queryIntentActivities(mainIntent, 0);
            Log.d("hardikkamothi","installed package size"+installedPackageList.size());
            for (ResolveInfo resolveInfo : installedPackageList) {
                if(resolveInfo!=null && resolveInfo.activityInfo!=null && resolveInfo.activityInfo.packageName!=null){
                    String query_url = GOOGLE_URL + resolveInfo.activityInfo.packageName;
                    Log.i(TAG, query_url);
                    category = getCategory(query_url);
                    if(!TextUtils.isEmpty(category) && !category.equalsIgnoreCase("error")) {
                        Log.d("hardikkamothi","Application name >>>>>>>>>>>>>>  :::"+resolveInfo.activityInfo.name);
                        Log.d("hardikkamothi", "Category is :::" + category);
                    }
                }
            }
            return null;
        }


        private String getCategory(String query_url) {
//            boolean network = mActivityUtil.isNetworkAvailable();
//            if (!network) {
//                //manage connectivity lost
//                return ERROR;
//            } else {
                try {
                    Document doc = Jsoup.connect(query_url).get();
                    Element link = doc.select("a[class=\"hrTbp R8zArc\"]").get(1);

                    return link.text();
                } catch (Exception e) {
                    return ERROR;
                }
//            }
        }
    }
}
