package co.minium.launcher3.token;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import co.minium.launcher3.model.ContactListItem;
import de.greenrobot.event.EventBus;

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
        add(new TokenItem(TokenItemType.DATA));
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
            route();
        }
    }
}
