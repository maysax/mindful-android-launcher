package co.minium.launcher3.ui;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import co.minium.launcher3.R;
import minium.co.core.ui.CoreActivity;

@Fullscreen
@EActivity(R.layout.activity_tempo)
public class TempoActivity extends CoreActivity {

    @AfterViews
    void afterViews() {
        loadFragment(TempoFragment_.builder().build(),R.id.mainView,"main");
        loadTopBar();
    }

    private void loadTopBar() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
    }
}
