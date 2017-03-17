package co.minium.launcher3.mm;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import co.minium.launcher3.R;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

/**
 * Created by tkb on 2017-03-13.
 */

@EFragment(R.layout.mm_layout)
public class MindfulMorningFragment extends CoreFragment {

     @ViewById
    ImageView crossActionBar;
    @ViewById
    Button pause_button;
    @Click
    void pause_button(){
        ((CoreActivity)getActivity()).loadChildFragment(new MindfulMorningList_(),R.id.mainView);
    }
    @Click
    void crossActionBar(){
        getActivity().onBackPressed();
    }
    @AfterViews
    public void afterViews(){

    }
}
