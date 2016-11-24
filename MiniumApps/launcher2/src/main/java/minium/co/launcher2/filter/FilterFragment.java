package minium.co.launcher2.filter;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.MainActivity_;
import minium.co.launcher2.R;
import minium.co.launcher2.contactspicker.ContactsLoader;
import minium.co.launcher2.contactspicker.OnContactSelectedListener;
import minium.co.launcher2.data.ActionItemManager;
import minium.co.launcher2.events.ActionItemUpdateEvent;
import minium.co.launcher2.events.LoadFragmentEvent;
import minium.co.launcher2.flow.FlowActivity_;
import minium.co.launcher2.helper.ActivityHelper;
import minium.co.launcher2.model.ActionItem;
import minium.co.launcher2.model.ActionListItem;
import minium.co.launcher2.model.ContactListItem;
import minium.co.launcher2.model.MainListItem;
import minium.co.launcher2.model.OptionsListItem;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_filter)
public class FilterFragment extends CoreFragment {

    @ViewById
    ListView listView;

    @Pref
    DroidPrefs_ prefs;

    @Bean
    ActionItemManager manager;

    FilterAdapter adapter;

    List<MainListItem> items;

    private String mSearchString = null;

    private OnContactSelectedListener mContactsListener;


    public FilterFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {

    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Background
    void loadData() {
        items = new ArrayList<>();
        loadActions();
        loadContacts();
        loadOptions();
        loadView();
    }

    @UiThread
    void loadView() {
        adapter = new FilterAdapter(getActivity(), items);
        listView.setAdapter(adapter);
        adapter.getFilter().filter(manager.getCurrent().getActionText());
    }

    private void loadOptions() {
        items.add(new MainListItem(new OptionsListItem(0, "{fa-comment}", getString(R.string.title__sendAsSMS))));
        items.add(new MainListItem(new OptionsListItem(1, "{fa-pencil}", getString(R.string.title_saveNote))));
        items.add(new MainListItem(new OptionsListItem(2, "{fa-user-plus}", getString(R.string.title_createContact))));
    }

    private void loadActions() {
        items.add(new MainListItem(new ActionListItem(0, "{fa-comment}", getString(R.string.title_text))));
        items.add(new MainListItem(new ActionListItem(1, "{fa-phone}", getString(R.string.title_call))));
        items.add(new MainListItem(new ActionListItem(2, "{fa-sticky-note}", getString(R.string.title_note))));
        items.add(new MainListItem(new ActionListItem(3, "{fa-users}", getString(R.string.title_messages))));
        items.add(new MainListItem(new ActionListItem(4, "{fa-phone}", getString(R.string.title_callLog))));
        items.add(new MainListItem(new ActionListItem(5, "{fa-user}", getString(R.string.title_contacts))));
        items.add(new MainListItem(new ActionListItem(6, "{fa-ban}", getString(R.string.title_flow))));
        items.add(new MainListItem(new ActionListItem(7, "{fa-microphone}", getString(R.string.title_voicemail))));
        items.add(new MainListItem(new ActionListItem(8, "{fa-sticky-note}", getString(R.string.title_notes))));
        items.add(new MainListItem(new ActionListItem(9, "{fa-clock-o}", getString(R.string.title_clock))));
        items.add(new MainListItem(new ActionListItem(10, "{fa-cogs}", getString(R.string.title_settings))));
        items.add(new MainListItem(new ActionListItem(11, "{fa-tint}", getString(R.string.title_theme))));
        items.add(new MainListItem(new ActionListItem(12, "{fa-bell}", getString(R.string.title_notificationScheduler))));
        items.add(new MainListItem(new ActionListItem(13, "{fa-home}", getString(R.string.title_defaultLauncher))));
    }

    private void loadContacts() {
        List<ContactListItem> contactListItems = new ContactsLoader().loadContacts(getActivity());

        for (ContactListItem item : contactListItems) {
            items.add(new MainListItem(item));
        }
    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {
        MainListItem.ItemType type = adapter.getItem(position).getType();

        switch (type) {

            case ACTION_LIST_ITEM:
                position = adapter.getItem(position).getActionListItem().getId();

                switch (position) {
                    case 0:
                        manager.setCurrent(new ActionItem(ActionItem.ActionItemType.TEXT));
                        manager.fireEvent();
                        break;
                    case 1:
                        manager.setCurrent(new ActionItem(ActionItem.ActionItemType.CALL));
                        manager.fireEvent();
                        break;
                    case 2:
                        manager.setCurrent(new ActionItem(ActionItem.ActionItemType.NOTE));
                        manager.fireEvent();
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
                        if (!new ActivityHelper(getActivity()).openNotesApp())
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
                        break;
                    case 12:
//                        if (!new ActivityHelper(getActivity()).openSettingsApp())
//                            UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                        EventBus.getDefault().post(new LoadFragmentEvent(LoadFragmentEvent.NOTIFICATION_SCHEDULER));
                        break;
                    case 13:
                        handleDefaultLauncher();
                        break;
                    default:
                        UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                        break;

                }
                break;
            case CONTACT_ITEM:
                ContactListItem item = adapter.getItem(position).getContactListItem();
                if (item.hasMultipleNumber()) {
                    mContactsListener.onContactNameSelected(item.getContactId(), item.getContactName());
                } else {
                    mContactsListener.onContactNumberSelected(item.getContactId(), item.getContactName(), item.getNumber().getNumber());
                }
                break;
            case OPTION_ITEM:
                position = adapter.getItem(position).getOptionsListItem().getId();

                switch (position) {
                    case 0:
                        manager.getCurrent().setCompleted(true);
                        manager.add(new ActionItem(ActionItem.ActionItemType.TEXT));
                        manager.fireEvent();
                        break;
                    case 1:
                        getActivity().sendBroadcast(new Intent().setAction("minium.co.notes.CREATE_NOTES").putExtra("body", manager.get(ActionItem.ActionItemType.DATA).getActionText()));
                        manager.clear();
                        break;
                    case 2:
                        String inputStr = manager.getCurrent().getActionText();
                        if (PhoneNumberUtils.isGlobalPhoneNumber(inputStr)) {
                            startActivity(new Intent(Intent.ACTION_INSERT).setType(ContactsContract.Contacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.PHONE, inputStr));
                        } else {
                            startActivity(new Intent(Intent.ACTION_INSERT).setType(ContactsContract.Contacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.NAME, inputStr));
                        }
                        manager.clear();
                        break;
                    default:
                        UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                        break;

                }
                break;
        }



    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mContactsListener = (OnContactSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnContactSelectedListener");
        }
    }

    private void handleDefaultLauncher() {
        if (isMyLauncherDefault()) {
            Tracer.d("Launcher2 is the default launcher");
            getActivity().getPackageManager().clearPackagePreferredActivities(getActivity().getPackageName());
            openChooser();
        } else {
            Tracer.d("Launcher2 is not the default launcher: " + getLauncherPackageName());
            if (getLauncherPackageName().equals("android")) {
                openChooser();
            } else
                openSettings();
        }
    }

    private void openChooser() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private boolean isMyLauncherDefault() {
        return getLauncherPackageName().equals(getActivity().getPackageName());
    }

    private String getLauncherPackageName() {
        PackageManager localPackageManager = getActivity().getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        String str = localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
        return str;
    }

    private void openSettings() {
        try {
            Tracer.d("Opening specific app settings");
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

            Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
            launcherIntent.addCategory(Intent.CATEGORY_HOME);
            ResolveInfo resolveInfo = getActivity().getPackageManager().resolveActivity(launcherIntent, PackageManager.MATCH_DEFAULT_ONLY);
            String currentHomePackage = resolveInfo.activityInfo.packageName;

            intent.setData(Uri.parse("package:" + currentHomePackage));
            startActivity(intent);

        } catch ( ActivityNotFoundException e ) {
            Tracer.e(e, e.getMessage());

            //Open the generic Apps page:
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivity(intent);

        }
    }

    @Subscribe
    public void onActionUpdateEvent(ActionItemUpdateEvent event) {
        if (adapter == null) return;
        String newText = event.getText();
        String newFilter = !TextUtils.isEmpty(newText) ? newText : "";

        if (mSearchString != null && mSearchString.equals(newFilter)) {
            return;
        }
        mSearchString = newFilter;
        adapter.getFilter().filter(mSearchString);
    }
}
