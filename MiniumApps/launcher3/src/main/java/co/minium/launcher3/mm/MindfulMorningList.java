package co.minium.launcher3.mm;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import co.minium.launcher3.R;
import co.minium.launcher3.event.MindfulMorgingEventStart;
import co.minium.launcher3.mm.model.ActivitiesStorage;
import co.minium.launcher3.mm.model.ActivitiesStorageDao;
import co.minium.launcher3.mm.model.DBUtility;
import de.greenrobot.event.EventBus;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

/**
 * Created by tkb on 2017-03-14.
 */

@EFragment(R.layout.mm_list)
public class MindfulMorningList  extends CoreFragment {

   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mm_list, parent, false);

    }*/
   List<ActivitiesStorage> activitiesStorageList;
   @ViewById
   ListView activity_list_view;

    @ViewById
    ImageView crossActionBar;

    @Click
    void crossActionBar(){
        getActivity().onBackPressed();
    }

   @AfterViews
   void afterViews(){
       activitiesStorageList =  DBUtility.getActivitySession()
               .queryBuilder().where(ActivitiesStorageDao.Properties.Time.notEq(0)).list();

       MindfulMorningListAdapter mindfulMorningListAdapter = new MindfulMorningListAdapter(getActivity(),activitiesStorageList);
       activity_list_view.setAdapter(mindfulMorningListAdapter);

       activity_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//               ((CoreActivity)getActivity()).loadChildFragment(MindfulMorningListDetails_.builder().title(activitiesStorageList.get(i).getName()).value(activitiesStorageList.get(i).getTime()).build(),R.id.mainView);

               EventBus.getDefault().post(new MindfulMorgingEventStart(i));


           }
       });


   }

}
