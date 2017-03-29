package co.minium.launcher3.token;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import co.minium.launcher3.model.ContactListItem;
import co.minium.launcher3.model.MainListItem;
import co.minium.launcher3.msg.SmsObserver;
import de.greenrobot.event.EventBus;
import minium.co.core.log.Tracer;

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
            manager.setCurrent(new TokenItem(TokenItemType.CONTACT));
            manager.getCurrent().setTitle(item.getContactName());
            manager.getCurrent().setExtra1(String.valueOf(item.getContactId()));
            manager.getCurrent().setCompleteType(TokenCompleteType.HALF);
            route();
        } else {
            manager.setCurrent(new TokenItem(TokenItemType.CONTACT));
            manager.getCurrent().setTitle(item.getContactName());
            manager.getCurrent().setExtra1(String.valueOf(item.getContactId()));
            manager.getCurrent().setExtra2(item.getNumber().getNumber());
            manager.getCurrent().setCompleteType(TokenCompleteType.FULL);
            contactPickedDone();
        }
    }

    public void contactNumberPicked(MainListItem item) {
        manager.getCurrent().setExtra2(item.getTitle());
        manager.getCurrent().setCompleteType(TokenCompleteType.FULL);
        contactPickedDone();
    }

    private void contactPickedDone() {
        if (manager.hasCompleted(TokenItemType.DATA) && manager.hasCompleted(TokenItemType.CONTACT)) {
            manager.add(new TokenItem(TokenItemType.END_OP));
        } else if (manager.hasCompleted(TokenItemType.CONTACT)) {
            manager.add(new TokenItem(TokenItemType.DATA));
        }
        route();
    }

    public void sendText(Context context) {
        try {

            if (manager.hasCompleted(TokenItemType.CONTACT) && manager.has(TokenItemType.DATA)) {
                new SmsObserver(context, manager.get(TokenItemType.CONTACT).getExtra2(), manager.get(TokenItemType.DATA).getTitle()).start();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(manager.get(TokenItemType.CONTACT).getExtra2(), null, manager.get(TokenItemType.DATA).getTitle(), null, null);
            } else if (!manager.has(TokenItemType.CONTACT)) {
                manager.getCurrent().setCompleteType(TokenCompleteType.FULL);
                manager.add(new TokenItem(TokenItemType.CONTACT));
                route();
            }
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
//            UIUtils.toast(context, "The message will not get sent.");
        }
    }

    public void call(Activity activity) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            activity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + manager.get(TokenItemType.CONTACT).getExtra2())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
