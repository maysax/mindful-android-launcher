package co.siempo.phone.mm;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import co.siempo.phone.R;
import co.siempo.phone.ui.TopFragment_;
import co.siempo.phone.util.PackageUtil;
import minium.co.core.ui.CoreActivity;

@EActivity(R.layout.time_picker_custom)

public class MMTimePickerActivity extends CoreActivity {


    @AfterViews
    public void afterViews() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
        loadFragment(new MMTimePickerFragment_(), R.id.mainView, "Main");
    }

    @Override
    protected void onResume() {
        super.onResume();
        PackageUtil.checkPermission(this);
    }
}
