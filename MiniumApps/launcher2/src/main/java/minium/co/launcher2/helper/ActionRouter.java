package minium.co.launcher2.helper;

import android.content.Intent;
import android.net.Uri;

import org.androidannotations.annotations.EBean;

import minium.co.launcher2.MainActivity;
import minium.co.launcher2.data.ActionItemManager;
import minium.co.launcher2.events.LoadFragmentEvent;
import minium.co.launcher2.model.ActionItem;

/**
 * Created by Shahab on 6/14/2016.
 */
@EBean
public class ActionRouter {

    MainActivity activity;

    ActionItemManager manager;

    public void onActionItemUpdate(MainActivity activity, ActionItemManager manager) {
        this.activity = activity;
        this.manager = manager;
        ActionItem current = manager.getCurrent();

        switch (current.getType()) {

            case CALL: handleCall(); break;
            case TEXT: handleText(); break;
            case NOTE: handleNote(); break;
            case CONTACT: handleContacts(); break;
            case EMPTY: handleEmpty(); break;
            case DATA: handleData(); break;
        }
    }

    private void handleEmpty() {
        activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.MAIN_FRAGMENT));
    }

    private void handleText() {
        manager.add(new ActionItem(ActionItem.ActionItemType.CONTACT));
        manager.fireEvent();
    }

    private void handleContacts() {
        if (manager.getCurrent().isCompleted()) {
            if (manager.has(ActionItem.ActionItemType.CALL)) {
                activity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + manager.getCurrent().getExtra())));

            } else if (manager.has(ActionItem.ActionItemType.TEXT)) {
                manager.add(new ActionItem(ActionItem.ActionItemType.DATA));
                activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.SEND));
            }
        } else
            activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.CONTACTS_LIST));
    }

    private void handleNote() {
        if (manager.getCurrent().isCompleted()) {
            manager.add(new ActionItem(ActionItem.ActionItemType.DATA));
            activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.OPTIONS));
        }
    }

    private void handleData() {

    }

    private void handleCall() {
        manager.add(new ActionItem(ActionItem.ActionItemType.CONTACT));
        manager.fireEvent();
    }
}
