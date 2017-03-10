package co.minium.launcher3.mm;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import co.minium.launcher3.R;

/**
 * Created by tkb on 2017-03-10.
 */

public class RepeatFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activities_layout, parent, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        TextView titleActionBar = (TextView)view.findViewById(R.id.titleActionBar);
        titleActionBar.setText("Repeat");

        ListView listView = (ListView)view.findViewById(R.id.activity_list_view);
        RepeatAdapter repeatAdapter = new RepeatAdapter(getActivity());
        listView.setAdapter(repeatAdapter);
    }
}
