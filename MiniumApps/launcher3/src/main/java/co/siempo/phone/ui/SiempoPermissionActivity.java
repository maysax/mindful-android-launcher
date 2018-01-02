package co.siempo.phone.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

import co.siempo.phone.MainActivity;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.util.PermissionUtil;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

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
    @ViewById
    TextView txtPermissionLabel;

    @ViewById
    TableRow tblLocation;
    @ViewById
    TableRow tblCalls;
    @ViewById
    TableRow tblContact;
    @ViewById
    TableRow tblSMS;
    @ViewById
    TableRow tblCamera;
    @ViewById
    TableRow tblNotification;
    @ViewById
    TableRow tblDrawOverlay;
    @ViewById
    TableRow tblStorage;
    @Pref
    Launcher3Prefs_ launcher3Prefs;
    CompoundButton.OnClickListener onClickListener = new CompoundButton.OnClickListener()

    {
        @Override
        public void onClick(View v) {

            Switch aSwitch = (Switch) v;
            if (aSwitch.isChecked()) {
                UIUtils.toastShort(SiempoPermissionActivity.this, R.string.runtime_permission_text);

            }
            startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName())), PermissionUtil.APP_PERMISSION);


        }
    };
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            if (!deniedPermissions.isEmpty()) {
                if (deniedPermissions.contains(Manifest.permission.SEND_SMS)) {
                    switchSmsPermission.setChecked(false);

                }
                if (deniedPermissions.contains(Manifest.permission.CAMERA)) {
                    switchCameraPermission.setChecked(false);
                }
                if (deniedPermissions.contains(Manifest.permission.READ_CONTACTS)) {
                    switchContactPermission.setChecked(false);
                }
                if (deniedPermissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    switchFilePermission.setChecked(false);

                }
                if (deniedPermissions.contains(Manifest.permission.CALL_PHONE)) {
                    switchCallPermission.setChecked(false);

                }

                UIUtils.toastShort(SiempoPermissionActivity.this, "Permission denied: " + deniedPermissions.get(0));
            }
        }
    };
    private PermissionUtil permissionUtil;
    private boolean isFromHome;

    @AfterViews
    void afterViews() {
        permissionUtil = new PermissionUtil(this);
        setSupportActionBar(toolbar);
        switchSmsPermission.setOnClickListener(onClickListener);
        switchContactPermission.setOnClickListener(onClickListener);
        switchCameraPermission.setOnClickListener(onClickListener);
        switchCallPermission.setOnClickListener(onClickListener);
        switchFilePermission.setOnClickListener(onClickListener);

        Intent intent = getIntent();
        if (intent != null) {
            isFromHome = intent.getBooleanExtra(MainActivity.IS_FROM_HOME, false);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();


        if (permissionUtil.hasGiven(PermissionUtil.CONTACT_PERMISSION)) {
            switchContactPermission.setChecked(true);
        } else {
            switchContactPermission.setChecked(false);
        }
        if (permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION)) {
            switchCallPermission.setChecked(true);
        } else {
            switchCallPermission.setChecked(false);
        }
        if (permissionUtil.hasGiven(PermissionUtil.SEND_SMS_PERMISSION)) {
            switchSmsPermission.setChecked(true);
        } else {
            switchSmsPermission.setChecked(false);
        }
        if (permissionUtil.hasGiven(PermissionUtil.CAMERA_PERMISSION)) {
            switchCameraPermission.setChecked(true);
        } else {
            switchCameraPermission.setChecked(false);
        }
        if (permissionUtil.hasGiven(PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION)) {
            switchFilePermission.setChecked(true);
        } else {
            switchFilePermission.setChecked(false);
        }
        if (permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS)) {
            switchNotificationAccess.setChecked(true);
        } else {
            switchNotificationAccess.setChecked(false);
        }
        if (permissionUtil.hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS)) {
            switchOverlayAccess.setChecked(true);
        } else {
            switchOverlayAccess.setChecked(false);
        }


        if (isFromHome) {
            switchContactPermission.setVisibility(View.VISIBLE);
            switchCallPermission.setVisibility(View.VISIBLE);
            switchSmsPermission.setVisibility(View.VISIBLE);
            switchCameraPermission.setVisibility(View.VISIBLE);
            switchFilePermission.setVisibility(View.VISIBLE);
            switchNotificationAccess.setVisibility(View.VISIBLE);
            switchOverlayAccess.setVisibility(View.VISIBLE);
            btnContinue.setVisibility(View.VISIBLE);
            tblLocation.setVisibility(View.GONE);
            txtPermissionLabel.setText(getString(R.string.permission_title));

            if (Build.VERSION.SDK_INT >= 23) {
                tblContact.setVisibility(View.VISIBLE);
                tblCalls.setVisibility(View.VISIBLE);
                tblCamera.setVisibility(View.VISIBLE);
                tblDrawOverlay.setVisibility(View.VISIBLE);
                tblStorage.setVisibility(View.VISIBLE);
                tblNotification.setVisibility(View.VISIBLE);
                tblSMS.setVisibility(View.VISIBLE);
            } else {
                tblContact.setVisibility(View.GONE);
                tblCalls.setVisibility(View.GONE);
                tblCamera.setVisibility(View.GONE);
                tblDrawOverlay.setVisibility(View.GONE);
                tblStorage.setVisibility(View.GONE);
                tblNotification.setVisibility(View.VISIBLE);
                tblSMS.setVisibility(View.GONE);
            }
        } else {
            switchContactPermission.setVisibility(View.GONE);
            switchCallPermission.setVisibility(View.GONE);
            switchSmsPermission.setVisibility(View.GONE);
            switchCameraPermission.setVisibility(View.GONE);
            switchFilePermission.setVisibility(View.GONE);
            switchNotificationAccess.setVisibility(View.GONE);
            switchOverlayAccess.setVisibility(View.GONE);
            btnContinue.setVisibility(View.GONE);
            if (permissionUtil.hasGiven(PermissionUtil.LOCATION_PERMISSION)) {
                tblLocation.setVisibility(View.VISIBLE);
            } else {
                tblLocation.setVisibility(View.GONE);
            }
            txtPermissionLabel.setText(getString(R.string.permission_siempo_alpha_title));
        }

        if (isFromHome && permissionUtil.hasGiven(PermissionUtil
                .CONTACT_PERMISSION) &&
                permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION) && permissionUtil.hasGiven(PermissionUtil.SEND_SMS_PERMISSION) &&
                permissionUtil.hasGiven(PermissionUtil.CAMERA_PERMISSION) && permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS) && permissionUtil.hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS)) {
            finish();
        }
    }

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
        if (Build.VERSION.SDK_INT >= 23) {
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
    }

    @Click(R.id.btnContinue)
    void myButtonWasClicked() {
        if (permissionUtil.hasGiven(PermissionUtil.CONTACT_PERMISSION) && permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION) && permissionUtil.hasGiven(PermissionUtil.SEND_SMS_PERMISSION) &&
                permissionUtil.hasGiven(PermissionUtil.CAMERA_PERMISSION) && permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS) && permissionUtil.hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS)) {
//            launcher3Prefs.isPermissionGivenAndContinued().put(true);
            finish();
        } else {
            UIUtils.toastShort(SiempoPermissionActivity.this, R.string.grant_all_to_proceed_text);
        }

    }

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
        if (isFromHome) {
            UIUtils.toastShort(SiempoPermissionActivity.this, R.string.permission_proceed_text);
        } else {
            super.onBackPressed();
        }

    }


}
