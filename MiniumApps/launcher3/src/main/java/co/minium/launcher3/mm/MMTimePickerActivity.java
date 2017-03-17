package co.minium.launcher3.mm;

import android.os.Bundle;
import android.view.Window;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.WindowFeature;

import co.minium.launcher3.R;
import co.minium.launcher3.ui.TopFragment_;
import minium.co.core.ui.CoreActivity;

@Fullscreen
@EActivity(R.layout.time_picker_custom)

public class MMTimePickerActivity extends CoreActivity {
   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN, android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_picker_custom);

        loadTopBar();
        loadFragment(new TimePickerFragment_(),R.id.mainView,"Main");

    }*/

    @AfterViews
    public void afterViews(){
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
        loadFragment(new TimePickerFragment_(),R.id.mainView,"Main");

    }
    /*private void loadTopBar() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
    }*/

}
