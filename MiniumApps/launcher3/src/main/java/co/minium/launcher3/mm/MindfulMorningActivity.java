package co.minium.launcher3.mm;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import co.minium.launcher3.R;
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

}
