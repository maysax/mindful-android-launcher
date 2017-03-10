package co.minium.launcher3.mm;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import co.minium.launcher3.R;

/**
 * Created by tkb on 2017-03-10.
 */

public class ActivitiesAdapter extends ArrayAdapter<ActivitiesModel> {

    private final Activity context;
    ArrayList<ActivitiesModel>activitiesModel;
    public ActivitiesAdapter(Activity context, ArrayList<ActivitiesModel>activitiesModel) {
        super(context, R.layout.activities_row, activitiesModel);
        this.context = context;
        this.activitiesModel = activitiesModel;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.activities_row, null, false);
        TextView txt_time = (TextView) rowView.findViewById(R.id.txt_time);

        TextView txt_title = (TextView) rowView.findViewById(R.id.txt_title);

        txt_title.setText(activitiesModel.get(position).getTitle());
        txt_time.setText(activitiesModel.get(position).getTimeValue());
        return rowView;
    }

    @Override
    public int getCount() {
        return activitiesModel.size();

    }
}
