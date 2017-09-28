package co.siempo.phone.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import co.siempo.phone.event.TourchOnOff;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * This background service used for detect tourch status and feature used for any other background status.
 */
public class StatusBarService extends Service {

    private CameraManager cameraManager;
    private String mCameraId;
    private Camera camera;
    private Camera.Parameters parameters;
    public static boolean isFlashOn = false;

    // private MyObserver myObserver;// Quick setting feature reference
    @TargetApi(23)
    @Override
    public void onCreate() {
        super.onCreate();
        // Quick setting feature reference
//        myObserver= new MyObserver(new Handler());
//        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true,
//                myObserver);
        EventBus.getDefault().register(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            cameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
            try {
                mCameraId = cameraManager.getCameraIdList()[0];
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            CameraManager.TorchCallback mTorchCallback = new CameraManager.TorchCallback() {

                @Override
                public void onTorchModeChanged(String cameraId, boolean enabled) {
                    super.onTorchModeChanged(cameraId, enabled);
                    isFlashOn = enabled;
                }

                @Override
                public void onTorchModeUnavailable(String cameraId) {
                    super.onTorchModeUnavailable(cameraId);
                }
            };
            cameraManager.registerTorchCallback(mTorchCallback, new Handler());
        } else {
            camera = Camera.open();
            parameters = camera.getParameters();
        }
    }


    public StatusBarService() {
    }

    @Subscribe
    public void tourchOnOff(TourchOnOff tourchOnOFF) {
        if (tourchOnOFF.isRunning()) {
            turnONFlash();
        } else {
            turnOffFlash();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Turning On flash
     */
    private void turnONFlash() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            try {
                cameraManager.setTorchMode(mCameraId, true);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
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
        }
        isFlashOn = false;
    }

    /**
     * Turning On flash
     */
    private boolean checkFlashOnOFF() {
        if (camera == null) {
            camera = Camera.open();
            parameters = camera.getParameters();
        }
        if (parameters.getFlashMode().equals(android.hardware.Camera.Parameters.FLASH_MODE_ON)) {
            return true;
        } else if (parameters.getFlashMode().equals(android.hardware.Camera.Parameters.FLASH_MODE_OFF)) {
            return false;
        } else if (parameters.getFlashMode().equals(android.hardware.Camera.Parameters.FLASH_MODE_TORCH)) {
            return true;
        } else if (parameters.getFlashMode().equals(android.hardware.Camera.Parameters.FLASH_MODE_AUTO)) {
            return false;
        }
        return false;
    }
//    Used for SSA-206 Quick settings for feature reference.
//    class MyObserver extends ContentObserver {
//        public MyObserver(Handler handler) {
//            super(handler);
//        }
//
//        @Override
//        public void onChange(boolean selfChange) {
//            this.onChange(selfChange, null);
//        }
//
//        @Override
//        public void onChange(boolean selfChange, Uri uri) {
//            // do s.th.
//            // depending on the handler you might be on the UI
//            // thread, so be cautious!
//            Log.d("Raja","Rajajajajajaj");
//        }
//    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
//        getContentResolver().unregisterContentObserver(myObserver);//reference for quick setting
        super.onDestroy();
    }
}
