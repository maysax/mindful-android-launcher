package co.siempo.phone.service;

import android.os.Environment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.StringRequestListener;

import org.androidannotations.annotations.EBean;

import java.io.File;
import java.util.Locale;

import co.siempo.phone.event.CheckVersionEvent;
import co.siempo.phone.event.DownloadApkEvent;
import co.siempo.phone.log.Tracer;
import de.greenrobot.event.EventBus;

/**
 * Created by Shahab on 1/10/2017.
 */

@EBean
public abstract class CoreAPIClient {

    protected final String AWS_HOST = "http://34.193.40.200:8001";
    protected final String AWS_TOKEN = "SN2NaFFSMPkKRhMOioNEPERrCl2iCuhRcHwpm0J9";
    protected AnalyticsListener analyticsListener = new AnalyticsListener() {
        @Override
        public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
            Tracer.d("timeTakenInMillis: " + timeTakenInMillis
                    + " bytesSent: " + bytesSent
                    + " bytesReceived: " + bytesReceived
                    + " isFromCache: " + isFromCache);
        }
    };

    protected abstract String getAppName();

    /**
     * This function is use to check current app version with play store version
     * and display alert if update is available using AWS API's.
     */
    public void checkAppVersion(String versionFor) {
        if (versionFor.equalsIgnoreCase(CheckVersionEvent.ALPHA)) {
            AndroidNetworking.get(String.format(Locale.US, "%s/%s/version", AWS_HOST, getAppName()))
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .doNotCacheResponse()
                    .build()
                    .setAnalyticsListener(analyticsListener)
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                int version = Integer.parseInt(response.trim());
                                EventBus.getDefault().post(new CheckVersionEvent(version, CheckVersionEvent.ALPHA));
                            } catch (Exception e) {
                                EventBus.getDefault().post(new
                                        CheckVersionEvent(-1000,
                                        CheckVersionEvent.ALPHA));
                                Tracer.e(e, e.getMessage());

                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            EventBus.getDefault().post(new CheckVersionEvent
                                    (-1000, CheckVersionEvent.ALPHA));
                            Tracer.e(anError.getCause(), anError.getErrorDetail());
                        }
                    });
        } else if (versionFor.equalsIgnoreCase(CheckVersionEvent.BETA)) {
            AndroidNetworking.get(String.format(Locale.US, "%s/%s/version-beta", AWS_HOST, getAppName()))
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .doNotCacheResponse()
                    .build()
                    .setAnalyticsListener(analyticsListener)
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                int version = Integer.parseInt(response.trim());
                                EventBus.getDefault().post(new CheckVersionEvent(version, CheckVersionEvent.BETA));
                            } catch (Exception e) {
                                EventBus.getDefault().post(new
                                        CheckVersionEvent(-1000,
                                        CheckVersionEvent.BETA));
                                Tracer.e(e, e.getMessage());

                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            EventBus.getDefault().post(new CheckVersionEvent
                                    (-1000, CheckVersionEvent.BETA));
                            Tracer.e(anError.getCause(), anError.getErrorDetail());
                        }
                    });
        }
//        AndroidNetworking.get(String.format(Locale.US, "%s/%s/version", AWS_HOST, getAppName()))
//                .setTag("test")
//                .setPriority(Priority.MEDIUM)
//                .doNotCacheResponse()
//                .build()
//                .setAnalyticsListener(analyticsListener)
//                .getAsString(new StringRequestListener() {
//                    @Override
//                    public void onResponse(String response) {
//                        EventBus.getDefault().post(new CheckVersionEvent(Integer.parseInt(response.trim())));
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        EventBus.getDefault().post(new CheckVersionEvent(-1000));
//                        Tracer.e(anError.getCause(), anError.getErrorDetail());
//                    }
//                });
    }

    public void downloadApk() {
        final File externalFilesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (!externalFilesDir.exists()) {
            boolean mkdirs = externalFilesDir.mkdirs();
            Tracer.d("Creating " + externalFilesDir.getAbsolutePath() + ": " + mkdirs);
        }

        AndroidNetworking.download(String.format(Locale.US, "%s/%s/app/%s.apk", AWS_HOST, getAppName(), getAppName()), externalFilesDir.getAbsolutePath(), getAppName() + ".apk")
                .setTag("downloadTest")
                .setPriority(Priority.MEDIUM)
                .doNotCacheResponse()
                .build()
                .setAnalyticsListener(analyticsListener)
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        Tracer.d("Download apk " + bytesDownloaded + "/" + totalBytes);
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Tracer.i("Download completed");
                        EventBus.getDefault().post(new DownloadApkEvent(externalFilesDir.getAbsolutePath() + File.separator + getAppName() + ".apk"));
                    }

                    @Override
                    public void onError(ANError error) {
                        Tracer.e(error.getCause(), error.getErrorDetail());
                    }
                });
    }

}
