package co.siempo.phone.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.activities.DashboardActivity;
import co.siempo.phone.adapters.MainListAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.CreateNoteEvent;
import co.siempo.phone.event.SendSmsEvent;
import co.siempo.phone.fragments.PaneFragment;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.models.MainListItemType;
import co.siempo.phone.token.TokenItem;
import co.siempo.phone.token.TokenItemType;
import co.siempo.phone.token.TokenManager;
import co.siempo.phone.token.TokenRouter;
import co.siempo.phone.utils.ContactsLoader;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.Sorting;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.EventBus;


/**
 * Created by shahab on 2/16/17.
 */

public class MainFragmentMediator {

    private SharedPreferences launcher3Prefs;
    private Context context;
    private PaneFragment fragment;
    private List<MainListItem> items;
    private List<MainListItem> contactItems;

    public MainFragmentMediator(PaneFragment paneFragment) {
        this.fragment = paneFragment;
        context = this.fragment.getActivity();
        launcher3Prefs =
                context.getSharedPreferences("Launcher3Prefs", 0);
    }

    public void loadData() {

        if (TextUtils.isEmpty(PrefSiempo.getInstance(context).read(PrefSiempo.SERACH_LIST, ""))) {
            items = new ArrayList<>();
            contactItems = new ArrayList<>();
            loadActions();
            loadContacts();
            loadDefaults();
            items = Sorting.sortList(items);
            PackageUtil.storeSearchList(items, context);
        } else {
            items = PackageUtil.getSearchList(context);
        }
    }

    public void resetData() {
        items = new ArrayList<>();

        if (TextUtils.isEmpty(PrefSiempo.getInstance(context).read(PrefSiempo.SERACH_LIST, ""))) {
            items = new ArrayList<>();

            contactItems = new ArrayList<>();

            loadActions();
            loadContacts();
            loadDefaults();
            items = Sorting.sortList(items);
            PackageUtil.storeSearchList(items, context);
        } else {
            items = PackageUtil.getSearchList(context);
            items = Sorting.sortList(items);
        }
        if (getAdapter() != null) {
            getAdapter().loadData(items);
            getAdapter().notifyDataSetChanged();
        }

    }

    private void loadActions() {
        new MainListItemLoader(fragment.getActivity()).loadItems(items, fragment);
    }


