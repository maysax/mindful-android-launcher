package co.minium.launcher3.pause;

import android.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.minium.launcher3.R;
import co.minium.launcher3.app.Launcher3Prefs_;
import de.greenrobot.event.Subscribe;
import minium.co.core.ui.CoreFragment;

@EFragment(R.layout.fragment_pause_preference)
public class PausePreferenceFragment extends CoreFragment {

    PauseRecyclerViewAdapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    public PausePreferenceFragment() {
        // Required empty public constructor
    }

    @Click
    void crossActionBar() {
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
        //((CoreActivity)getActivity()).finish();
    }

    @ViewById
    RecyclerView pref_recyclerview;
    @AfterViews
    void afterViews() {
        String[] subjects =
                {"Allow favorites","Allow calls"};

        recylerViewLayoutManager =
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        pref_recyclerview.setLayoutManager(recylerViewLayoutManager);

        recyclerViewAdapter = new PauseRecyclerViewAdapter(context, subjects);

        pref_recyclerview.setAdapter(recyclerViewAdapter);
    }

    @Subscribe
    void pausePreferenceEvent(PausePreferenceEvent event) {
        launcherPrefs.isPauseAllowFavoriteChecked().put(event.isAllowFavorites());
        launcherPrefs.isPauseAllowCallsChecked().put(event.isAllowCalls());
    }
}
