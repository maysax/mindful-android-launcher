package co.siempo.phone.main;

import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.call.CallLogActivity_;
import co.siempo.phone.contact.ContactsLoader;
import co.siempo.phone.event.CreateNoteEvent;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.mm.MMTimePickerActivity_;
import co.siempo.phone.model.ContactListItem;
import co.siempo.phone.model.MainListItem;
import co.siempo.phone.model.MainListItemType;
import co.siempo.phone.pause.PauseActivity_;
import co.siempo.phone.tempo.TempoActivity_;
import co.siempo.phone.token.TokenItemType;
import co.siempo.phone.token.TokenRouter;
import de.greenrobot.event.EventBus;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

import static co.siempo.phone.R.string.title_defaultLauncher;

/**
 * Created by shahab on 2/16/17.
 */

class MainFragmentMediator {

    private MainFragment fragment;

    private List<MainListItem> items;
    private List<ContactListItem> contactItems;

    MainFragmentMediator(MainFragment mainFragment) {
        this.fragment = mainFragment;
    }

    void loadData() {
        items = new ArrayList<>();
        loadActions();
        loadContacts();
        loadDefaults();
    }

    void resetData() {
        items.clear();
        loadActions();
        loadContacts();
        loadDefaults();
        if (getAdapter() != null) {
            getAdapter().loadData(items);
            getAdapter().notifyDataSetChanged();
        }
    }

    private void loadActions() {
        items.add(new MainListItem(4, fragment.getString(R.string.title_messages), "fa-users"));
        items.add(new MainListItem(5, fragment.getString(R.string.title_callLog), "fa-phone"));
        items.add(new MainListItem(6, fragment.getString(R.string.title_contacts), "fa-user"));
        items.add(new MainListItem(7, fragment.getString(R.string.title_pause), "fa-ban"));
        // items.add(new MainListItem(8, fragment.getString(R.string.title_voicemail), "fa-microphone"));
        items.add(new MainListItem(9, fragment.getString(R.string.title_notes), "fa-sticky-note"));
        //items.add(new MainListItem(10, fragment.getString(R.string.title_clock), "fa-clock-o"));
        items.add(new MainListItem(11, fragment.getString(R.string.title_settings), "fa-cogs"));
        // items.add(new MainListItem(12, fragment.getString(R.string.title_theme), "fa-tint"));
        items.add(new MainListItem(13, fragment.getString(R.string.title_tempo), "fa-bell"));
        items.add(new MainListItem(14, fragment.getString(R.string.title_map), "fa-street-view"));

        if (!Build.MODEL.toLowerCase().contains("siempo")) {
            items.add(new MainListItem(15, fragment.getString(title_defaultLauncher), "fa-street-view"));
        }

        items.add(new MainListItem(16, fragment.getString(R.string.title_mindfulMorning), "fa-coffee"));
        items.add(new MainListItem(17, fragment.getString(R.string.title_version, BuildConfig.VERSION_NAME), "fa-info-circle"));
        //items.add(new MainListItem(18, fragment.getString(R.string.title_inbox), "fa-inbox"));

        items.add(new MainListItem(19, fragment.getString(R.string.title_feedback), "fa-question-circle"));
        items.add(new MainListItem(20, fragment.getString(R.string.title_calendar), "fa-calendar"));
        items.add(new MainListItem(21, fragment.getString(R.string.title_clock), "fa-clock-o"));

    }

