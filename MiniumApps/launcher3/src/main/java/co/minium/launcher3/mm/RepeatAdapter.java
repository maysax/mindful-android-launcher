package co.minium.launcher3.mm;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import co.minium.launcher3.R;

/**
 * Created by tkb on 2017-03-10.
 */

public class RepeatAdapter extends ArrayAdapter<String> {

    private final Activity context;

    private static final String days [] = {
            "Mondays","Toesdays","Wednesdays","Thursdays","Fridays","Saturdays","Sundays"};

    public RepeatAdapter(Activity context) {
        super(context, R.layout.repeat_row, days);
        this.context = context;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.repeat_row, null, false);
        TextView day_title = (TextView) rowView.findViewById(R.id.day_title);

        CheckBox check_day = (CheckBox) rowView.findViewById(R.id.check_day);


        day_title.setText(days[position].toString());

        return rowView;
    }

    /*@Override
    public int getCount() {
        return activitiesModel.size();

    }*/
}
