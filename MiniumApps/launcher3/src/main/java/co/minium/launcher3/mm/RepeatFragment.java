package co.minium.launcher3.mm;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.minium.launcher3.R;
import co.minium.launcher3.db.DBUtility;
import co.minium.launcher3.mm.controller.DatabaseController;
import co.minium.launcher3.mm.model.DaysOfWeekWhichWasSetAlarm;
import co.minium.launcher3.mm.model.DaysOfWeekWhichWasSetAlarmDao;
import co.minium.launcher3.mm.model.Utilities;

/**
 * Created by tkb on 2017-03-10.
 */

public class RepeatFragment extends Fragment implements RepeatAdapter.IteamAccess {
    List<DaysOfWeekWhichWasSetAlarm> daysOfWeekCheckedList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activities_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        daysOfWeekCheckedList = DatabaseController.getDays();

        TextView titleActionBar = (TextView)view.findViewById(R.id.titleActionBar);
        titleActionBar.setText("Repeat");

        ListView listView = (ListView)view.findViewById(R.id.activity_list_view);
        RepeatAdapter repeatAdapter = new RepeatAdapter(getActivity(),this,daysOfWeekCheckedList);
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

        if (daysOfWeekCheckedList.size()!=0){
            DatabaseController.updateDaysList(daysOfWeekCheckedList);
        }
    }


    @Override
    public void addObject(int position, boolean value) {
        daysOfWeekCheckedList.get(position).setIsChecked(value);
    }
}
