package co.siempo.phone.mm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.db.DaysOfWeekWhichWasSetAlarm;

/**
 * Created by tkb on 2017-03-10.
 */

public class RepeatAdapter extends ArrayAdapter<DaysOfWeekWhichWasSetAlarm> {


    // private static final String days [] = {
    //       "Mondays","Toesdays","Wednesdays","Thursdays","Fridays","Saturdays","Sundays"};
    List<DaysOfWeekWhichWasSetAlarm> daysOfWeekCheckedList;
    Context context;
    IteamAccess iteamAccess;

    public RepeatAdapter(Context context, IteamAccess iteamAccess, List<DaysOfWeekWhichWasSetAlarm> daysOfWeekCheckedList) {
        super(context, R.layout.repeat_row, daysOfWeekCheckedList);
        this.daysOfWeekCheckedList = daysOfWeekCheckedList;
        this.iteamAccess = iteamAccess;
        this.context = context;
    }

    @Override
    public View getView(final int position, final View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.repeat_row, parent, false);
        TextView day_title = (TextView) rowView.findViewById(R.id.day_title);

        CheckBox check_day = (CheckBox) rowView.findViewById(R.id.check_day);

        day_title.setText(daysOfWeekCheckedList.get(position).getDay().toString());
        check_day.setChecked(daysOfWeekCheckedList.get(position).getIsChecked());

        check_day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                iteamAccess.addObject(position, b);
            }
        });
        return rowView;
    }

    /*@Override
    public int getCount() {
        return activitiesModel.size();

    }*/

    public interface IteamAccess {
        void addObject(int position, boolean value);
    }
}
