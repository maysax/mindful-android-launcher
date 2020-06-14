package co.siempo.phone.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;

import co.siempo.phone.R;
import co.siempo.phone.fragments.AppMenuFragment;
import co.siempo.phone.fragments.TempoSettingsFragment;
import co.siempo.phone.utils.PrefSiempo;

public class SettingsActivity extends CoreActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tempo_settings);
        if (getIntent().hasExtra("FlagApp")) {
            loadFragment(AppMenuFragment.newInstance(true), R.id.tempoView, "main");
        } else {
            loadFragment(new TempoSettingsFragment(), R.id.tempoView, "main");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusBar();
    }

    private void statusBar() {
        final boolean isEnable = PrefSiempo.getInstance(this).read(PrefSiempo.DEFAULT_NOTIFICATION_ENABLE, true);
        if (isEnable) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            decorView.setFitsSystemWindows(true);

            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        if (PrefSiempo.getInstance(SettingsActivity.this).read(PrefSiempo.DEFAULT_NOTIFICATION_ENABLE, true)) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    View decorView = getWindow().getDecorView();
                                    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                                    decorView.setSystemUiVisibility(uiOptions);
                                    decorView.setFitsSystemWindows(true);
                                }
                            }, 3000);
                        }
                    }
                }
            });
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
        statusBarColor();
    }

    private void statusBarColor() {
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Window window = getWindow();
                window.setStatusBarColor(ContextCompat.getColor(SettingsActivity.this ,R.color.green_solid));
                window.setNavigationBarColor(ContextCompat.getColor(SettingsActivity.this ,R.color.green_solid));
            }
        },1000);*/
    }
}