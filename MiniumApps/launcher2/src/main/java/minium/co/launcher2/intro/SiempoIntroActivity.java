package minium.co.launcher2.intro;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.sharedpreferences.Pref;

import minium.co.core.app.DroidPrefs;
import minium.co.core.app.DroidPrefs_;
import minium.co.launcher2.MainActivity_;
import minium.co.launcher2.R;

/**
 * Created by Shahab on 1/9/2017.
 */

@Fullscreen
@EActivity
public class SiempoIntroActivity extends AppIntro2 {

    @Pref
    DroidPrefs_ prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setFadeAnimation();

        addSlide(AppIntroFragment.newInstance(getString(R.string.title_welcome_siempo), getString(R.string.msg_siempo_description), R.mipmap.ic_launcher, ContextCompat.getColor(this, R.color.bg_grey), ContextCompat.getColor(this, R.color.colorAccent), ContextCompat.getColor(this, R.color.black)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.title_welcome_siempo2), getString(R.string.msg_siempo_description), R.mipmap.ic_launcher, ContextCompat.getColor(this, R.color.bg_grey), ContextCompat.getColor(this, R.color.colorAccent), ContextCompat.getColor(this, R.color.black)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.title_welcome_siempo3), getString(R.string.msg_siempo_description), R.mipmap.ic_launcher, ContextCompat.getColor(this, R.color.bg_grey), ContextCompat.getColor(this, R.color.colorAccent), ContextCompat.getColor(this, R.color.black)));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        leave();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        leave();
    }

    private void leave() {
        prefs.hasShownIntroScreen().put(true);
        MainActivity_.intent(this).start();
    }
}
