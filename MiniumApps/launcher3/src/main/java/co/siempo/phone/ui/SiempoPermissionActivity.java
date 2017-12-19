package co.siempo.phone.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

import co.siempo.phone.MainActivity;
import co.siempo.phone.MainActivity_;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.util.PermissionUtil;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;


/**
 * Created by Shahab on 1/12/2017.
 */
@EActivity(R.layout.activity_permission)
public class SiempoPermissionActivity extends CoreActivity {

    @ViewById
    Toolbar toolbar;
    @ViewById
    Switch switchContactPermission;
    @ViewById
    Switch switchCallPermission;
    @ViewById
    Switch switchSmsPermission;
    @ViewById
    Switch switchCameraPermission;
    @ViewById
    Switch switchFilePermission;
    @ViewById
    Switch switchNotificationAccess;
    @ViewById
    Switch switchOverlayAccess;
    @ViewById
    Button btnContinue;
    private PermissionUtil permissionUtil;
    @Pref
    Launcher3Prefs_ launcher3Prefs;

    @AfterViews
    void afterViews() {
        permissionUtil = new PermissionUtil(this);
//        if (launcher3Prefs.isPermissionGivenAndContinued().get() && permissionUtil.hasGiven(PermissionUtil.CONTACT_PERMISSION)
//                && permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION) && permissionUtil.hasGiven(PermissionUtil.SEND_SMS_PERMISSION)
//                && permissionUtil.hasGiven(PermissionUtil.CAMERA_PERMISSION) && permissionUtil.hasGiven(PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION)
//                && permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS) && permissionUtil.hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS)
//                ) {
//            Intent intent = new Intent(SiempoPermissionActivity.this, MainActivity_.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            finish();
//        } else {
            setSupportActionBar(toolbar);
            switchSmsPermission.setOnCheckedChangeListener(onCheckedChangeListener);
            switchContactPermission.setOnCheckedChangeListener(onCheckedChangeListener);
            switchCameraPermission.setOnCheckedChangeListener(onCheckedChangeListener);
            switchCallPermission.setOnCheckedChangeListener(onCheckedChangeListener);
            switchFilePermission.setOnCheckedChangeListener(onCheckedChangeListener);
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (permissionUtil.hasGiven(PermissionUtil.CONTACT_PERMISSION))
            switchContactPermission.setChecked(true);
        if (permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION))
            switchCallPermission.setChecked(true);
        if (permissionUtil.hasGiven(PermissionUtil.SEND_SMS_PERMISSION))
            switchSmsPermission.setChecked(true);
        if (permissionUtil.hasGiven(PermissionUtil.CAMERA_PERMISSION))
            switchCameraPermission.setChecked(true);
        if (permissionUtil.hasGiven(PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION))
            switchFilePermission.setChecked(true);
        if (permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS))
            switchNotificationAccess.setChecked(true);
        if (permissionUtil.hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS))
            switchOverlayAccess.setChecked(true);


    }


    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked) {
                switch (buttonView.getId()) {
                    case R.id.switchCallPermission:

                        new TedPermission(SiempoPermissionActivity.this)
                                .setPermissionListener(permissionlistener)
                                .setDeniedMessage(R.string.permission_rejected_text)
                                .setPermissions(Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_CALL_LOG)
                                .check();

                        break;

                    case R.id.switchContactPermission:

                        new TedPermission(SiempoPermissionActivity.this)
                                .setPermissionListener(permissionlistener)
                                .setDeniedMessage(R.string.permission_rejected_text)
                                .setPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
                                .check();

                        break;
                    case R.id.switchCameraPermission:

                        new TedPermission(SiempoPermissionActivity.this)
                                .setPermissionListener(permissionlistener)
                                .setDeniedMessage(R.string.permission_rejected_text)
                                .setPermissions(Manifest.permission.CAMERA)

                                .check();

                        break;
                    case R.id.switchFilePermission:

                        new TedPermission(SiempoPermissionActivity.this)
                                .setPermissionListener(permissionlistener)
                                .setDeniedMessage(R.string.permission_rejected_text)
                                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                                .check();

                        break;
                    case R.id.switchSmsPermission:

                        new TedPermission(SiempoPermissionActivity.this)
                                .setPermissionListener(permissionlistener)
                                .setDeniedMessage(R.string.permission_rejected_text)
                                .setPermissions(Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.RECEIVE_MMS)
                                .check();

                        break;

                }
            } else {
                startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName())), PermissionUtil.APP_PERMISSION);
            }

        }
    };


    @TargetApi(22)
    @CheckedChange
    void switchNotificationAccess(CompoundButton btn, boolean isChecked) {
        if (isChecked) {
            if (!new PermissionUtil(this).hasGiven(PermissionUtil.NOTIFICATION_ACCESS)) {
                startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), PermissionUtil.NOTIFICATION_ACCESS);
            }
        } else {
            startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), PermissionUtil.NOTIFICATION_ACCESS);
        }
    }

    @TargetApi(23)
    @CheckedChange
    void switchOverlayAccess(CompoundButton btn, boolean isChecked) {
        if (isChecked) {
            if (!Settings.canDrawOverlays(SiempoPermissionActivity.this)) {
                Toast.makeText(SiempoPermissionActivity.this, R.string.msg_overlay_settings, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PermissionUtil.DRAWING_OVER_OTHER_APPS);
            }
        } else {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), PermissionUtil.DRAWING_OVER_OTHER_APPS);
        }
    }


    @Click(R.id.btnContinue)
    void myButtonWasClicked() {

        if (permissionUtil.hasGiven(PermissionUtil.CONTACT_PERMISSION) && permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION) && permissionUtil.hasGiven(PermissionUtil.SEND_SMS_PERMISSION) &&
                permissionUtil.hasGiven(PermissionUtil.CAMERA_PERMISSION) && permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS) && permissionUtil.hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS)) {
            launcher3Prefs.isPermissionGivenAndContinued().put(true);
            finish();


        } else {
            UIUtils.toastShort(SiempoPermissionActivity.this, R.string.grant_all_to_proceed_text);
        }

    }


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            if (!deniedPermissions.isEmpty()) {
                if (deniedPermissions.contains(Manifest.permission.SEND_SMS)) {
                    switchSmsPermission.setOnCheckedChangeListener(null);
                    switchSmsPermission.setChecked(false);
                    switchSmsPermission.setOnCheckedChangeListener(onCheckedChangeListener);

                }
                if (deniedPermissions.contains(Manifest.permission.CAMERA)) {
                    switchCameraPermission.setOnCheckedChangeListener(null);
                    switchCameraPermission.setChecked(false);
                    switchCameraPermission.setOnCheckedChangeListener(onCheckedChangeListener);
                }
                if (deniedPermissions.contains(Manifest.permission.READ_CONTACTS)) {
                    switchContactPermission.setOnCheckedChangeListener(null);
                    switchContactPermission.setChecked(false);
                    switchContactPermission.setOnCheckedChangeListener(onCheckedChangeListener);
                }
                if (deniedPermissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    switchFilePermission.setOnCheckedChangeListener(null);
                    switchFilePermission.setChecked(false);
                    switchFilePermission.setOnCheckedChangeListener(onCheckedChangeListener);

                }
                if (deniedPermissions.contains(Manifest.permission.CALL_PHONE)) {
                    switchCallPermission.setOnCheckedChangeListener(null);
                    switchCallPermission.setChecked(false);
                    switchCallPermission.setOnCheckedChangeListener(onCheckedChangeListener);

                }

                UIUtils.toastShort(SiempoPermissionActivity.this, "Permission denied: " + deniedPermissions.get(0));
            }
        }
    };


    @OnActivityResult(PermissionUtil.NOTIFICATION_ACCESS)
    void onResultNotificationAccess(int resultCode) {
        if (!new PermissionUtil(this).hasGiven(PermissionUtil.NOTIFICATION_ACCESS)) {
            switchNotificationAccess.setChecked(false);
        } else {
            switchNotificationAccess.setChecked(true);
        }
    }

    @OnActivityResult(PermissionUtil.DRAWING_OVER_OTHER_APPS)
    void onResultDrawingAccess(int resultCode) {
        if (!new PermissionUtil(this).hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS)) {
            switchOverlayAccess.setChecked(false);
        } else {
            switchOverlayAccess.setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (permissionUtil.hasGiven(PermissionUtil.CONTACT_PERMISSION)
                && permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION) && permissionUtil.hasGiven(PermissionUtil.SEND_SMS_PERMISSION)
                && permissionUtil.hasGiven(PermissionUtil.CAMERA_PERMISSION) && permissionUtil.hasGiven(PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION)
                && permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS) && permissionUtil.hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS)
                ) {
            UIUtils.toastShort(SiempoPermissionActivity.this, R.string.permission_proceed_text);
        }
        else
        {

            UIUtils.toastShort(SiempoPermissionActivity.this, R.string.grant_all_to_proceed_text);
        }

    }


}
