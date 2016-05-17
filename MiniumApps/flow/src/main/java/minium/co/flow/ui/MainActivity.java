package minium.co.flow.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.service.notification.NotificationListenerService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.KeyUp;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.Random;

import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;
import minium.co.flow.NotificationListener;
import minium.co.flow.NotificationListener_;
import minium.co.flow.R;
import minium.co.flow.utils.ServiceUtils;

@EActivity(resName = "activity_main")
public class MainActivity extends CoreActivity {

    @ViewById
    VerticalProgressBar vpBar;

    private boolean isAnimationRunning;
    private boolean isServiceRunning;
    private float progress;
    private final float SPAN = 60 * 1000f;
    private final float INTERVAL = 15 * 1000f;
    private final int ANIMATION_DURATION = 500;

    @AfterViews
    void afterViews() {
        progress = SPAN;
        setPercentage(1);
    }

    @UiThread(delay = 1000L)
    void updateUI() {
        progress += 1000;
        Tracer.d("updateUI " + progress);
        animate();
        if (progress < SPAN)
            updateUI();
        else {
            playSound();
            NotificationListener_.intent(this).extra("start", false).start();
            isServiceRunning = false;
            finish();
        }
    }

    void playSound() {
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(this, defaultRingtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    mp.release();
                }
            });
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isEnabled(this)) {
            UIUtils.confirm(this, "Minium notification service is not enabled. Please allow Minium to access notification service", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
            });
        }
    }

    /** @return True if {@link NotificationListener} is enabled. */
    public static boolean isEnabled(Context mContext) {
        return ServiceUtils.isNotificationListenerServiceRunning(mContext, NotificationListener_.class);
    }

    private void animate() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(vpBar, "percent", vpBar.getPercent(), progress / SPAN));
        set.setDuration(ANIMATION_DURATION);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimationRunning = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationRunning = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    private void setPercentage(int percentage) {
        vpBar.setSmoothPercent(percentage, ANIMATION_DURATION);
    }

    void onVolumeUpKeyPressed() {
        Tracer.d("onKeyUp: Volume up");
        if (progress > 0) {
            if (!isServiceRunning) {
                NotificationListener_.intent(this).extra("start", true).start();
                updateUI();
                isServiceRunning = true;
            }
            progress -= INTERVAL;
            animate();
        }
    }

    boolean onBackKeyPressed(int keyCode, KeyEvent keyEvent) {
        if (isServiceRunning) {
            Tracer.d("onKeyUp: Back");
            return true;
        }
        else
            return super.onKeyUp(keyCode, keyEvent);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            {
                onVolumeUpKeyPressed();
                return true;
            }
            case KeyEvent.KEYCODE_BACK:
            {
                return onBackKeyPressed(keyCode, keyEvent);
            }
        }
        return super.onKeyUp(keyCode, keyEvent);
    }
}
