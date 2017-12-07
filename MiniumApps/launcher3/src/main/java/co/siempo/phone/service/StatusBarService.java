package co.siempo.phone.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import co.siempo.phone.event.TorchOnOff;
import co.siempo.phone.helper.FirebaseHelper;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.event.FirebaseEvent;

/**
 * This background service used for detect torch status and feature used for any other background status.
 */

public class StatusBarService extends Service {

    private CameraManager cameraManager;
    private String mCameraId;
    @SuppressWarnings("deprecation")
    private Camera camera;
    @SuppressWarnings("deprecation")
    private Camera.Parameters parameters;
    public static boolean isFlashOn = false;

    SharedPreferences sharedPreferences;
    private MyObserver myObserver;
    private AppInstallUninstall appInstallUninstall;


    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("DroidPrefs", 0);
        registerObserverForContact();
        registerObserverForAppInstallUninstall();
        EventBus.getDefault().register(this);

    }

    /**
     * Observer for when installing new app or uninstalling the app.
     */
    private void registerObserverForAppInstallUninstall() {
        appInstallUninstall = new AppInstallUninstall();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(appInstallUninstall, intentFilter);
    }

    /**
     * Observer for when new contact adding or updating any exiting contact.
     */
    private void registerObserverForContact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                myObserver = new MyObserver(new Handler());
                getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true,
                        myObserver);
            }
        } else {
            myObserver = new MyObserver(new Handler());
            getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true,
                    myObserver);
        }
    }


    public StatusBarService() {
    }

    @Subscribe
    public void tourchOnOff(TorchOnOff torchOnOFF) {
        if (torchOnOFF.isRunning()) {
            turnONFlash();
        } else {
            turnOffFlash();
        }
    }

    @Subscribe
    public void firebaseEvent(FirebaseEvent firebaseEvent) {
        FirebaseHelper.getIntance().logScreenUsageTime(firebaseEvent.getScreenName(),firebaseEvent.getStrStartTime());
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Turning On flash
     */
    @TargetApi(23)
    private void turnONFlash() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            cameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
            try {
                mCameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(mCameraId, true);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            CameraManager.TorchCallback mTorchCallback = new CameraManager.TorchCallback() {

                @Override
                public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                    super.onTorchModeChanged(cameraId, enabled);
                    isFlashOn = enabled;
                }

                @Override
                public void onTorchModeUnavailable(@NonNull String cameraId) {
                    super.onTorchModeUnavailable(cameraId);
                }
            };
            cameraManager.registerTorchCallback(mTorchCallback, new Handler());
        } else {
            //noinspection deprecation
            camera = Camera.open();
            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.startPreview();
        }
        isFlashOn = true;
    }

    /**
     * Turning On flash
     */
    private void turnOffFlash() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            try {
                cameraManager.setTorchMode(mCameraId, false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            camera.stopPreview();
            if (camera != null) {
                camera.release();
                camera = null;
            }
        }
        isFlashOn = false;
    }


    private class MyObserver extends ContentObserver {
        MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // do s.th.
            // depending on the handler you might be on the UI
            // thread, so be cautious!

            sharedPreferences.edit().putBoolean("isContactUpdate", true).apply();
        }
    }

    class AppInstallUninstall extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            CoreApplication.getInstance().getAllApplicationPackageName();
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String installPackageName = intent.getData().getEncodedSchemeSpecificPart();
                Log.d("Testing with device.", "Added" + installPackageName);
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String uninstallPackageName = intent.getData().getSchemeSpecificPart();
                Log.d("Testing with device.", "Removed" + uninstallPackageName);
            }
            sharedPreferences.edit().putBoolean("isAppUpdated", true).apply();
            EventBus.getDefault().post(new AppInstalledEvent(true));
        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (myObserver != null)
            getContentResolver().unregisterContentObserver(myObserver);
        if (appInstallUninstall != null)
            unregisterReceiver(appInstallUninstall);
        super.onDestroy();
    }
}
