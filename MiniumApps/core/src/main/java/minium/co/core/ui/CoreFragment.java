package minium.co.core.ui;

import android.app.Activity;
import android.app.Fragment;


import com.squareup.leakcanary.RefWatcher;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Trace;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment
public abstract class CoreFragment extends Fragment {

    protected final String TRACE_TAG = LogConfig.TRACE_TAG + "MainFragment";
    protected CoreActivity context;

    public CoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = (CoreActivity) activity;
    }

    @Trace(tag = TRACE_TAG)
    @Override
    public void onStart() {
        Tracer.v("Fragment onStart(): " + this.getClass().getSimpleName());
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Trace(tag = TRACE_TAG)
    @Override
    public void onStop() {
        Tracer.v("Fragment onStop(): " + this.getClass().getSimpleName());
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void genericEvent(Object event) {
        // DO NOT code here, it is a generic catch event method 
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RefWatcher refWatcher = CoreApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
