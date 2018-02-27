package co.siempo.phone.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.HomePressEvent;
import co.siempo.phone.fragments.HelpFragment;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.Subscribe;


public class HelpActivity extends CoreActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        loadFragment(new HelpFragment(), R.id.helpView, "main");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        try {
            if (event.isVisible() && UIUtils.isMyLauncherDefault(this)) {
                finish();
            }

        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }
    }

}
