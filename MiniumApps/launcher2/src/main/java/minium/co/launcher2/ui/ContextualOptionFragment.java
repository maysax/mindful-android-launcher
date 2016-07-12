package minium.co.launcher2.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.widget.ListView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.Subscribe;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.data.ActionItemManager;
import minium.co.launcher2.events.ActionItemUpdateEvent;
import minium.co.launcher2.filter.FilterAdapter;
import minium.co.launcher2.model.ActionItem;
import minium.co.launcher2.model.MainListItem;
import minium.co.launcher2.model.OptionsListItem;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_filter)
public class ContextualOptionFragment extends CoreFragment {

    @ViewById
    ListView listView;

    @Pref
    DroidPrefs_ prefs;

    @Bean
    ActionItemManager manager;

    FilterAdapter adapter;

    List<MainListItem> items;

    private String mSearchString = null;


    public ContextualOptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Background
    void loadData() {
        items = new ArrayList<>();
        loadOptions();
        loadView();
    }

    @UiThread
    void loadView() {
        adapter = new FilterAdapter(getActivity(), items);
        listView.setAdapter(adapter);
    }

    private void loadOptions() {
        items.clear();

        if (manager.has(ActionItem.ActionItemType.TEXT))
            items.add(new MainListItem(new OptionsListItem(0, "{fa-paper-plane}", "Send")));
        else if (manager.getCurrent().getType() == ActionItem.ActionItemType.CONTACT_NUMBER) {

        }
        else if (manager.getCurrent().getType() == ActionItem.ActionItemType.DATA) {
            if (manager.has(ActionItem.ActionItemType.NOTE)) {
                items.add(new MainListItem(new OptionsListItem(4, "{fa-pencil}", "Save Note")));
            }
            else if (manager.getCurrent().getActionText().isEmpty()) {
                if (manager.has(ActionItem.ActionItemType.CONTACT_NUMBER)) {
                    items.add(new MainListItem(new OptionsListItem(1, "{fa-phone}", "Call")));
                    items.add(new MainListItem(new OptionsListItem(2, "{fa-comment-o}", "Text")));
                    items.add(new MainListItem(new OptionsListItem(3, "{fa-user}", "View Contact")));
                }
            }
            else {
                if (manager.has(ActionItem.ActionItemType.CONTACT_NUMBER)) {
                    if (manager.has(ActionItem.ActionItemType.TEXT))
                        items.add(new MainListItem(new OptionsListItem(0, "{fa-paper-plane}", "Send")));
                    else {
                        items.add(new MainListItem(new OptionsListItem(2, "{fa-comment-o}", "Text")));
                        items.add(new MainListItem(new OptionsListItem(3, "{fa-user}", "View Contact")));
                    }
                } else {
                    items.add(new MainListItem(new OptionsListItem(4, "{fa-pencil}", "Save Note")));
                    items.add(new MainListItem(new OptionsListItem(5, "{fa-user-plus}", "Create Contact")));
                }
            }
        } else if (manager.getCurrent().getType() == ActionItem.ActionItemType.NOTE) {
            items.add(new MainListItem(new OptionsListItem(4, "{fa-pencil}", "Save Note")));
        }

        notifyDataSetChanged();
    }

    @UiThread
    void notifyDataSetChanged() {
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void loadSendingOption() {
        items.clear();

        items.add(new MainListItem(new OptionsListItem(6, "{fa-spinner spin}", "Sending...")));
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {
        position = adapter.getItem(position).getOptionsListItem().getPosition();

        switch (position) {
            case 0:
                // TODO: progress bar
                loadSendingOption();
                manager.getCurrent().setCompleted(true);
                if (!manager.has(ActionItem.ActionItemType.TEXT)) {
                    manager.add(new ActionItem(ActionItem.ActionItemType.TEXT));
                }
                manager.add(new ActionItem(ActionItem.ActionItemType.END_OP));


                manager.fireEvent();
                break;
            case 1:
                manager.add(new ActionItem(ActionItem.ActionItemType.CALL));
                manager.fireEvent();
                break;
            case 2:
                if (manager.getCurrent().getActionText().isEmpty()) {
                    manager.setCurrent(new ActionItem(ActionItem.ActionItemType.TEXT));
                    manager.fireEvent();
                    loadOptions();
                } else {
                    manager.getCurrent().setCompleted(true);
                    manager.add(new ActionItem(ActionItem.ActionItemType.TEXT));
                    manager.fireEvent();
                    loadSendingOption();
                }
                break;
            case 3:
                Intent contactView = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, manager.get(ActionItem.ActionItemType.CONTACT).getExtra());
                contactView.setData(uri);
                startActivity(contactView);
                break;
            case 4:
                UIUtils.confirm(getActivity(), getString(R.string.msg_noteSaved), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        manager.clear();
                    }
                });
                break;
            case 5:
                String inputStr = manager.getCurrent().getActionText();
                if (PhoneNumberUtils.isGlobalPhoneNumber(inputStr)) {
                    startActivity(new Intent(Intent.ACTION_INSERT).setType(ContactsContract.Contacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.PHONE, inputStr));
                } else {
                    startActivity(new Intent(Intent.ACTION_INSERT).setType(ContactsContract.Contacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.NAME, inputStr));
                }
                break;
            case 6:
                break;
            default:
                UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
        }

    }

    @Subscribe
    public void onActionUpdateEvent(ActionItemUpdateEvent event) {
        if (manager.getCurrent().getType() == ActionItem.ActionItemType.DATA) {
            loadOptions();
        }
    }
}
