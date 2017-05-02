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
        items.add(new MainListItem(1, getString(R.string.title_messages), "fa-users"));
        items.add(new MainListItem(2, getString(R.string.title_calls), "fa-phone"));
        items.add(new MainListItem(3, getString(R.string.title_contacts), "fa-user"));
        items.add(new MainListItem(4, getString(R.string.title_pause), "fa-ban"));
//        items.add(new MainListItem(5, getString(R.string.title_voicemail), "fa-microphone"));
        items.add(new MainListItem(6, getString(R.string.title_notes), "fa-sticky-note"));
//        items.add(new MainListItem(7, getString(R.string.title_clock), "fa-clock-o"));
        items.add(new MainListItem(8, getString(R.string.title_settings), "fa-cogs"));
//        items.add(new MainListItem(9, getString(R.string.title_theme), "fa-tint"));
        items.add(new MainListItem(10, getString(R.string.title_notificationScheduler), "fa-bell"));
        items.add(new MainListItem(11, getString(R.string.title_map), "fa-street-view"));
        //items.add(new MainListItem(17, getString(R.string.title_inbox), "fa-inbox"));
        items.add(new MainListItem(16, getString(R.string.title_email), "fa-envelope"));
        items.add(new MainListItem(18, getString(R.string.title_feedback), "fa-question-circle"));

        if (!Build.MODEL.toLowerCase().contains("siempo")) {
            items.add(new MainListItem(12, getString(title_defaultLauncher), "fa-certificate"));
        }

        items.add(new MainListItem(13, getString(R.string.title_mindfulMorning), "fa-coffee"));
        //items.add(new MainListItem(14, getString(R.string.title_mindfulMorningAlarm), "fa-coffee"));
        items.add(new MainListItem(15, getString(R.string.title_version, BuildConfig.VERSION_NAME), "fa-info-circle"));
        items.add(new MainListItem(19, getString(R.string.title_apps), "fa-list"));
        items.add(new MainListItem(20, getString(R.string.title_calendar), "fa-calendar"));
        items.add(new MainListItem(21, getString(R.string.title_clock), "fa-clock-o"));

        adapter = new OldMenuAdapter(getActivity(), items);
        listView.setAdapter(adapter);
    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {
        int id = items.get(position).getId();
        switch (id) {
            case 1: new ActivityHelper(getActivity()).openMessagingApp(); break;
            case 2:
                new ActivityHelper(getActivity()).openCallApp(); break;
            case 3: new ActivityHelper(getActivity()).openContactsApp(); break;
            case 4:
                PauseActivity_.intent(getActivity()).start(); break;
            case 5: UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented)); break;
            case 6: new ActivityHelper(getActivity()).openNotesApp(false); break;
            case 7: UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented)); break;
            case 8: new ActivityHelper(getActivity()).openSettingsApp(); break;
            case 9: UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented)); break;
            case 10:
                TempoActivity_.intent(getActivity()).start(); break;
            case 11:
                //SiempoMapActivity_.intent(getActivity()).start(); break;
                new ActivityHelper(getActivity()).openGMape(Constants.GOOGLE_MAP_PACKAGE); break;

            case 12: new ActivityHelper(getActivity()).handleDefaultLauncher((CoreActivity) getActivity()); break;
            case 13:
                MMTimePickerActivity_.intent(getActivity()).start();
                // mindful morning
                break;
            case 14:
                MindfulMorningActivity_.intent(getActivity()).start();
                break;
            case 15:
                // Version text, no action should be taken
                break;
            case 16:
                new ActivityHelper(getActivity()).openEmail(); break;
            case 17:
                //new ActivityHelper(getActivity()).openGoogleInbox(); break;
            case 18:
                new ActivityHelper(getActivity()).openFeedback(); break;
            case 19:
                InstalledAppList_.intent(getActivity()).start(); break;
            case 20:
                new ActivityHelper(getActivity()).openGMape(Constants.CALENDAR_PACKAGE); break;
            case 21:
                new ActivityHelper(getActivity()).openGMape(Constants.CLOCK_PACKAGE); break;

            default: UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented)); break;

        }

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
