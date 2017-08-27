package minium.co.launcher2.ui;


import android.app.Fragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.Subscribe;
import minium.co.core.ui.CoreFragment;
import minium.co.launcher2.R;
import minium.co.launcher2.events.MakeChipEvent;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_search)
public class SearchFragment extends CoreFragment {

    @ViewById
    SearchLayout searchLayout;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Subscribe
    public void onEvent(MakeChipEvent event) {
        //searchLayout.makeChip(event.getText());
    }
}
