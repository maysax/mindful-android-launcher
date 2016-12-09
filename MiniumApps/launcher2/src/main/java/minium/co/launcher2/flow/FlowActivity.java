package minium.co.launcher2.flow;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Vibrator;
import android.view.Display;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.ThemeUtils;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.notificationscheduler.NotificationScheduleReceiver_;
import minium.co.launcher2.ui.TopFragment_;
import minium.co.launcher2.ui.widget.VerticalProgressBar;
import minium.co.launcher2.utils.AudioUtils;
import minium.co.core.util.ServiceUtils;

@Fullscreen
@EActivity(R.layout.activity_flow)
public class FlowActivity extends CoreActivity {

    @ViewById
    ViewGroup parentLayout;

    @ViewById
    FrameLayout statusView;

    @ViewById
    VerticalProgressBar vpBar;

    @ViewById
    TextView txtTimer;

    @ViewById
    TextView txtRemainingTime;

    @Pref
    DroidPrefs_ prefs;

    @SystemService
    Vibrator vibrator;

    private boolean isAnimationRunning;
    private boolean isServiceRunning = false;
    private float progress;
    private float flowMaxTimeLimitMillis = 4 * 60 * 1000f;
    private float flowSegmentDurationMillis = 4 * 15 * 1000f;
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
        loadConfigValues();

        statusView.setBackgroundColor(ThemeUtils.getPrimaryDarkColor(this));
        vpBar.setBackgroundColor(ThemeUtils.getPrimaryColor(this));
        vpBar.setFillColor(ThemeUtils.getPrimaryDarkColor(this));

        loadTopView();
        SCREEN_HEIGHT = getScreenHeight() - UIUtils.dpToPx(this, 20);   // decreasing status bar height 20dp
        Tracer.d("Screen height: " + SCREEN_HEIGHT);
        progress = flowMaxTimeLimitMillis;
        setPercentage(1);

        prefs.isFlowRunning().put(true);
    }

    void loadConfigValues() {
        flowMaxTimeLimitMillis = prefs.flowMaxTimeLimitMillis().get();
        flowSegmentDurationMillis = prefs.flowSegmentDurationMillis().get();
    }

    void loadTopView() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
    }

    @UiThread(delay = 100L)
    void updateUI() {
        progress += 100;
        // Tracer.d("updateUI " + progress);
        animate();
        if (progress < flowMaxTimeLimitMillis)
            updateUI();
        else {
            endFlow();
        }
    }

    void endFlow() {
        SiempoNotificationService_.intent(this).extra("start", false).start();
        prefs.isFlowRunning().put(false);
        isServiceRunning = false;
        sendBroadcast(new Intent(this, NotificationScheduleReceiver_.class));
        onCompletion();
    }

    @UiThread(delay = 300)
    void onCompletion() {
        vibrator.vibrate(800);
        new AudioUtils().playNotificationSound(this);
        finish();
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
        } else if (!isServiceRunning) {
            onVolumeUpKeyPressed();
        }
    }

    @Click
    void parentLayout() {
        UIUtils.toastShort(this, "Press Volume-Up key to increase by 15 min");
    }

    /** @return True if {@link SiempoNotificationService} is enabled. */
    public static boolean isEnabled(Context mContext) {
        return ServiceUtils.isNotificationListenerServiceRunning(mContext, SiempoNotificationService_.class);
    }

    private void animate() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(vpBar, "percent", vpBar.getPercent(), progress / flowMaxTimeLimitMillis),
                ObjectAnimator.ofFloat(txtTimer, "y", SCREEN_HEIGHT - (SCREEN_HEIGHT * (progress / flowMaxTimeLimitMillis))));
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

        long gap = (long) (flowMaxTimeLimitMillis - progress);
        txtRemainingTime.setText(String.format(Locale.US, "%dm", (gap / 60000) + (gap % 60000 == 0 ? 0 : 1)));
    }

    private void setPercentage(int percentage) {
        vpBar.setSmoothPercent(percentage, ANIMATION_DURATION);
    }

    void onVolumeUpKeyPressed() {
        Tracer.d("onKeyUp: Volume up");
        if (progress > 0 && !isAnimationRunning) {
            if (!isServiceRunning) {
                SiempoNotificationService_.intent(this).extra("start", true).start();
                updateUI();
                isServiceRunning = true;
            }
            progress -= flowSegmentDurationMillis;
            progress = Math.max(-1, progress);

            long gap = (long) (flowMaxTimeLimitMillis - progress);
            long target = Calendar.getInstance().getTimeInMillis() + gap;
            txtTimer.setText(new SimpleDateFormat("h:mm a", Locale.US).format(new Date(target)));

            animate();
        }
    }

    boolean onBackKeyPressed(int keyCode, KeyEvent keyEvent) {
        if (isServiceRunning) {
            Tracer.d("onKeyUp: Back");
            UIUtils.confirm(this, "Want to end your flow?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    progress = flowMaxTimeLimitMillis;
                }
            });
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
