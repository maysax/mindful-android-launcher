package co.siempo.phone.mm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.db.ActivitiesStorage;

/**
 * Created by tkb on 2017-03-10.
 */

@SuppressWarnings("ALL")
public class MindfulMorningListAdapter extends ArrayAdapter<ActivitiesStorage> {

    private final Activity context;
    List<ActivitiesStorage> activitiesModel;

    public MindfulMorningListAdapter(Activity context, List<ActivitiesStorage> activitiesModel) {
        super(context, R.layout.mm_list_row, activitiesModel);
        this.context = context;
        this.activitiesModel = activitiesModel;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"InflateParams", "ViewHolder"}) View rowView = inflater.inflate(R.layout.mm_list_row, null, false);
        TextView txt_time = (TextView) rowView.findViewById(R.id.txt_time);

        TextView txt_title = (TextView) rowView.findViewById(R.id.txt_title);

        txt_title.setText(activitiesModel.get(position).getName());
        txt_time.setText(activitiesModel.get(position).getTime() + "");
        return rowView;
    }

/*    @Override
    public int getCount() {
        return activitiesModel.size();

    }*/
}
