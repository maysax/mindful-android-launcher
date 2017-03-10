package co.minium.launcher3.mm;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import co.minium.launcher3.R;

/**
 * Created by tkb on 2017-03-10.
 */
@EFragment
public class MeditationTimeFragment extends Fragment {
    @FragmentArg
    String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.meditation_time, parent, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        TextView titleActionBar = (TextView)view.findViewById(R.id.titleActionBar);
        titleActionBar.setText(title);
        ImageView crossActionBar = (ImageView) view.findViewById(R.id.crossActionBar);
        crossActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }
}
