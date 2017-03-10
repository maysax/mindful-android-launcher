package co.minium.launcher3.mm;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import co.minium.launcher3.R;
import minium.co.core.ui.CoreActivity;

/**
 * Created by tkb on 2017-03-10.
 */

public class ActivitiesFragment  extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activities_layout, parent, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ListView listView = (ListView)view.findViewById(R.id.activity_list_view);
        ActivitiesAdapter activitiesAdapter = new ActivitiesAdapter(getActivity(),new ActivitiesModel().getActivityModel());
        listView.setAdapter(activitiesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String [] title = {"Meditation","Workout","Reading","Journaling","Pause"};
                switch (i) {
                    case 0:
                        ((CoreActivity)getActivity()).loadChildFragment(MeditationTimeFragment_.builder().title(title[i]).build(),R.id.mainView);
                        break;
                    case 1:
                        ((CoreActivity)getActivity()).loadChildFragment(MeditationTimeFragment_.builder().title(title[i]).build(),R.id.mainView);
                        break;
                    case 2:
                        ((CoreActivity)getActivity()).loadChildFragment(MeditationTimeFragment_.builder().title(title[i]).build(),R.id.mainView);
                        break;
                    case 3:
                        ((CoreActivity)getActivity()).loadChildFragment(MeditationTimeFragment_.builder().title(title[i]).build(),R.id.mainView);
                        break;
                    case 4:
                        ((CoreActivity)getActivity()).loadChildFragment(MeditationTimeFragment_.builder().title(title[i]).build(),R.id.mainView);
                        break;
                }
            }
        });
    }
}
