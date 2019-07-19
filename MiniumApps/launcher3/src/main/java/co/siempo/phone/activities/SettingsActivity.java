package co.siempo.phone.activities;

import android.os.Handler;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import co.siempo.phone.R;
import co.siempo.phone.fragments.AppMenuFragment;
import co.siempo.phone.fragments.TempoSettingsFragment_;
import co.siempo.phone.utils.PrefSiempo;

@EActivity(R.layout.activity_tempo_settings)
public class SettingsActivity extends CoreActivity {

    @AfterViews
    void afterViews() {
        if (getIntent().hasExtra("FlagApp")) {
            loadFragment(AppMenuFragment.newInstance(true), R.id.tempoView, "main");
        } else {
            loadFragment(TempoSettingsFragment_.builder().build(), R.id.tempoView, "main");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusBar();
    }

    private void statusBar()
    {
        final boolean isEnable = PrefSiempo.getInstance(this).read(PrefSiempo.DEFAULT_NOTIFICATION_ENABLE, true);
        if(isEnable)
        {
            View decorView =  getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            decorView.setFitsSystemWindows(true);

            decorView.setOnSystemUiVisibilityChangeListener
                    (new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                if(PrefSiempo.getInstance(SettingsActivity.this).read(PrefSiempo.DEFAULT_NOTIFICATION_ENABLE, true))
                                {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            View decorView =  getWindow().getDecorView();
                                            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                                            decorView.setSystemUiVisibility(uiOptions);
                                            decorView.setFitsSystemWindows(true);
                                        }
                                    },3000);
                                }
                            }
                        }
                    });

        }else
        {
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