    private void loadContacts() {
        try {
            if (fragment.getManager() != null && fragment.getManager().hasCompleted(TokenItemType.CONTACT)) {
                return;
            }
            if (contactItems == null) {
                contactItems = new ContactsLoader().loadContacts(fragment.getActivity());
            }
            items.addAll(contactItems);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    private void loadDefaults() {
        try {
            if (fragment.getManager().hasCompleted(TokenItemType.CONTACT) && fragment.getManager().has(TokenItemType.DATA) && !fragment.getManager().get(TokenItemType.DATA).getTitle().isEmpty()){
                items.add(new MainListItem(1, fragment.getString(R.string.title_sendAsSMS), R.drawable.icon_sms, MainListItemType.DEFAULT));
            }
            else if (fragment.getManager().hasCompleted(TokenItemType.CONTACT)){
                items.add(new MainListItem(1, fragment.getString(R.string.title_sendAsSMS), R.drawable.icon_sms, MainListItemType.DEFAULT));
                items.add(new MainListItem(4, fragment.getString(R.string.title_call), R.drawable.icon_call, MainListItemType.DEFAULT));
            } else if (fragment.getManager().hasCompleted(TokenItemType.DATA)) {
                items.add(new MainListItem(1, fragment.getString(R.string.title_sendAsSMS), R.drawable.icon_sms, MainListItemType.DEFAULT));
                items.add(new MainListItem(3, fragment.getString(R.string.title_createContact), R.drawable.icon_create_user, MainListItemType.DEFAULT));
                items.add(new MainListItem(2, fragment.getString(R.string.title_saveNote), R.drawable.icon_save_note, MainListItemType.DEFAULT));
            } else {
                items.add(new MainListItem(1, fragment.getString(R.string.title_sendAsSMS), R.drawable.icon_sms, MainListItemType.DEFAULT));
                items.add(new MainListItem(3, fragment.getString(R.string.title_createContact), R.drawable.icon_create_user, MainListItemType.DEFAULT));
                items.add(new MainListItem(2, fragment.getString(R.string.title_saveNote), R.drawable.icon_save_note, MainListItemType.DEFAULT));
            }
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    List<MainListItem> getItems() {
        return items;
    }

    private MainListAdapter getAdapter() {
        return fragment.getAdapter();
    }

    void listItemClicked(TokenRouter router, int position) {
        MainListItemType type = getAdapter().getItem(position).getItemType();

        switch (type) {

            case CONTACT:
                router.contactPicked((ContactListItem) getAdapter().getItem(position));
                break;
            case ACTION:
                position = getAdapter().getItem(position).getId();
                switch (position) {
                    case 1:
                    case 2:
                    case 3:
                    case 4: new ActivityHelper(fragment.getActivity()).openMessagingApp(); break;
                    case 5: CallLogActivity_.intent(fragment.getActivity()).start(); break;
                    case 6: new ActivityHelper(fragment.getActivity()).openContactsApp(); break;
                    case 7: PauseActivity_.intent(fragment.getActivity()).start(); break;
                    case 8: break;
                    case 9: new ActivityHelper(fragment.getActivity()).openNotesApp(false); break;
                    case 10: break;
                    case 11: new ActivityHelper(fragment.getActivity()).openSettingsApp(); break;
                    case 12: break;
                    case 13:
                        TempoActivity_.intent(fragment.getActivity()).start(); break;
                    case 14:
                       // SiempoMapActivity_.intent(fragment.getActivity()).start(); break;
                        new ActivityHelper(fragment.getActivity()).openGMape(Constants.GOOGLE_MAP_PACKAGE); break;

                    case 15:
                        new ActivityHelper(fragment.getActivity()).handleDefaultLauncher((CoreActivity) fragment.getActivity());
                        break;
                    case 16:
                        MMTimePickerActivity_.intent(fragment.getActivity()).start();
                        break;
                    case 17:
                        break;
                    case 18:
                        //new ActivityHelper(fragment.getActivity()).openGoogleInbox(); break;
                    case 19:
                        new ActivityHelper(fragment.getActivity()).openFeedback(); break;
                    case 20:
                        new ActivityHelper(fragment.getActivity()).openGMape(Constants.CALENDAR_PACKAGE); break;
                    case 21:
                        new ActivityHelper(fragment.getActivity()).openGMape(Constants.CLOCK_PACKAGE); break;
                }
                break;
            case DEFAULT:
                position = getAdapter().getItem(position).getId();

                switch (position) {
                    case 1:
                        router.sendText(fragment.getActivity());
                        break;
                    case 2:
                        router.createNote(fragment.getActivity());
                        EventBus.getDefault().post(new CreateNoteEvent());
                        break;
                    case 3:
                        router.createContact(fragment.getActivity());
                        break;
                    case 4:
                        router.call(fragment.getActivity());
                        break;
                    default:
                        UIUtils.alert(fragment.getActivity(), fragment.getString(R.string.msg_not_yet_implemented));
                        break;
                }
                break;
            case NUMBERS:
                router.contactNumberPicked(getAdapter().getItem(position));
                break;
        }
    }

    public void contactPicker() {
        items.clear();
        loadContacts();
        loadDefaults();
        getAdapter().loadData(items);
        getAdapter().notifyDataSetChanged();
    }

    public void contactNumberPicker(int selectedContactId) {

        items.clear();

        for (ContactListItem item : contactItems) {
            if (item.getContactId() == selectedContactId) {
                for (ContactListItem.ContactNumber number : item.getNumbers()) {
                    items.add(new MainListItem(selectedContactId, number.getNumber(), R.drawable.icon_call, MainListItemType.NUMBERS));
                }
            }
        }
        getAdapter().loadData(items);
        getAdapter().notifyDataSetChanged();
    }

    public void listItemClicked2(TokenRouter router, int position) {

    }

    public void defaultData() {
        items.clear();
        loadDefaults();
        getAdapter().loadData(items);
        getAdapter().notifyDataSetChanged();
    }
}
