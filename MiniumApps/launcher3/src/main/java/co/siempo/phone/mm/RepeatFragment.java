package co.siempo.phone.mm;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Calendar;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.db.DaysOfWeekWhichWasSetAlarm;
import co.siempo.phone.mm.controller.AlarmController;
import co.siempo.phone.mm.controller.DatabaseController;
import co.siempo.phone.mm.model.Utilities;
import minium.co.core.ui.CoreFragment;

/**
 * Created by tkb on 2017-03-10.
 */

@EFragment(R.layout.activities_layout)
public class RepeatFragment extends CoreFragment implements RepeatAdapter.IteamAccess {
    List<DaysOfWeekWhichWasSetAlarm> daysOfWeekCheckedList;

    @Pref
    Launcher3Prefs_ launcherPrefs;
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activities_layout, parent, false);
    }*/

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        daysOfWeekCheckedList = DatabaseController.getDays();

        TextView titleActionBar = (TextView) view.findViewById(R.id.titleActionBar);
        titleActionBar.setText("Repeat");

        ListView listView = (ListView) view.findViewById(R.id.activity_list_view);
        RepeatAdapter repeatAdapter = new RepeatAdapter(getActivity(), this, daysOfWeekCheckedList);
        listView.setAdapter(repeatAdapter);

        ImageView crossActionBar = (ImageView) view.findViewById(R.id.crossActionBar);
        crossActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        if (daysOfWeekCheckedList.size() != 0) {
            DatabaseController.updateDaysList(daysOfWeekCheckedList);
        }

        Calendar calendar = Utilities.getCalendar(launcherPrefs);
        AlarmController.setAlarm(getActivity(), calendar);

    }


    @Override
    public void addObject(int position, boolean value) {
        daysOfWeekCheckedList.get(position).setIsChecked(value);
    }
}
