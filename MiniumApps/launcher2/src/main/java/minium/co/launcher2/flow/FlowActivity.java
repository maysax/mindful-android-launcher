package minium.co.launcher2.flow;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.ThemeUtils;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.ui.TopFragment_;
import minium.co.launcher2.ui.widget.VerticalProgressBar;
import minium.co.launcher2.utils.ServiceUtils;

@Fullscreen
@EActivity(R.layout.activity_flow)
public class FlowActivity extends CoreActivity {

    @ViewById
    FrameLayout statusView;

    @ViewById
    VerticalProgressBar vpBar;

    @ViewById
    TextView txtTimer;

    @Extra
    boolean isVolumeUpInit = false;

    private boolean isAnimationRunning;
    private boolean isServiceRunning = false;
    private float progress;
    private final float SPAN = 60 * 1000f;
    private final float INTERVAL = 15 * 1000f;
    private final int ANIMATION_DURATION = 100;
    private int SCREEN_HEIGHT;

    private int getScreenHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    @AfterViews
    void afterViews() {
        statusView.setBackgroundColor(ThemeUtils.getPrimaryDarkColor(this));
        vpBar.setBackgroundColor(ThemeUtils.getPrimaryColor(this));
        vpBar.setFillColor(ThemeUtils.getPrimaryDarkColor(this));

        loadTopView();
        SCREEN_HEIGHT = getScreenHeight() - UIUtils.dpToPx(this, 20);   // decreasing status bar height 20dp
        Tracer.d("Screen height: " + SCREEN_HEIGHT);
        progress = SPAN;
        setPercentage(1);
    }

    void loadTopView() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView);
    }

    @UiThread(delay = 100L)
    void updateUI() {
        progress += 100;
        // Tracer.d("updateUI " + progress);
        animate();
        if (progress < SPAN)
            updateUI();
        else {
            FlowNotificationService_.intent(this).extra("start", false).start();
            isServiceRunning = false;
            onCompletion();
        }
    }

    @UiThread(delay = 300)
    void onCompletion() {
        playSound();
        finish();
    }

    void playSound() {
        Tracer.i("Playing sound...");
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.reset();
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
            Tracer.i("Playing sound completed");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Tracer.e(e, e.getMessage());
        } catch (SecurityException e) {
            e.printStackTrace();
            Tracer.e(e, e.getMessage());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Tracer.e(e, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Tracer.e(e, e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isEnabled(this)) {
            UIUtils.confirm(this, "Ebb flow manager service is not enabled. Please allow Ebb flow manager to access notification service", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
            });
        } else {
            if (isVolumeUpInit) onVolumeUpKeyPressed();
        }
        isVolumeUpInit = false;
    }

    /** @return True if {@link FlowNotificationService} is enabled. */
    public static boolean isEnabled(Context mContext) {
        return ServiceUtils.isNotificationListenerServiceRunning(mContext, FlowNotificationService_.class);
    }

    private void animate() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(vpBar, "percent", vpBar.getPercent(), progress / SPAN),
                ObjectAnimator.ofFloat(txtTimer, "y", SCREEN_HEIGHT - (SCREEN_HEIGHT * (progress / SPAN))));
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
        if (progress > 0 && !isAnimationRunning) {
            if (!isServiceRunning) {
                FlowNotificationService_.intent(this).extra("start", true).start();
                updateUI();
                isServiceRunning = true;
            }
            progress -= INTERVAL;
            progress = Math.max(-1, progress);

            long gap = (long) (SPAN - progress);
            long target = Calendar.getInstance().getTimeInMillis() + gap;
            txtTimer.setText(new SimpleDateFormat("hh:mm:ss a", Locale.US).format(new Date(target)));

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
//            case KeyEvent.KEYCODE_VOLUME_UP:
//            {
//                onVolumeUpKeyPressed();
//                return true;
//            }
            case KeyEvent.KEYCODE_BACK:
            {
                return onBackKeyPressed(keyCode, keyEvent);
            }
        }
        return super.onKeyUp(keyCode, keyEvent);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                onVolumeUpKeyPressed();
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}
