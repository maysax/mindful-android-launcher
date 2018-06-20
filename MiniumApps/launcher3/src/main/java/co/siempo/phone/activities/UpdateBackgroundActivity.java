package co.siempo.phone.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;

import co.siempo.phone.R;
import co.siempo.phone.event.NotifyBackgroundChange;
import co.siempo.phone.event.NotifyBackgroundToService;
import co.siempo.phone.utils.PermissionUtil;
import co.siempo.phone.utils.PrefSiempo;
import de.greenrobot.event.EventBus;

public class UpdateBackgroundActivity extends CoreActivity {

    String strImage;
    private Toolbar toolbar;
    private PermissionUtil permissionUtil;
    private ImageView imageView;
    private CircularProgressDrawable circularProgressDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_background);
        permissionUtil = new PermissionUtil(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.update_background);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
        Intent imageIntent = getIntent();
        imageView = findViewById(R.id.imageView);
        circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(8f);
        circularProgressDrawable.setCenterRadius(80f);
        circularProgressDrawable.setColorSchemeColors(Color.parseColor("#448AFF"));
        circularProgressDrawable.start();

        if (imageIntent.getExtras() != null && imageIntent.hasExtra("imageUri")) {
            strImage = imageIntent.getExtras().getString("imageUri");
            checkPermissionAndDisplay(this, strImage);
        }
    }

    private void checkPermissionAndDisplay(final Context context, final String strImage) {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !permissionUtil.hasGiven
                (PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION))) {
            try {
                TedPermission.with(context)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Glide.with(context)
                                        .load(Uri.fromFile(new File(strImage)))
                                        .placeholder(circularProgressDrawable)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(imageView);
                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                startActivity(new Intent(UpdateBackgroundActivity.this,
                                        SettingsActivity_.class).addFlags(Intent
                                        .FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }
                        })
                        .setDeniedMessage(R.string.msg_permission_denied)
                        .setPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest
                                        .permission
                                        .READ_EXTERNAL_STORAGE})
                        .check();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Glide.with(context)
                    .load(Uri.fromFile(new File(strImage)))
                    .placeholder(circularProgressDrawable)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageView);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED, new Intent());
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_email_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.tick);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                PrefSiempo.getInstance(UpdateBackgroundActivity.this).write(PrefSiempo
                        .DEFAULT_BAG, strImage);
                PrefSiempo.getInstance(UpdateBackgroundActivity.this).write(PrefSiempo.DEFAULT_BAG_ENABLE, true);
                setResult(Activity.RESULT_OK, new Intent());
                EventBus.getDefault().postSticky(new NotifyBackgroundChange(true));
                EventBus.getDefault().post(new NotifyBackgroundToService(true));
                finish();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
