package com.itconquest.tracking.services;

import android.util.Base64;

import com.adeel.library.easyFTP;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.itconquest.tracking.util.TrackingLogger;
import com.itconquest.tracking.util.TrackingPref_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;
import java.util.Locale;

import minium.co.core.log.Tracer;
import minium.co.core.service.CoreAPIClient;

/**
 * Created by Shahab on 1/5/2017.
 */

@EBean
public class ApiClient extends CoreAPIClient {

    @Pref
    TrackingPref_ trackingPrefs;

    private final String FTP_HOST = "dropbox.sandbox2000.com";
    private final String FTP_USER = "junkspace";
    private final String FTP_PASS = "C!55iL9p";



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
        AndroidNetworking.upload(String.format(Locale.US, "%s/upload.php?secret=%s", AWS_HOST, AWS_TOKEN))
                .addHeaders("Authorization", Base64.encodeToString("user:@#132".getBytes(), Base64.DEFAULT))
                .addMultipartParameter("app", "tracking")
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
                        trackingPrefs.trackingLogFileName().put("");
                    }

                    @Override
                    public void onError(ANError anError) {
                        Tracer.e(anError.getCause(), anError.getErrorDetail());
                    }
                });
    }


    @Override
    protected String getAppName() {
        return "tracking";
    }
}
