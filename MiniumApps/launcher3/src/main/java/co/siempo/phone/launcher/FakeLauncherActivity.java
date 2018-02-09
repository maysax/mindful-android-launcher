package co.siempo.phone.launcher;

import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.event.AppInstalledEvent;
import de.greenrobot.event.Subscribe;

/**
 * Created by Shahab on 12/30/2016.
 */

public class FakeLauncherActivity extends CoreActivity {

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
        }
    }
}