    private void loadContacts() {
        try {
            List<MainListItem> localList = null;
            if (fragment != null && fragment.getManager() != null && fragment.getManager().hasCompleted(TokenItemType.CONTACT)) {
                return;
            }
            if (fragment != null && contactItems.size() == 0) {

                localList = new ContactsLoader().loadContacts(fragment.getActivity());
                contactItems = localList;

            }
            if (localList != null) {
                items.addAll(localList);
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }

    }

    private void loadDefaults() {
        try {
            if (fragment != null) {

                if (fragment.getManager().hasCompleted(TokenItemType.CONTACT) && fragment.getManager().has(TokenItemType.DATA) && !fragment.getManager().get(TokenItemType.DATA).getTitle().isEmpty()) {
                    items.add(new MainListItem(1, fragment.getString(R.string.title_sendAsSMS), R.drawable.ic_messages_tool, MainListItemType.DEFAULT));
                } else if (fragment.getManager().hasCompleted(TokenItemType.CONTACT)) {
                    items.add(new MainListItem(1, fragment.getString(R.string.title_sendAsSMS), R.drawable.ic_messages_tool, MainListItemType.DEFAULT));
                } else if (fragment.getManager().hasCompleted(TokenItemType.DATA)) {
                    items.add(new MainListItem(1, fragment.getString(R
                            .string.title_sendAsSMS), R.drawable.ic_messages_tool,
                            MainListItemType.DEFAULT));
                    items.add(new MainListItem(2, fragment.getString(R
                            .string.title_saveNote), R.drawable.ic_notes_tool,
                            MainListItemType.DEFAULT));
                    items.add(new MainListItem(3, fragment.getString(R.string.title_swipe), R.drawable.ic_default_swipe, MainListItemType.DEFAULT));
                } else {
                    items.add(new MainListItem(1, fragment.getString(R.string.title_sendAsSMS), R.drawable.ic_messages_tool, MainListItemType.DEFAULT));
                    items.add(new MainListItem(2, fragment.getString(R.string.title_saveNote), R.drawable.ic_notes_tool, MainListItemType.DEFAULT));
                    items.add(new MainListItem(3, fragment.getString(R.string.title_swipe), R.drawable.ic_default_swipe, MainListItemType.DEFAULT));
                }
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }

    }

    public List<MainListItem> getItems() {
        return items;
    }

    private MainListAdapter getAdapter() {
        return fragment.getAdapter();
    }

    public void listItemClicked(TokenRouter router, int position, String data) {
        MainListItemType type;
        type = getAdapter().getItem(position).getItemType();
        if (type != null)
            switch (type) {
                case CONTACT:
                    if (router != null) {
                        router.contactPicked(getAdapter().getItem(position));
                        FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_CONTACT_PICK, "", data);
                    }
                    break;
                case ACTION:
                    if (getAdapter() != null && TextUtils.isEmpty(getAdapter().getItem(position).getPackageName())) {
                        items.get(position).setDate(Calendar.getInstance().getTime());
                        items.set(position, items.get(position));
                        items = Sorting.sortList(items);
                        PackageUtil.storeSearchList(items, context);
                        position = getAdapter().getItem(position).getId();
                        new MainListItemLoader(fragment.getActivity()).listItemClicked(position);
                    } else {
                        if (fragment != null) {
                            DashboardActivity.isTextLenghGreater = "";
                            UIUtils.hideSoftKeyboard(fragment.getActivity(), fragment.getActivity().getWindow().getDecorView().getWindowToken());
                            boolean status = new ActivityHelper(fragment.getActivity()).openAppWithPackageName(getAdapter().getItem(position).getPackageName());
                            FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_APPLICATION_PICK, getAdapter().getItem(position).getPackageName(), "");
                            if (status) {
                                items.get(position).setDate(Calendar.getInstance().getTime());
                                items.set(position, items.get(position));
                                PackageUtil.storeSearchList(items, context);
                            }
                        }
                    }
                    break;
                case DEFAULT:
                    position = getAdapter().getItem(position).getId();
                    switch (position) {
                        case 1:
                            if (router != null && fragment != null) {
                                router.sendText(fragment.getActivity());
                                FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_SMS, "", data);
                            }
                            break;
                        //Notes
                        case 2:
                            if (router != null && fragment != null) {
                                router.createNote(fragment.getActivity());
                                FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_SAVE_NOTE, "", data);
                                EventBus.getDefault().post(new CreateNoteEvent());
                                new ActivityHelper(context).openNotesApp(true);
                            }
                            break;
                        //Write code for Junk Food Pane on this code
                        case 3:

                            if (router != null && fragment != null) {
                                fragment.setCurrentPage(0);
                            }
                            break;
                        case 4:
                            if (router != null && fragment != null) {
                                router.call(fragment.getActivity());
                                DashboardActivity.isTextLenghGreater = "";
                                FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_CALL, "", data);
                                EventBus.getDefault().post(new SendSmsEvent(true, "", ""));
                            }
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
                            DashboardActivity.isTextLenghGreater = "";
                            FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_CALL, "", data);
                            EventBus.getDefault().post(new SendSmsEvent(true, "", ""));
                        } catch (Exception e) {
                            CoreApplication.getInstance().logException(e);
                            e.printStackTrace();
                        }
                    } else {
                        if (router != null) {
                            router.contactNumberPicked(getAdapter().getItem(position));
                            FirebaseHelper.getIntance().logIFAction(FirebaseHelper.ACTION_CONTACT_PICK, "", data);
                        }
                    }
                    break;
                default:
                    break;
            }
    }


    public void loadDefaultData() {
        items = new ArrayList<>();

        if (TextUtils.isEmpty(PrefSiempo.getInstance(context).read(PrefSiempo.SERACH_LIST, ""))) {
            items = new ArrayList<>();

            contactItems = new ArrayList<>();

            loadActions();
            loadContacts();
            loadDefaults();
            items = Sorting.sortList(items);
            PackageUtil.storeSearchList(items, context);
        } else {
            TokenItem current = TokenManager.getInstance().getCurrent();
            List<MainListItem> defaultItems = new ArrayList<>();
            items = PackageUtil.getSearchList(context);
            for (MainListItem cItems : items) {
                if (cItems.getItemType() == MainListItemType.DEFAULT) {
                    defaultItems.add(cItems);
                }
            }
            items = Sorting.sortList(defaultItems);
        }
        if (getAdapter() != null) {
            getAdapter().loadData(items);
            getAdapter().notifyDataSetChanged();
        }

    }

    public void contactPicker() {
        items = new ArrayList<>();

        if (TextUtils.isEmpty(PrefSiempo.getInstance(context).read(PrefSiempo.SERACH_LIST, ""))) {
            items = new ArrayList<>();

            contactItems = new ArrayList<>();

            loadActions();
            loadContacts();
            loadDefaults();
            items = Sorting.sortList(items);
            PackageUtil.storeSearchList(items, context);
        } else {
            TokenItem current = TokenManager.getInstance().getCurrent();
            contactItems = new ArrayList<>();
            List<MainListItem> abc = new ArrayList<>();
            items = PackageUtil.getSearchList(context);
            for (MainListItem cItems : items) {
                if (cItems.getItemType() == MainListItemType.CONTACT || cItems.getItemType() == MainListItemType.DEFAULT) {
                    abc.add(cItems);
                }
            }
            contactItems = abc;
            items = Sorting.sortList(abc);
        }
        if (getAdapter() != null) {
            getAdapter().loadData(items);
            getAdapter().notifyDataSetChanged();
        }

    }

    public void contactNumberPicker(int selectedContactId) {

        items = new ArrayList<>();

        if (contactItems != null) {
            for (MainListItem item : contactItems) {
                if (item != null && item.getContactId() == selectedContactId) {
                    for (MainListItem.ContactNumber number : item.getNumbers()) {
                        items.add(new MainListItem(selectedContactId, number.getNumber(), R.drawable.icon_call, MainListItemType.NUMBERS));
                    }
                }
            }
            getAdapter().loadData(items);
            getAdapter().notifyDataSetChanged();
        }

    }

    public void defaultData() {
        items = new ArrayList<>();

        if (TextUtils.isEmpty(PrefSiempo.getInstance(context).read(PrefSiempo.SERACH_LIST, ""))) {
            items = new ArrayList<>();

            contactItems = new ArrayList<>();

            loadActions();
            loadContacts();
            loadDefaults();
            items = Sorting.sortList(items);
            PackageUtil.storeSearchList(items, context);
        } else {
            List<MainListItem> defaultItems = new ArrayList<>();
            items = PackageUtil.getSearchList(context);
            for (MainListItem cItems : items) {
                if (cItems.getItemType() == MainListItemType.DEFAULT && !cItems.getTitle().equalsIgnoreCase(context.getResources
                        ().getString
                        (R.string.title_saveNote))) {
                    defaultItems.add(cItems);
                }
            }
            items = Sorting.sortList(defaultItems);
        }
        if (getAdapter() != null) {
            getAdapter().loadData(items);
            getAdapter().notifyDataSetChanged();
        }
    }

}
