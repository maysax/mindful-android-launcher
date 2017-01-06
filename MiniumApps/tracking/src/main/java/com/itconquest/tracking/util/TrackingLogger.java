package com.itconquest.tracking.util;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.itconquest.tracking.App_;
import com.itconquest.tracking.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import minium.co.core.app.CoreApplication;
import minium.co.core.log.LogConfig;
import minium.co.core.log.LogFormatter;

import static java.security.AccessController.getContext;

/**
 * Created by Shahab on 12/6/2016.
 */

public class TrackingLogger {

    private static ExecutorService executor = null;

    private Context context;

    /**
     * Get the ExecutorService
     *
     * @return the ExecutorService
     */
    protected static ExecutorService getExecutor() {
        return executor;
    }

    private static String fileName = "";

    public static String getCurrentFileName() {
        return fileName;
    }


    protected static void log2file(final String path, final String str) {
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor();
        }

        fileName = path;

        executor.execute(new Runnable() {
            @Override
            public void run() {
                PrintWriter out = null;

                File file = GetFileFromPath(path);

                try {
                    out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
                    out.println(str);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
        });
    }

    /**
     * Get File form the file path.<BR>
     * if the file does not exist, create it and return it.
     *
     * @param path the file path
     * @return the file
     */
    private static File GetFileFromPath(String path) {
        boolean ret;
        boolean isExist;
        boolean isWritable;
        File file = null;

        if (TextUtils.isEmpty(path)) {
            Log.e("Error", "The path of Log file is Null.");
            return file;
        }

        file = new File(path);

        isExist = file.exists();
        isWritable = file.canWrite();

        if (isExist) {
            if (isWritable) {
                //Log.i("Success", "The Log file exist,and can be written! -" + file.getAbsolutePath());
            } else {
                Log.e("Error", "The Log file can not be written.");
            }
        } else {
            //create the log file
            try {
                ret = file.createNewFile();
                if (ret) {
                    Log.i("Success", "The Log file was successfully created! -" + file.getAbsolutePath());
                } else {
                    Log.i("Success", "The Log file exist! -" + file.getAbsolutePath());
                }

                isWritable = file.canWrite();
                if (!isWritable) {
                    Log.e("Error", "The Log file can not be written.");
                }
            } catch (IOException e) {
                Log.e("Error", "Failed to create The Log file.");
                e.printStackTrace();
            }
        }

        return file;
    }

    public static void log(String message, Throwable tr) {
        LogFormatter.EclipseFormatter formatter = new LogFormatter.EclipseFormatter();
        String formatMsg = formatter.format(LogFormatter.LEVEL.DEBUG, LogConfig.LOG_TAG, message, tr);
        String dataDirPath = Environment.getDataDirectory().getAbsolutePath();
        File externalFilesDir = CoreApplication.getInstance().getExternalFilesDir(dataDirPath);
        if (externalFilesDir != null) {
            log2file(externalFilesDir.getAbsolutePath() + File.separator + App_.getInstance().getFileName(), formatMsg);
        }
    }

}
