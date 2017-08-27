package com.siempo.tracking.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.siempo.tracking.R;
import com.siempo.tracking.util.PermissionUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;


/**
 * Created by Shahab on 1/12/2017.
 */
@EActivity(R.layout.activity_permission)
public class PermissionActivity extends CoreActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    Switch switchAppPermission;

    @ViewById
    Switch switchNotificationAccess;

    @ViewById
    Switch switchUsageStatistics;

    @ViewById
    Switch switchDrawingOver;

    @AfterViews
    void afterViews() {
        setSupportActionBar(toolbar);

        PermissionUtil pu = new PermissionUtil(this);

        if (pu.hasGiven(PermissionUtil.APP_PERMISSION)) switchAppPermission.setChecked(true);
        if (pu.hasGiven(PermissionUtil.NOTIFICATION_ACCESS))
            switchNotificationAccess.setChecked(true);
        if (pu.hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS)) switchDrawingOver.setChecked(true);
        if (pu.hasGiven(PermissionUtil.USAGE_STATISTICS)) switchUsageStatistics.setChecked(true);

    }

    @CheckedChange
    void switchAppPermission(CompoundButton btn, boolean isChecked) {
        if (isChecked) {
            new TedPermission(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you deny this permission, the Siempo Tracking app will not function properly. You can change the permissions at any time in Settings > Permissions. You can also revoke the permissions completely by uninstalling the app.")
                    .setDeniedCloseButtonText("Dismiss")
                    .setGotoSettingButtonText("Update permissions")
                    .setPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check();
        } else {
            startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName())), PermissionUtil.APP_PERMISSION);
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

    @CheckedChange
    void switchUsageStatistics(CompoundButton btn, boolean isChecked) {
        if (isChecked) {
            if (!new PermissionUtil(this).hasGiven(PermissionUtil.USAGE_STATISTICS)) {
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), PermissionUtil.USAGE_STATISTICS);
            }
        } else {
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), PermissionUtil.USAGE_STATISTICS);
        }
    }

    @TargetApi(23)
    @CheckedChange
    void switchDrawingOver(CompoundButton btn, boolean isChecked) {
        if (isChecked) {
            if (!new PermissionUtil(this).hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PermissionUtil.DRAWING_OVER_OTHER_APPS);

            }
        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, PermissionUtil.DRAWING_OVER_OTHER_APPS);
        }

    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            if (!deniedPermissions.isEmpty()) {
                switchAppPermission.setChecked(false);
                UIUtils.toast(PermissionActivity.this, "Permission denied: " + deniedPermissions.get(0));
            }
        }
    };

    @OnActivityResult(PermissionUtil.APP_PERMISSION)
    void onResultAppPermission(int resultCode) {
        if (!new PermissionUtil(this).hasGiven(PermissionUtil.APP_PERMISSION)) {
            switchAppPermission.setChecked(false);
            UIUtils.toast(PermissionActivity.this, "Notification access permission is not given");
        } else {
            switchAppPermission.setChecked(true);
        }
    }

    @OnActivityResult(PermissionUtil.NOTIFICATION_ACCESS)
    void onResultNotificationAccess(int resultCode) {
        if (!new PermissionUtil(this).hasGiven(PermissionUtil.NOTIFICATION_ACCESS)) {
            switchNotificationAccess.setChecked(false);
            UIUtils.toast(PermissionActivity.this, "Notification access permission is not given");
        } else {
            switchNotificationAccess.setChecked(true);
        }
    }

    @OnActivityResult(PermissionUtil.USAGE_STATISTICS)
    void onResultUsageStatistics(int resultCode) {
        if (!new PermissionUtil(this).hasGiven(PermissionUtil.USAGE_STATISTICS)) {
            switchUsageStatistics.setChecked(false);
            UIUtils.toast(PermissionActivity.this, "Usage statistics permission is not given");
        } else {
            switchUsageStatistics.setChecked(true);
        }
    }

    @OnActivityResult(PermissionUtil.DRAWING_OVER_OTHER_APPS)
    void onResultDrawOverlays(int resultCode) {
        if (!new PermissionUtil(this).hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS)) {
            switchDrawingOver.setChecked(false);
            UIUtils.toast(PermissionActivity.this, "Draw overlays permission is not given");
        } else {
            switchDrawingOver.setChecked(true);
        }
    }
}
