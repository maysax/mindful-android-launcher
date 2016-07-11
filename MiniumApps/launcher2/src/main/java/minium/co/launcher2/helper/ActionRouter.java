package minium.co.launcher2.helper;

import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

import org.androidannotations.annotations.EBean;

import minium.co.core.log.Tracer;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.MainActivity;
import minium.co.launcher2.data.ActionItemManager;
import minium.co.launcher2.events.LoadFragmentEvent;
import minium.co.launcher2.messages.SmsObserver;
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
            case CONTACT_NUMBER: handleContactNumber(); break;
            case EMPTY: handleEmpty(); break;
            case DATA: handleData(); break;
            case END_OP: handleEndOp(); break;
        }
    }

    private void handleEndOp() {
        if (manager.has(ActionItem.ActionItemType.TEXT)) {
            sendSMS(manager.get(ActionItem.ActionItemType.CONTACT_NUMBER).getActionText(), manager.get(ActionItem.ActionItemType.DATA).getActionText());
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            new SmsObserver(activity, phoneNumber, message).start();

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message , null, null);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
            UIUtils.toast(activity, "The message will not get sent.");
        }
    }

    private void handleEmpty() {
        activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.MAIN_FRAGMENT));
    }

    private void handleText() {
        if (manager.has(ActionItem.ActionItemType.CONTACT) && manager.has(ActionItem.ActionItemType.DATA)) {
            if (manager.get(ActionItem.ActionItemType.DATA).isCompleted()) {
                handleEndOp();
            } else
                activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.CONTEXTUAL_OPTIONS));
        } else if (manager.has(ActionItem.ActionItemType.CONTACT)) {
            manager.add(new ActionItem(ActionItem.ActionItemType.DATA));
            activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.CONTEXTUAL_OPTIONS));
        } else {
            manager.add(new ActionItem(ActionItem.ActionItemType.CONTACT));
            handleContacts();
        }
    }

    private void handleContacts() {
        if (manager.getCurrent().isCompleted()) {
            manager.add(new ActionItem(ActionItem.ActionItemType.CONTACT_NUMBER));
            handleContactNumber();
        } else
            activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.CONTACTS_LIST));

    }

    private void handleContactNumber() {
        if (manager.getCurrent().isCompleted()) {
            if (manager.has(ActionItem.ActionItemType.DATA)) {
                activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.CONTEXTUAL_OPTIONS));
            } else if (manager.has(ActionItem.ActionItemType.TEXT)) {
                manager.add(new ActionItem(ActionItem.ActionItemType.DATA));
                activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.CONTEXTUAL_OPTIONS));
            } else if (manager.has(ActionItem.ActionItemType.CALL)) {
                activity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + manager.getCurrent().getExtra())));
                manager.clear();
            } else {
                manager.add(new ActionItem(ActionItem.ActionItemType.DATA));
                activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.CONTEXTUAL_OPTIONS));
            }
        } else {
            activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.CONTACTS_NUMBER_LIST));
        }
    }

    private void handleNote() {
        if (!manager.has(ActionItem.ActionItemType.DATA)) {
            manager.add(new ActionItem(ActionItem.ActionItemType.DATA));
        }
        activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.CONTEXTUAL_OPTIONS));

    }

    private void handleData() {
        if (manager.has(ActionItem.ActionItemType.CONTACT) && manager.has(ActionItem.ActionItemType.TEXT)) {
            activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.CONTEXTUAL_OPTIONS));
        } else if (manager.getLength() == 1) {
            handleEmpty();
        }
    }

    private void handleCall() {

        if (manager.has(ActionItem.ActionItemType.CONTACT_NUMBER)) {
            activity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + manager.get(ActionItem.ActionItemType.CONTACT_NUMBER).getActionText())));
            manager.clear();
        } else {
            manager.add(new ActionItem(ActionItem.ActionItemType.CONTACT));
            handleContacts();
        }

    }
}
