package co.minium.launcher3.call;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import co.minium.launcher3.R;
import co.minium.launcher3.ui.TopFragment_;
import minium.co.core.ui.CoreActivity;

@Fullscreen
@EActivity(R.layout.activity_call_log)
public class CallLogActivity extends CoreActivity {

    @AfterViews
    void afterViews() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
        loadFragment(CallLogFragment_.builder().build(), R.id.mainView, "main");
    }
}
