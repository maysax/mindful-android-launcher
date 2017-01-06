package com.itconquest.tracking.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by Shahab on 12/6/2016.
 */

public final class FileUtil {

    public void deleteOldApk() {
        File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "tracking.apk");
        if (apkFile.exists())
            delete(apkFile);
    }

    private boolean delete(File file) {
        return file.delete();
    }

}
