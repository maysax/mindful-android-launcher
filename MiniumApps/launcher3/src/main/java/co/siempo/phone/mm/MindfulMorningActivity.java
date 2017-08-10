package co.siempo.phone.mm;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import co.siempo.phone.R;
import co.siempo.phone.event.MindfulMorgingEventStart;
import de.greenrobot.event.Subscribe;
import minium.co.core.ui.CoreActivity;

/**
 * Created by tkb on 2017-03-17.
 */
@Fullscreen
@EActivity(R.layout.mindful_morning_activity)

public class MindfulMorningActivity extends CoreActivity {

    @AfterViews
    public void afterViews() {

        loadFragment(new MindfulMorningFragment_(), R.id.mainView, "Main");

    }

    @Subscribe
    public void MindfulMorgingEventStart(MindfulMorgingEventStart event) {
        loadFragment(MinfulMorningActivated_.builder().startPosition(event.getStartPosition()).build(), R.id.mainView, "main");
    }

}
