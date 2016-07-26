package minium.co.launcher2.notificationscheduler;


import android.support.v4.app.Fragment;
import android.widget.NumberPicker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreFragment;
import minium.co.launcher2.R;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_notification_scheduler)
public class NotificationSchedulerFragment extends CoreFragment {

    @ViewById
    NumberPicker valPicker;

    @Pref
    DroidPrefs_ prefs;


    public NotificationSchedulerFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        valPicker.setMinValue(0);
        valPicker.setMaxValue(12);
        valPicker.setDisplayedValues(new String[]{ "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60" });
        valPicker.setWrapSelectorWheel(true);
        valPicker.setValue(prefs.notificationScheduleIndex().get());

        valPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                prefs.notificationScheduleIndex().put(newVal);
            }
        });
    }

}
