package minium.co.launcher2.notificationscheduler;


import android.support.v4.app.Fragment;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Locale;

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

    @ViewById
    TextView txtMsg;

    @Pref
    DroidPrefs_ prefs;

    private String[] pickerData = new String[] { "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60" };


    public NotificationSchedulerFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        valPicker.setMinValue(0);
        valPicker.setMaxValue(pickerData.length - 1);
        valPicker.setDisplayedValues(pickerData);
        valPicker.setWrapSelectorWheel(true);
        valPicker.setValue(prefs.notificationScheduleIndex().get());
        updateUI(prefs.notificationScheduleIndex().get());

        valPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateUI(newVal);
                prefs.notificationScheduleIndex().put(newVal);
            }
        });
    }

    private void updateUI(int newVal) {
        switch (newVal) {
            case 0: txtMsg.setText("Notification is enabled always"); break;
            default: txtMsg.setText(String.format(Locale.US, "Notification will be enabled for every %s minutes", pickerData [newVal]));
        }
    }
}
