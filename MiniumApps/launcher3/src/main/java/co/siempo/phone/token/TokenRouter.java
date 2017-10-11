package co.siempo.phone.token;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.widget.Toast;


import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import co.siempo.phone.MainActivity;
import co.siempo.phone.event.SearchLayoutEvent;
import co.siempo.phone.event.SendSmsEvent;
import co.siempo.phone.model.ContactListItem;
import co.siempo.phone.model.MainListItem;
import co.siempo.phone.msg.SmsObserver;
import de.greenrobot.event.EventBus;
import minium.co.core.log.Tracer;
import minium.co.notes.utils.DataUtils;


@EBean
public class TokenRouter {


    void route() {
        EventBus.getDefault().post(new TokenUpdateEvent());
    }

    private void handleContacts(int ind) {

    }

    void setCurrent(TokenItem tokenItem) {
        TokenManager.getInstance().setCurrent(tokenItem);
        route();
    }
    public void add(TokenItem tokenItem) {
        TokenManager.getInstance().getCurrent().setCompleteType(TokenCompleteType.FULL);
        TokenManager.getInstance().add(tokenItem);
        route();
    }

    public void createNote(Context context) {
        context.sendBroadcast(new Intent().setAction("minium.co.notes.CREATE_NOTES")
                .putExtra(DataUtils.NOTE_TITLE, TokenManager.getInstance().getCurrent().getTitle()));
        TokenManager.getInstance().clear();
    }

    public void createContact(Context context) {
        String inputStr = TokenManager.getInstance().getCurrent().getTitle();
        if (PhoneNumberUtils.isGlobalPhoneNumber(inputStr)) {
            context.startActivity(new Intent(Intent.ACTION_INSERT).setType(ContactsContract.Contacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.PHONE, inputStr));
        } else {
            context.startActivity(new Intent(Intent.ACTION_INSERT).setType(ContactsContract.Contacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.NAME, inputStr));
        }
        TokenManager.getInstance().clear();
    }

    public void contactPicked(ContactListItem item) {
        if (item.hasMultipleNumber()) {
            TokenManager.getInstance().setCurrent(new TokenItem(TokenItemType.CONTACT));
            TokenManager.getInstance().getCurrent().setTitle(item.getContactName());
            TokenManager.getInstance().getCurrent().setExtra1(String.valueOf(item.getContactId()));
            TokenManager.getInstance().getCurrent().setCompleteType(TokenCompleteType.HALF);
            route();
        } else {
            TokenManager.getInstance().setCurrent(new TokenItem(TokenItemType.CONTACT));
            TokenManager.getInstance().getCurrent().setTitle(item.getContactName());
            TokenManager.getInstance().getCurrent().setExtra1(String.valueOf(item.getContactId()));
            TokenManager.getInstance().getCurrent().setExtra2(item.getNumber().getNumber());
            TokenManager.getInstance().getCurrent().setCompleteType(TokenCompleteType.FULL);
            contactPickedDone();
        }
    }

    public void contactNumberPicked(MainListItem item) {
        TokenManager.getInstance().getCurrent().setExtra2(item.getTitle());
        TokenManager.getInstance().getCurrent().setCompleteType(TokenCompleteType.FULL);
        contactPickedDone();
    }

    private void contactPickedDone() {
        if (TokenManager.getInstance().hasCompleted(TokenItemType.DATA) && TokenManager.getInstance().hasCompleted(TokenItemType.CONTACT)) {
            TokenManager.getInstance().add(new TokenItem(TokenItemType.END_OP));
        } else if (TokenManager.getInstance().hasCompleted(TokenItemType.CONTACT)) {
            TokenManager.getInstance().add(new TokenItem(TokenItemType.DATA));
        }
        route();
    }

    public void sendText(Context context) {
        try {
            if (TokenManager.getInstance().hasCompleted(TokenItemType.CONTACT) && TokenManager.getInstance().has(TokenItemType.DATA)) {
                String strNumber  = TokenManager.getInstance().get(TokenItemType.CONTACT).getExtra2();
                String strMessage  =  TokenManager.getInstance().get(TokenItemType.DATA).getTitle();
                new SmsObserver(context, strNumber, strMessage).start();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(strNumber, null, strMessage, null, null);
                Toast.makeText(context, "Sending Message...", Toast.LENGTH_LONG).show();
                String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);


                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + Uri.encode(strNumber)));
                if (defaultSmsPackageName != null) // Can be null in case that there is no default, then the user would be able to choose any app that supports this intent.
                {
                    intent.setPackage(defaultSmsPackageName);
                }
                context.startActivity(intent);
                EventBus.getDefault().post(new SendSmsEvent(true,strNumber,strMessage));
            } else if (!TokenManager.getInstance().has(TokenItemType.CONTACT)) {
                TokenManager.getInstance().getCurrent().setCompleteType(TokenCompleteType.FULL);
                TokenManager.getInstance().add(new TokenItem(TokenItemType.CONTACT));
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
            activity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + TokenManager.getInstance().get(TokenItemType.CONTACT).getExtra2())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
