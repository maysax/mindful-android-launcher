package co.siempo.phone.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.utils.PermissionUtil;
import co.siempo.phone.utils.UIUtils;

public class SiempoPermissionActivity extends CoreActivity {
    Toolbar toolbar;
    Switch switchContactPermission;
    //Switch switchCallPermission;
    //Switch switchSmsPermission;
    Switch switchFilePermission;
    Switch switchNotificationAccess;
    Switch switchOverlayAccess;
    Switch switchControlAccessUsage;
    Button btnContinue;
    TextView txtPermissionLabel;
    TableRow tblLocation;
    //TableRow tblCalls;
    TableRow tblContact;
    //TableRow tblSMS;
    TableRow tblNotification;
    TableRow tblDrawOverlay;
    TableRow tblStorage;
    TableRow tblControlAccessUsage;
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Log.d("TAG", "Permission granted");
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            UIUtils.toast(SiempoPermissionActivity.this, "Permission denied");
            askForPermission(Constants.PERMISSIONS);
        }
    };
    CompoundButton.OnClickListener onClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                /*case R.id.tblCalls:
                    askForPermission(new String[]{
                            Manifest.permission.CALL_PHONE});
                    break;*/
                case R.id.tblContact:
                    askForPermission(new String[]{
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS});
                    break;
                /*case R.id.tblSMS:
                    askForPermission(new String[]{
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.READ_SMS});
                    break;*/
                case R.id.tblStorage:
                    askForPermission(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE});
                    break;
            }
        }
    };
    private PermissionUtil permissionUtil;
    private boolean isFromHome;
    private ProgressDialog pd;

    private void askForPermission(String[] PERMISSIONS) {
        try {
            TedPermission.with(SiempoPermissionActivity.this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(R.string.msg_permission_denied)
                    .setPermissions(PERMISSIONS)
                    .check();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        toolbar = findViewById(R.id.toolbar);
        switchContactPermission = findViewById(R.id.switchContactPermission);
        switchFilePermission = findViewById(R.id.switchFilePermission);
        switchNotificationAccess = findViewById(R.id.switchNotificationAccess);
        switchOverlayAccess = findViewById(R.id.switchOverlayAccess);
        switchControlAccessUsage = findViewById(R.id.switchControlAccessUsage);
        btnContinue = findViewById(R.id.btnContinue);
        txtPermissionLabel = findViewById(R.id.txtPermissionLabel);
        tblLocation = findViewById(R.id.tblLocation);
        tblContact = findViewById(R.id.tblContact);
        tblNotification = findViewById(R.id.tblNotification);
        tblDrawOverlay = findViewById(R.id.tblDrawOverlay);
        tblStorage = findViewById(R.id.tblStorage);
        tblControlAccessUsage = findViewById(R.id.tblControlAccessUsage);
        if (btnContinue != null) {
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SiempoPermissionActivity.this.myButtonWasClicked();
                }
            });
        }
        if (switchNotificationAccess != null) {
            ((CompoundButton) switchNotificationAccess).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SiempoPermissionActivity.this.switchNotificationAccess(buttonView, isChecked);
                }
            });
        }
        afterViews();
    }

    private void afterViews() {
        permissionUtil = new PermissionUtil(this);
        setSupportActionBar(toolbar);

        //tblSMS.setOnClickListener(onClickListener);
        tblContact.setOnClickListener(onClickListener);
        //tblCalls.setOnClickListener(onClickListener);
        tblStorage.setOnClickListener(onClickListener);

        //switchSmsPermission.setClickable(false);
        switchContactPermission.setClickable(false);
        //switchCallPermission.setClickable(false);
        switchFilePermission.setClickable(false);
        switchNotificationAccess.setClickable(false);

        tblNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchNotificationAccess.isChecked()) {
                    if (!new PermissionUtil(SiempoPermissionActivity.this)
                            .hasGiven(PermissionUtil
                                    .NOTIFICATION_ACCESS)) {

                        startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), PermissionUtil.NOTIFICATION_ACCESS);
                    }
                } else {

                    startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), PermissionUtil.NOTIFICATION_ACCESS);

                }
            }
        });


        Intent intent = getIntent();
        if (intent != null) {
            isFromHome = intent.getBooleanExtra(DashboardActivity.IS_FROM_HOME, false);
        }
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);


    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if (permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION)) {
            switchCallPermission.setChecked(true);
        } else {
            switchCallPermission.setChecked(false);
        }
        if (permissionUtil.hasGiven(PermissionUtil.SEND_SMS_PERMISSION)) {
            switchSmsPermission.setChecked(true);
        } else {
            switchSmsPermission.setChecked(false);
        }*/
        if (permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS)) {
            switchNotificationAccess.setChecked(true);
        } else {
            switchNotificationAccess.setChecked(false);
        }

        if (isFromHome) {
            //switchCallPermission.setVisibility(View.VISIBLE);
            switchFilePermission.setVisibility(View.VISIBLE);
            switchNotificationAccess.setVisibility(View.VISIBLE);
            switchOverlayAccess.setVisibility(View.VISIBLE);
            btnContinue.setVisibility(View.VISIBLE);
            tblLocation.setVisibility(View.GONE);
            txtPermissionLabel.setText(getString(R.string.permission_title));

            if (Build.VERSION.SDK_INT >= 23) {
                // tblCalls.setVisibility(View.VISIBLE);
                tblDrawOverlay.setVisibility(View.GONE);
                tblNotification.setVisibility(View.VISIBLE);
            } else {
                tblContact.setVisibility(View.GONE);
                //tblCalls.setVisibility(View.GONE);
                tblDrawOverlay.setVisibility(View.GONE);
                tblStorage.setVisibility(View.GONE);
                tblNotification.setVisibility(View.VISIBLE);
                //tblSMS.setVisibility(View.GONE);
            }
            if (hasUsageStatsPermission(this)) {
                tblControlAccessUsage.setVisibility(View.VISIBLE);
            } else {
                tblControlAccessUsage.setVisibility(View.GONE);
            }
        } else {
            switchContactPermission.setVisibility(View.GONE);
            //switchCallPermission.setVisibility(View.GONE);
            //switchSmsPermission.setVisibility(View.GONE);
            switchFilePermission.setVisibility(View.GONE);
            switchNotificationAccess.setVisibility(View.GONE);
            switchOverlayAccess.setVisibility(View.GONE);
            btnContinue.setVisibility(View.GONE);
            switchControlAccessUsage.setVisibility(View.GONE);

//            if (permissionUtil.hasGiven(PermissionUtil.LOCATION_PERMISSION)) {
//                tblLocation.setVisibility(View.VISIBLE);
//            } else {
//                tblLocation.setVisibility(View.GONE);
//            }
            txtPermissionLabel.setText(getString(R.string.permission_siempo_alpha_title));


            if (permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS)) {
                tblNotification.setVisibility(View.VISIBLE);
            }
            if (Build.VERSION.SDK_INT >= 23) {
                if (permissionUtil.hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS)) {
                    tblDrawOverlay.setVisibility(View.VISIBLE);
                }
                /*if (permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION)) {
                    tblCalls.setVisibility(View.VISIBLE);
                }
                if (permissionUtil.hasGiven(PermissionUtil.SEND_SMS_PERMISSION)) {
                    tblSMS.setVisibility(View.VISIBLE);
                }*/
                if (permissionUtil.hasGiven(PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION)) {
                    tblStorage.setVisibility(View.VISIBLE);
                }

                if (permissionUtil.hasGiven(PermissionUtil.CONTACT_PERMISSION)) {
                    tblContact.setVisibility(View.VISIBLE);
                }
            }

            if (hasUsageStatsPermission(this)) {
                tblControlAccessUsage.setVisibility(View.VISIBLE);
            } else {
                tblControlAccessUsage.setVisibility(View.GONE);
            }

        }
        if (isFromHome &&
                /*permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION)
                &&*/
                permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS)) {
            finish();
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (appOps != null) {
            mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), context.getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @TargetApi(22)
    void switchNotificationAccess(CompoundButton btn, boolean isChecked) {
        if (isChecked) {
            if (!new PermissionUtil(this).hasGiven(PermissionUtil.NOTIFICATION_ACCESS)) {
                startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), PermissionUtil.NOTIFICATION_ACCESS);
            }
        } else {
            startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), PermissionUtil.NOTIFICATION_ACCESS);
        }
    }

    void myButtonWasClicked() {
        if (/*permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION) &&*/
                permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS)) {
            finish();
        } else {
            UIUtils.toastShort(SiempoPermissionActivity.this, R.string.grant_all_to_proceed_text);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (!new PermissionUtil(this).hasGiven(PermissionUtil.NOTIFICATION_ACCESS)) {
                switchNotificationAccess.setChecked(false);
            } else {
                switchNotificationAccess.setChecked(true);
            }
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
