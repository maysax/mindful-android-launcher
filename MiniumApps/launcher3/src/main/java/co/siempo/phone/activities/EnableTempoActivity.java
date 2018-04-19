package co.siempo.phone.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import co.siempo.phone.R;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PermissionUtil;

public class EnableTempoActivity extends CoreActivity {
    PermissionUtil permissionUtil;
    ImageView imgStep, imgCenter;
    Toolbar toolbar;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_tempo);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        permissionUtil = new PermissionUtil(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_gray_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgStep = findViewById(R.id.imgStep);
        imgCenter = findViewById(R.id.imgCenter);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (btnSubmit.getText().toString().equalsIgnoreCase(getString(R.string.enable_setting_a))) {
                        startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), PermissionUtil.NOTIFICATION_ACCESS);
                    } else if (btnSubmit.getText().toString().equalsIgnoreCase(getString(R.string.enable_setting_b))) {
                        Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else if (btnSubmit.getText().toString().equalsIgnoreCase(getString(R.string.tempo_enabled))) {
                        btnSubmit.setBackground(ContextCompat.getDrawable(EnableTempoActivity.this, R.drawable.button_bg_enable));
                        btnSubmit.setText(getString(R.string.tempo_enabled));
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                    }
                } else {
                    if (btnSubmit.getText().toString().equalsIgnoreCase(getString(R.string.enable_setting_a))) {
                        startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), PermissionUtil.NOTIFICATION_ACCESS);
                    } else if (btnSubmit.getText().toString().equalsIgnoreCase(getString(R.string.enable_setting_b))) {
                        askForPermission(new String[]{
                                Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE});
                    } else if (btnSubmit.getText().toString().equalsIgnoreCase(getString(R.string.enable_setting_c))) {
                        Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else if (btnSubmit.getText().toString().equalsIgnoreCase(getString(R.string.tempo_enabled))) {
                        btnSubmit.setBackground(ContextCompat.getDrawable(EnableTempoActivity.this, R.drawable.button_bg_enable));
                        btnSubmit.setText(getString(R.string.tempo_enabled));
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                    }
                }
            }
        });
    }

    private void askForPermission(String[] PERMISSIONS) {
        try {
            TedPermission.with(EnableTempoActivity.this)
                    .setPermissionListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            bindUi();
                        }

                        @Override
                        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                        }
                    })
                    .setDeniedMessage(R.string.msg_permission_denied)
                    .setPermissions(PERMISSIONS)
                    .check();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        bindUi();
    }

    private void bindUi() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (!permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS)) {
                imgCenter.setBackground(ContextCompat.getDrawable(this, R.drawable.screenshottop));
                imgStep.setBackground(ContextCompat.getDrawable(this, R.drawable.progress_a_lollipop));
                btnSubmit.setText(getString(R.string.enable_setting_a));
            } else if (!PackageUtil.isSiempoLauncher(this)) {
                imgCenter.setBackground(ContextCompat.getDrawable(this, R.drawable.screenshottop1));
                imgStep.setBackground(ContextCompat.getDrawable(this, R.drawable.progress_b_lollipop));
                btnSubmit.setText(getString(R.string.enable_setting_b));
            } else {
                imgCenter.setBackground(null);
                imgStep.setBackground(ContextCompat.getDrawable(this, R.drawable.progress_c_lollipop));
                btnSubmit.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg_enable));
                btnSubmit.setText(getString(R.string.tempo_enabled));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                    }
                }, 1000);
            }
        } else {
            if (!permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS)) {
                imgCenter.setBackground(ContextCompat.getDrawable(this, R.drawable.screenshottop));
                imgStep.setBackground(ContextCompat.getDrawable(this, R.drawable.progress_a));
                btnSubmit.setText(getString(R.string.enable_setting_a));
            } else if (!permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION)) {
                imgCenter.setBackground(null);
                imgStep.setBackground(ContextCompat.getDrawable(this, R.drawable.progress_b));
                btnSubmit.setText(getString(R.string.enable_setting_b));
            } else if (!PackageUtil.isSiempoLauncher(this)) {
                imgCenter.setBackground(ContextCompat.getDrawable(this, R.drawable.screenshottop1));
                imgStep.setBackground(ContextCompat.getDrawable(this, R.drawable.progress_c));
                btnSubmit.setText(getString(R.string.enable_setting_c));
            } else {
                imgCenter.setBackground(null);
                imgStep.setBackground(ContextCompat.getDrawable(this, R.drawable.progress_d));
                btnSubmit.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg_enable));
                btnSubmit.setText(getString(R.string.tempo_enabled));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                    }
                }, 1000);
            }
        }
    }
}
