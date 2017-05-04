package co.siempo.phone.old;


import android.os.Build;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.applist.InstalledAppList_;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.mm.MMTimePickerActivity_;
import co.siempo.phone.mm.MindfulMorningActivity_;
import co.siempo.phone.model.MainListItem;
import co.siempo.phone.pause.PauseActivity_;
import co.siempo.phone.tempo.TempoActivity_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;

import static co.siempo.phone.R.string.title_defaultLauncher;
import static co.siempo.phone.app.Constants.GOOGLE_PHOTOS;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_old_menu)
public class OldMenuFragment extends CoreFragment {

    private OldMenuAdapter adapter;
    private List<MainListItem> items;

    @ViewById
    ListView listView;


    public OldMenuFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        loadData();
    }

    private void loadData() {
        items = new ArrayList<>();
        new MainListItemLoader(getActivity()).loadItems(items);
        adapter = new OldMenuAdapter(getActivity(), items);
        listView.setAdapter(adapter);
    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {
        int id = items.get(position).getId();
        new MainListItemLoader(getActivity()).listItemClicked(id);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible)
        {
            try {
                UIUtils.hideSoftKeyboard(getActivity(),getActivity().getCurrentFocus().getWindowToken());
            } catch (Exception e) {
                Tracer.e(e, e.getMessage());
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            try {
                UIUtils.hideSoftKeyboard(getActivity(), getActivity().getCurrentFocus().getWindowToken());
            } catch (Exception e) {
                Tracer.e(e, e.getMessage());
            }
        }
    }
}
