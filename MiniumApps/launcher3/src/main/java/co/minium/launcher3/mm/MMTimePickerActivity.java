package co.minium.launcher3.mm;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import co.minium.launcher3.R;
import co.minium.launcher3.ui.TopFragment_;
import minium.co.core.ui.CoreActivity;

@Fullscreen
@EActivity(R.layout.time_picker_custom)

public class MMTimePickerActivity extends CoreActivity {


    @AfterViews
    public void afterViews(){
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
        loadFragment(new MMTimePickerFragment_(),R.id.mainView,"Main");
    }
}
