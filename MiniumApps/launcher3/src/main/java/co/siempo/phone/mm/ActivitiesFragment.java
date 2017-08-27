package co.siempo.phone.mm;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.db.ActivitiesStorage;
import co.siempo.phone.db.DBUtility;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

/**
 * Created by tkb on 2017-03-10.
 */
@SuppressWarnings("ALL")
@EFragment(R.layout.activities_layout)
public class ActivitiesFragment extends CoreFragment {
    List<ActivitiesStorage> activitiesStorageList;
    //ListView listView;
   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activities_layout, parent, false);

    }*/
    @ViewById
    ListView activity_list_view;
    @ViewById
    ImageView crossActionBar;

    @Click
    void crossActionBar() {
        getActivity().onBackPressed();
    }

    @AfterViews
    void afterViews() {
        activity_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // String [] title = {"Meditation Timer","Workout Timer","Reading Timer","Journaling Timer","Pause Timer"};
                ((CoreActivity) getActivity()).loadChildFragment(ActivitiesDetailsFragment_.builder().title(activitiesStorageList.get(i).getName()).build(), R.id.mainView);
                //MindfulMorningActivity_.intent(getActivity()).start();

            }
        });

    }


    private ArrayList<ActivitiesStorage> insertDefaultValue() {
        String[] name = {"Meditation", "Workout", "Reading", "Journaling", "Pause"};
        //int[] values = {0,0,0,0,0};
        ActivitiesStorage aActivityStorage;
        ArrayList<ActivitiesStorage> activitiesStorageList = new ArrayList<>();
        for (int i = 0; i < name.length; i++) {
            aActivityStorage = new ActivitiesStorage();
            aActivityStorage.setName(name[i]);
            aActivityStorage.setTime(0);
            activitiesStorageList.add(aActivityStorage);
        }
        DBUtility.getActivitySession().insertInTx(activitiesStorageList);
        return activitiesStorageList;
    }

    @Override
    public void onResume() {
        super.onResume();

        activitiesStorageList = DBUtility.getActivitySession().loadAll();
        if (activitiesStorageList.size() == 0) {
            activitiesStorageList = insertDefaultValue();
        }
        ActivitiesAdapter activitiesAdapter = new ActivitiesAdapter(getActivity(), activitiesStorageList);
        activity_list_view.setAdapter(activitiesAdapter);


    }
}
