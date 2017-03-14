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

import org.androidannotations.annotations.FragmentArg;

import co.minium.launcher3.R;
import minium.co.core.ui.CoreFragment;

/**
 * Created by tkb on 2017-03-13.
 */

public class MindfulMorningFragment extends CoreFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mm_layout, parent, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        /*ImageView imgBackground = (ImageView)view.findViewById(R.id.imgBackground);
        imgBackground.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
        imgBackground.setVisibility(View.VISIBLE);*/

        ImageView crossActionBar = (ImageView) view.findViewById(R.id.crossActionBar);
        crossActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }
}
