package minium.co.launcher2.ui;


import android.app.Fragment;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.MainActivity_;
import minium.co.launcher2.R;
import minium.co.launcher2.adapters.MainAdapter;
import minium.co.launcher2.events.FilterActionEvent;
import minium.co.launcher2.events.LoadFragmentEvent;
import minium.co.launcher2.flow.FlowActivity_;
import minium.co.launcher2.helper.ActivityHelper;
import minium.co.launcher2.helper.SearchTextParser;
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

    @Bean
    SearchTextParser parser;

    private String mSearchString = null;

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
                parser.onClickedActionItem(0);
                break;
            case 1:
                parser.onClickedActionItem(1);
                break;
            case 2:
                parser.onClickedActionItem(2);
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

                        if (color == getResources().getColor(R.color.material_core_red)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_Red);
                        } else if (color == getResources().getColor(R.color.material_core_pink)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_Pink);
                        } else if (color == getResources().getColor(R.color.material_core_purple)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_Purple);
                        } else if (color == getResources().getColor(R.color.material_core_deepPurple)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_DeepPurple);
                        } else if (color == getResources().getColor(R.color.material_core_indigo)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_Indigo);
                        } else if (color == getResources().getColor(R.color.material_core_blue)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_Blue);
                        } else if (color == getResources().getColor(R.color.material_core_lightBlue)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_LightBlue);
                        } else if (color == getResources().getColor(R.color.material_core_cyan)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_Cyan);
                        } else if (color == getResources().getColor(R.color.material_core_teal)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_Teal);
                        } else if (color == getResources().getColor(R.color.material_core_green)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_Green);
                        } else if (color == getResources().getColor(R.color.material_core_lightGreen)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_LightGreen);
                        } else if (color == getResources().getColor(R.color.material_core_lime)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_Lime);
                        } else if (color == getResources().getColor(R.color.material_core_yellow)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_Yellow);
                        } else if (color == getResources().getColor(R.color.material_core_amber)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_Amber);
                        } else if (color == getResources().getColor(R.color.material_core_orange)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_Orange);
                        } else if (color == getResources().getColor(R.color.material_core_deepOrange)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_DeepOrange);
                        } else if (color == getResources().getColor(R.color.material_core_brown)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_Brown);
                        } else if (color == getResources().getColor(R.color.material_core_grey)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_Grey);
                        } else if (color == getResources().getColor(R.color.material_core_blueGrey)) {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme_BlueGrey);
                        } else {
                            prefs.selectedThemeId().put(minium.co.core.R.style.CoreTheme);
                        }

                        android.app.TaskStackBuilder.create(getActivity())
                                .addNextIntent(new Intent(getActivity(), MainActivity_.class)).startActivities();
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

    @Subscribe
    public void onEventFilterActionEvents(FilterActionEvent event) {
        String newText = event.getText();
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

        if (mSearchString == null && newFilter == null) {
            return;
        }
        if (mSearchString != null && mSearchString.equals(newFilter)) {
            return;
        }
        mSearchString = newFilter;
        adapter.getFilter().filter(mSearchString);
    }

}
