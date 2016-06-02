package minium.co.launcher2.ui;


import android.app.Fragment;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import de.greenrobot.event.EventBus;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.adapters.MainAdapter;
import minium.co.launcher2.app.DroidPrefs_;
import minium.co.launcher2.events.LoadFragmentEvent;
import minium.co.launcher2.events.MainItemClickedEvent;
import minium.co.launcher2.flow.FlowActivity_;
import minium.co.launcher2.helper.ActivityHelper;
import minium.co.launcher2.model.MainListItem;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_main)
public class MainFragment extends CoreFragment {

    @ViewById
    ListView listView;

    @Pref
    DroidPrefs_ prefs;

    MainAdapter adapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        adapter = new MainAdapter(getActivity(), getListItems());
        listView.setAdapter(adapter);
    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {
        switch (position) {
            case 0:
                EventBus.getDefault().post(new MainItemClickedEvent("Text", 0));
                break;
            case 1:
                EventBus.getDefault().post(new MainItemClickedEvent("Call", 1));
                break;
            case 2:
                EventBus.getDefault().post(new MainItemClickedEvent("Note", 2));
                break;
            case 3:
                if (!new ActivityHelper(getActivity()).openMessagingApp())
                    UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
            case 4:
                // TODO: load call log
                EventBus.getDefault().post(new LoadFragmentEvent(LoadFragmentEvent.CALL_LOG));
                break;
            case 5:
                if (!new ActivityHelper(getActivity()).openContactsApp())
                    UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
            case 6:
                FlowActivity_.intent(this).start();
                break;
            case 7:
                UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
            case 8:
                UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
            case 9:
                UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
            case 10:
                if (!new ActivityHelper(getActivity()).openSettingsApp())
                    UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
            case 11:
                ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                        getResources().getIntArray(R.array.material_core_colors),
                        prefs.selectedThemeColor().get(),
                        5, ColorPickerDialog.SIZE_SMALL);

                        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        prefs.selectedThemeColor().put(color);
                        UIUtils.toast(getActivity(), "Color: " + color);
                    }
                });

                dialog.show(getFragmentManager(), "color_picker_dialog");
        }
    }

    private MainListItem[] getListItems() {
        return new MainListItem[] {
                new MainListItem("{fa-comment-o}", "Text"),
                new MainListItem("{fa-phone}", "Call"),
                new MainListItem("{fa-sticky-note-o}", "Add Note"),
                new MainListItem("{fa-users}", "Messages"),
                new MainListItem("{fa-phone}", "Call Log"),
                new MainListItem("{fa-user}", "Address Book"),
                new MainListItem("{fa-ban}", "Flow"),
                new MainListItem("{fa-microphone}", "Voicemail"),
                new MainListItem("{fa-sticky-note-o}", "Notes"),
                new MainListItem("{fa-clock-o}", "Clock"),
                new MainListItem("{fa-cogs}", "Settings"),
                new MainListItem("{fa-tint}", "Theme")
        };
    }

}
