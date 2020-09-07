package co.siempo.phone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Trace;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import co.siempo.phone.log.LogConfig;
import co.siempo.phone.log.Tracer;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class CoreFragment extends Fragment {
    protected static final String TRACE_TAG = LogConfig.TRACE_TAG + "MainFragment";

    @Nullable
    private ViewBinding mBinding;

    public CoreFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = onCreateViewBinding(inflater, container, savedInstanceState);
        return mBinding == null ? null : mBinding.getRoot();
    }

    @Nullable
    protected ViewBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Nullable
    public <VB extends ViewBinding> VB getViewBinding() {
        // noinspection unchecked
        return (VB) mBinding;
    }

    @NonNull
    public <VB extends ViewBinding> VB requireViewBinding() {
        if (mBinding == null) {
            throw new IllegalStateException("Fragment " + this + " has no active ViewBinding.");
        }
        // noinspection unchecked
        return (VB) mBinding;
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
        mBinding = null;
    }
}
