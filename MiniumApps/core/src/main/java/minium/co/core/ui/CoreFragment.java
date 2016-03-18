package minium.co.core.ui;


import android.support.v4.app.Fragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Trace;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.config.Config;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment
public abstract class CoreFragment extends Fragment {

    protected final String TRACE_TAG = Config.TRACE_TAG + "MainFragment";


    public CoreFragment() {
        // Required empty public constructor
    }

    @Trace(tag = TRACE_TAG)
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Trace(tag = TRACE_TAG)
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void genericEvent(Object event) {
        // DO NOT code here, it is a generic catch event method 
    }
}
