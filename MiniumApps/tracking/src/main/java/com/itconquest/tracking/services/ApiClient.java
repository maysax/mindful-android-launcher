package com.itconquest.tracking.services;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;

import com.adeel.library.easyFTP;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.itconquest.tracking.BuildConfig;
import com.itconquest.tracking.event.CheckVersionEvent;
import com.itconquest.tracking.event.DownloadApkEvent;
import com.itconquest.tracking.util.TrackingLogger;

import org.androidannotations.annotations.EBean;

import java.io.File;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 1/5/2017.
 */

@EBean
public class ApiClient {

    private final String FTP_HOST = "dropbox.sandbox2000.com";
    private final String FTP_USER = "junkspace";
    private final String FTP_PASS = "C!55iL9p";

    private final String AWS_HOST = "http://34.193.40.200:8001";
    private final String AWS_TOKEN = "SN2NaFFSMPkKRhMOioNEPERrCl2iCuhRcHwpm0J9";


    public void uploadFileToFTP() {
        com.adeel.library.easyFTP ftp = new easyFTP();
        try {
            ftp.connect(FTP_HOST, FTP_USER, FTP_PASS);
            Tracer.d("FTP connected");
            ftp.setWorkingDirectory("/dropbox.sandbox2000.com/Ebb/Tracking");
            ftp.uploadFile(new File(TrackingLogger.getCurrentFileName()).toString());
            ftp.disconnect();
            Tracer.d("File uploaded");
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
            e.printStackTrace();
        }
    }

    public void uploadFileToAWS() {
        AndroidNetworking.upload(String.format(Locale.US, "%s/upload.php?token=%s", AWS_HOST, AWS_TOKEN))
                .addMultipartFile("file", new File(TrackingLogger.getCurrentFileName()))
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        Tracer.d("Upload to AWS " + bytesUploaded + "/" + totalBytes);
                    }
                })
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Tracer.i("Upload to AWS " + response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Tracer.e(anError.getCause(), anError.getErrorDetail());
                    }
                });
    }

    public void checkAppVersion() {

        AndroidNetworking.get(String.format(Locale.US, "%s/count", AWS_TOKEN))
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        EventBus.getDefault().post(new CheckVersionEvent(Integer.parseInt(response.trim())));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Tracer.e(anError.getCause(), anError.getErrorDetail());
                    }
                });
    }

    public void downloadApk() {
        String dataDirPath = Environment.getDataDirectory().getAbsolutePath();
        final File externalFilesDir = CoreApplication.getInstance().getExternalFilesDir(dataDirPath);

        AndroidNetworking.download(String.format(Locale.US, "%s/tracking.apk", AWS_HOST), externalFilesDir.getAbsolutePath(), "tracking.apk")
                .setTag("downloadTest")
                .setPriority(Priority.MEDIUM)
                .build()
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
                        EventBus.getDefault().post(new DownloadApkEvent(externalFilesDir.getAbsolutePath() + File.separator + "tracking.apk"));
                    }
                    @Override
                    public void onError(ANError error) {
                        Tracer.e(error.getCause(), error.getErrorDetail());
                    }
                });


    }


}
