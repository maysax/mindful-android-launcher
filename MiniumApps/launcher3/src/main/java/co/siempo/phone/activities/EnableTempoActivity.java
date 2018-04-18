package co.siempo.phone.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toolbar;

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
                if (btnSubmit.getText().toString().equalsIgnoreCase("Enable Setting A")) {
                    startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), PermissionUtil.NOTIFICATION_ACCESS);
                } else if (btnSubmit.getText().toString().equalsIgnoreCase("Enable Setting B")) {
                    Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (btnSubmit.getText().toString().equalsIgnoreCase("TEMPO IS ENABLED!")) {
                    btnSubmit.setBackground(ContextCompat.getDrawable(EnableTempoActivity.this, R.drawable.button_bg_enable));
                    btnSubmit.setText("TEMPO IS ENABLED!");
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS)) {
            if (!PackageUtil.isSiempoLauncher(this)) {
                imgCenter.setBackground(ContextCompat.getDrawable(this, R.drawable.screenshottop1));
                imgStep.setBackground(ContextCompat.getDrawable(this, R.drawable.progress_meter_b));
                btnSubmit.setText("Enable Setting B");
            } else {
                imgCenter.setBackground(ContextCompat.getDrawable(this, R.drawable.screenshottop1));
                imgStep.setBackground(ContextCompat.getDrawable(this, R.drawable.progress_meter_c));
                btnSubmit.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg_enable));
                btnSubmit.setText("TEMPO IS ENABLED!");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                    }
                }, 1000);
            }
        } else {
            imgCenter.setBackground(ContextCompat.getDrawable(this, R.drawable.screenshottop));
            imgStep.setBackground(ContextCompat.getDrawable(this, R.drawable.progress_meter_a));
            btnSubmit.setText("Enable Setting A");
        }
    }
}
