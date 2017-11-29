package co.siempo.phone.main;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.MainActivity;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.applist.AppDrawerActivity_;
import co.siempo.phone.contact.ContactsLoader;
import co.siempo.phone.event.CreateNoteEvent;
import co.siempo.phone.event.SendSmsEvent;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.mm.MMTimePickerActivity_;
import co.siempo.phone.mm.MindfulMorningActivity_;
import co.siempo.phone.model.ContactListItem;
import co.siempo.phone.model.MainListItem;
import co.siempo.phone.model.MainListItemType;
import co.siempo.phone.pause.PauseActivity_;
import co.siempo.phone.service.ApiClient_;
import co.siempo.phone.settings.SiempoSettingsDefaultAppActivity;
import co.siempo.phone.tempo.TempoActivity_;
import co.siempo.phone.token.TokenItemType;
import co.siempo.phone.token.TokenManager;
import co.siempo.phone.token.TokenRouter;
import de.greenrobot.event.EventBus;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.CheckVersionEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

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
        contactItems = new ArrayList<>();
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
        new MainListItemLoader(fragment.getActivity()).loadItems(items, fragment);
    }


    private void loadAppList() {
        try {
            if (Launcher3App.getInstance().getPackagesList() != null && Launcher3App.getInstance().getPackagesList().size() > 0) {
                items.clear();
                for (ApplicationInfo applicationInfo : Launcher3App.getInstance().getPackagesList()) {
                    String appName = applicationInfo.loadLabel(fragment.getActivity().getPackageManager()).toString();
//                    items.add(new MainListItem(appName,MainListItemType.APPS,applicationInfo));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadContacts() {
        try {
            if (fragment.getManager() != null && fragment.getManager().hasCompleted(TokenItemType.CONTACT)) {
                return;
            }
            if (contactItems.size() == 0) {
                contactItems = new ContactsLoader().loadContacts(fragment.getActivity());
            }
            items.addAll(contactItems);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    private void loadDefaults() {
        try {
            if (fragment.getManager().hasCompleted(TokenItemType.CONTACT) && fragment.getManager().has(TokenItemType.DATA) && !fragment.getManager().get(TokenItemType.DATA).getTitle().isEmpty()) {
                items.add(new MainListItem(1, fragment.getString(R.string.title_sendAsSMS), R.drawable.icon_sms, MainListItemType.DEFAULT));
            } else if (fragment.getManager().hasCompleted(TokenItemType.CONTACT)) {
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
                items.add(new MainListItem(4, fragment.getString(R.string.title_call), R.drawable.icon_call, MainListItemType.NUMBERS));
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
        MainListItemType type;
        type = getAdapter().getItem(position).getItemType();
        if (type != null)
            switch (type) {
                case CONTACT:
                    router.contactPicked((ContactListItem) getAdapter().getItem(position));
                    FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_CONTACT_PICK, "");
                    break;
                case ACTION:
                    if (getAdapter().getItem(position).getApplicationInfo() == null) {
                        position = getAdapter().getItem(position).getId();
                        new MainListItemLoader(fragment.getActivity()).firebaseEvent(position);
                        new MainListItemLoader(fragment.getActivity()).listItemClicked(position);
                    } else {
                        UIUtils.hideSoftKeyboard(fragment.getActivity(), fragment.getActivity().getWindow().getDecorView().getWindowToken());
                        new ActivityHelper(fragment.getActivity()).openAppWithPackageName(getAdapter().getItem(position).getApplicationInfo().packageName);
                        MainActivity.isTextLenghGreater = "";
                        FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_APPLICATION_PICK, getAdapter().getItem(position).getApplicationInfo().packageName);
                    }
                    break;
                case DEFAULT:
                    position = getAdapter().getItem(position).getId();

                    switch (position) {
                        case 1:
                            router.sendText(fragment.getActivity());
                            FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_SMS, "");
                            break;
                        case 2:
                            router.createNote(fragment.getActivity());
                            FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_SAVE_NOTE, "");
                            EventBus.getDefault().post(new CreateNoteEvent());
                            break;
                        case 3:
                            router.createContact(fragment.getActivity());
                            FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_CREATE_CONTACT, "");
                            break;
                        case 4:
                            router.call(fragment.getActivity());
                            MainActivity.isTextLenghGreater = "";
                            FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_CALL, "");
                            EventBus.getDefault().post(new SendSmsEvent(true, "", ""));
                            break;
                        default:
                            UIUtils.alert(fragment.getActivity(), fragment.getString(R.string.msg_not_yet_implemented));
                            break;
                    }
                    break;
                case NUMBERS:
                    if (getAdapter().getItem(position).getTitle().trim().equalsIgnoreCase("call") && getAdapter().getItem(position).getId() == 4) {
                        try {
                            fragment.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + TokenManager.getInstance().getCurrent().getExtra2())));
                            MainActivity.isTextLenghGreater = "";
                            FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_CALL, "");
                            EventBus.getDefault().post(new SendSmsEvent(true, "", ""));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        router.contactNumberPicked(getAdapter().getItem(position));
                        FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_CONTACT_PICK, "");
                    }
                    break;
                default:
                    break;
            }
    }

    void contactPicker() {
        items.clear();
        loadContacts();
        loadDefaults();
        getAdapter().loadData(items);
        getAdapter().notifyDataSetChanged();
    }

    void contactNumberPicker(int selectedContactId) {
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

    void listItemClicked2(TokenRouter router, int position) {

    }

    void defaultData() {
        items.clear();
        loadDefaults();
        getAdapter().loadData(items);
        getAdapter().notifyDataSetChanged();
    }


    public void installedApplicationList() {
        items.clear();
        loadAppList();
        getAdapter().loadData(items);
        getAdapter().notifyDataSetChanged();
    }




}
