package minium.co.flow.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import java.util.Random;

import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;
import minium.co.flow.NotificationListener;
import minium.co.flow.NotificationListener_;
import minium.co.flow.R;
import minium.co.flow.utils.ServiceUtils;

@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    @ViewById
    VerticalProgressBar vpBar;

    private boolean isAnimationRunning;
    private float progress;
    private final float SPAN = 60f;
    private final float INTERVAL = 15f; // 15 mins

    @AfterViews
    void afterViews() {
        progress = 60f;
        setPercentage(1);
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
        set.setDuration(500);
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
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    private void setPercentage(int percentage) {
        vpBar.setSmoothPercent(percentage, 500);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Tracer.d("onKeyUp: Volume up");

            if (!isAnimationRunning && progress > 0) {
                NotificationListener_.intent(this).extra("start", true).start();
//                notifService.requestInterruptionFilter(NotificationListenerService.INTERRUPTION_FILTER_NONE);
                progress -= INTERVAL;
                animate();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
