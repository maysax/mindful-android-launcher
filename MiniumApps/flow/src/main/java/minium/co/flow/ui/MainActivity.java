package minium.co.flow.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import minium.co.core.ui.CoreActivity;
import minium.co.flow.R;

@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    @ViewById
    VerticalProgressBar vpBar;

    private boolean isAnimationRunning;
    private int progress;

    private void animate() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(vpBar, "percent", 0, progress / 100f));
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
        vpBar.setSmoothPercent(percentage);
    }
}
