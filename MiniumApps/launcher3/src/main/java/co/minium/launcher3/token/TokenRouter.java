package co.minium.launcher3.token;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import co.minium.launcher3.helper.ActivityHelper;
import co.minium.launcher3.model.ContactListItem;
import co.minium.launcher3.sms.SmsObserver;
import de.greenrobot.event.EventBus;
import minium.co.core.log.Tracer;
import minium.co.core.util.UIUtils;

/**
 * Created by shahab on 2/16/17.
 */
@EBean
public class TokenRouter {

    @Bean
    TokenManager manager;

    public void route() {
        EventBus.getDefault().post(new TokenUpdateEvent());
    }

    private void handleContacts(int ind) {

    }


    public void setCurrent(TokenItem tokenItem) {
        manager.setCurrent(tokenItem);
        route();
    }

    public void add(TokenItem tokenItem) {
        manager.getCurrent().setCompleteType(TokenCompleteType.FULL);
        manager.add(tokenItem);
        route();
    }

    public void createNote(Context context) {
        context.sendBroadcast(new Intent().setAction("minium.co.notes.CREATE_NOTES")
                .putExtra("body", manager.getCurrent().getTitle()));
        manager.clear();
    }

    public void createContact(Context context) {
        String inputStr = manager.getCurrent().getTitle();
        if (PhoneNumberUtils.isGlobalPhoneNumber(inputStr)) {
            context.startActivity(new Intent(Intent.ACTION_INSERT).setType(ContactsContract.Contacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.PHONE, inputStr));
        } else {
            context.startActivity(new Intent(Intent.ACTION_INSERT).setType(ContactsContract.Contacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.NAME, inputStr));
        }
        manager.clear();
    }

    public void contactPicked(ContactListItem item) {
        if (item.hasMultipleNumber()) {
            manager.getCurrent().setTitle(item.getContactName());
            manager.getCurrent().setExtra1(String.valueOf(item.getContactId()));
            manager.getCurrent().setCompleteType(TokenCompleteType.HALF);
            route();
        } else {
            manager.getCurrent().setTitle(item.getContactName());
            manager.getCurrent().setExtra1(String.valueOf(item.getContactId()));
            manager.getCurrent().setExtra2(item.getNumber().getNumber());
            manager.getCurrent().setCompleteType(TokenCompleteType.FULL);
            contactPickedDone();
        }
    }

    private void contactPickedDone() {
        if (manager.hasCompleted(TokenItemType.DATA) && manager.hasCompleted(TokenItemType.CONTACT)) {
            manager.add(new TokenItem(TokenItemType.END_OP));
            route();
        }
    }

    public void sendText(Context context) {
        try {
            //new SmsObserver(context, phoneNumber, message).start();

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(manager.get(TokenItemType.CONTACT).getExtra2(), null, manager.get(TokenItemType.DATA).getTitle() , null, null);
            new ActivityHelper(context).openMessagingApp();
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
//            UIUtils.toast(context, "The message will not get sent.");
        }
    }
}
