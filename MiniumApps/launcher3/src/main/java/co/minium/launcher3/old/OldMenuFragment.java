package co.minium.launcher3.old;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.minium.launcher3.BuildConfig;
import co.minium.launcher3.R;
import co.minium.launcher3.contact.PhoneNumbersAdapter;
import co.minium.launcher3.helper.ActivityHelper;
import co.minium.launcher3.model.MainListItem;
import co.minium.launcher3.ui.PauseActivity_;
import co.minium.launcher3.ui.TempoActivity_;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;

import static co.minium.launcher3.R.string.title_defaultLauncher;

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    private void loadData() {
        items = new ArrayList<>();
        items.add(new MainListItem(1, getString(R.string.title_messages), "fa-users"));
        items.add(new MainListItem(2, getString(R.string.title_callLog), "fa-phone"));
        items.add(new MainListItem(3, getString(R.string.title_contacts), "fa-user"));
        items.add(new MainListItem(4, getString(R.string.title_pause), "fa-ban"));
        items.add(new MainListItem(5, getString(R.string.title_voicemail), "fa-microphone"));
        items.add(new MainListItem(6, getString(R.string.title_notes), "fa-sticky-note"));
        items.add(new MainListItem(7, getString(R.string.title_clock), "fa-clock-o"));
        items.add(new MainListItem(8, getString(R.string.title_settings), "fa-cogs"));
        items.add(new MainListItem(9, getString(R.string.title_theme), "fa-tint"));
        items.add(new MainListItem(10, getString(R.string.title_notificationScheduler), "fa-bell"));
        items.add(new MainListItem(11, getString(R.string.title_map), "fa-street-view"));

        if (!Build.MODEL.toLowerCase().contains("siempo")) {
            items.add(new MainListItem(12, getString(title_defaultLauncher), "fa-certificate"));
        }

        items.add(new MainListItem(13, getString(R.string.title_version, BuildConfig.VERSION_NAME), "fa-info-circle"));

        adapter = new OldMenuAdapter(getActivity(), items);
        listView.setAdapter(adapter);
    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {
        int id = items.get(position).getId();
        switch (id) {
            case 1: new ActivityHelper(getActivity()).openMessagingApp(); break;
            case 2:
                UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented)); break;
            case 3: new ActivityHelper(getActivity()).openContactsApp(); break;
            case 4:
                PauseActivity_.intent(getActivity()).start(); break;
            case 5: UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented)); break;
            case 6: new ActivityHelper(getActivity()).openNotesApp(); break;
            case 7: UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented)); break;
            case 8: new ActivityHelper(getActivity()).openSettingsApp(); break;
            case 9: UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented)); break;
            case 10:
                TempoActivity_.intent(getActivity()).start(); break;
            case 11: UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented)); break;
            case 12: UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented)); break;
            case 13: break;
            default: UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented)); break;

        }

    }
}
