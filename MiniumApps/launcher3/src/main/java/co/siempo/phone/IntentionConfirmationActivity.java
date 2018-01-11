package co.siempo.phone;

import android.os.Handler;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.sharedpreferences.Pref;

import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@WindowFeature({Window.FEATURE_CONTENT_TRANSITIONS})
@EActivity(R.layout.activity_intention_confirmation)
public class IntentionConfirmationActivity extends CoreActivity {

    @Pref
    DroidPrefs_ droidPrefs;

    @ViewById
    TextView txtIntention;

    @ViewById
    LinearLayout linEditText;

    @AfterViews
    void afterViews() {
        linEditText.setTransitionName("linEditText");
        getWindow().setExitTransition(new Explode());
        txtIntention.setText(droidPrefs.defaultIntention().get());
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                supportFinishAfterTransition();

            }
        }, 2000);
    }
}
