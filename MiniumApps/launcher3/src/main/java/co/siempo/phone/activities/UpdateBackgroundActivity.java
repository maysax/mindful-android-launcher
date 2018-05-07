package co.siempo.phone.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import co.siempo.phone.R;
import co.siempo.phone.utils.PrefSiempo;

public class UpdateBackgroundActivity extends CoreActivity {

    String strImage;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_background);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.update_background);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
        Intent imageIntent = getIntent();
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        if (imageIntent.getExtras() != null && imageIntent.hasExtra("imageUri")) ;
        {
            strImage = imageIntent.getExtras().getString("imageUri");
            Glide.with(this)
                    .load(Uri.fromFile(new File(strImage)))
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
                setResult(Activity.RESULT_OK, new Intent());
                finish();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
