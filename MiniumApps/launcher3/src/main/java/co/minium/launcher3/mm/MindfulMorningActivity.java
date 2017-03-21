package co.minium.launcher3.mm;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import co.minium.launcher3.R;
import co.minium.launcher3.event.MindfulMorgingEventStart;
import co.minium.launcher3.event.PauseStartEvent;
import co.minium.launcher3.pause.PauseActivatedFragment_;
import de.greenrobot.event.Subscribe;
import minium.co.core.ui.CoreActivity;

/**
 * Created by tkb on 2017-03-17.
 */
@Fullscreen
@EActivity(R.layout.mindful_morning_activity)

public class MindfulMorningActivity extends CoreActivity {

    @AfterViews
    public void afterViews(){

        loadFragment(new MindfulMorningFragment_(),R.id.mainView,"Main");

    }

    @Subscribe
    public void MindfulMorgingEventStart(MindfulMorgingEventStart event) {
        loadFragment(PauseActivatedFragment_.builder().maxMillis(event.getMaxMillis()).build(), R.id.mainView, "main");
    }

}
