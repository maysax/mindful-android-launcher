package co.minium.launcher3.mm;

import android.os.Bundle;
import co.minium.launcher3.R;
import minium.co.core.ui.CoreActivity;


public class TimePickerActivity extends CoreActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN, android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_picker_custom);

        loadFragment(new TimePickerFragment(),R.id.mainView,"Main");

    }

}
