package co.minium.launcher3.mm;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import co.minium.launcher3.R;
import co.minium.launcher3.mm.model.ActivitiesStorage;
import co.minium.launcher3.mm.model.DBUtility;
import minium.co.core.ui.CoreActivity;

/**
 * Created by tkb on 2017-03-10.
 */

public class ActivitiesFragment  extends Fragment {
    List<ActivitiesStorage>activitiesStorageList;
    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activities_layout, parent, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        listView = (ListView)view.findViewById(R.id.activity_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               // String [] title = {"Meditation Timer","Workout Timer","Reading Timer","Journaling Timer","Pause Timer"};
                ((CoreActivity)getActivity()).loadChildFragment(ActivitiesDetailsFragment_.builder().title(activitiesStorageList.get(i).getName()).build(),R.id.mainView);
                //MindfulMorningActivity_.intent(getActivity()).start();

            }
        });

        ImageView crossActionBar = (ImageView) view.findViewById(R.id.crossActionBar);
        crossActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private ArrayList<ActivitiesStorage> insertDefaultValue() {
        String[] name = {"Meditation","Workout","Reading","Journaling","Pause"};
        //int[] values = {0,0,0,0,0};
        ActivitiesStorage aActivityStorage;
        ArrayList<ActivitiesStorage>activitiesStorageList = new ArrayList<>();
        for (int i=0; i<name.length;i++){
            aActivityStorage = new ActivitiesStorage();
            aActivityStorage.setName(name[i]);
            aActivityStorage.setTime(0);
            activitiesStorageList.add(aActivityStorage);
        }
        DBUtility.GetActivitySession().insertInTx(activitiesStorageList);
        return activitiesStorageList;
    }

    @Override
    public void onResume() {
        super.onResume();

        activitiesStorageList = DBUtility.GetActivitySession().loadAll();
        if (activitiesStorageList.size()==0){
            activitiesStorageList = insertDefaultValue();
        }
        ActivitiesAdapter activitiesAdapter = new ActivitiesAdapter(getActivity(),activitiesStorageList);
        listView.setAdapter(activitiesAdapter);


    }
}
