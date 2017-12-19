package co.siempo.phone.mm;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.event.MindfulMorgingEventStart;
import co.siempo.phone.util.PackageUtil;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.ui.CoreActivity;

/**
 * Created by tkb on 2017-03-17.
 */
@SuppressWarnings("ALL")
@EActivity(R.layout.mindful_morning_activity)

public class MindfulMorningActivity extends CoreActivity {

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
        }
    }

    @AfterViews
    public void afterViews() {

        loadFragment(new MindfulMorningFragment_(), R.id.mainView, "Main");

    }

    @Subscribe
    public void MindfulMorgingEventStart(MindfulMorgingEventStart event) {
        loadFragment(MinfulMorningActivated_.builder().startPosition(event.getStartPosition()).build(), R.id.mainView, "main");
    }

    @Override
    protected void onResume() {
        super.onResume();
        PackageUtil.checkPermission(this);
    }
}
