package co.siempo.phone.token;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
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

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.SendSmsEvent;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.msg.SmsObserver;
import co.siempo.phone.utils.DataUtils;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.EventBus;


@EBean
public class TokenRouter {

    String SMS_SENT = "SMS_SENT";
    String SMS_DELIVERED = "SMS_DELIVERED";


    void route() {
        EventBus.getDefault().post(new TokenUpdateEvent());
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
        DataUtils.saveNotes(context, TokenManager.getInstance().getCurrent().getTitle());
    }


    public void createContact(Context context) {
        String inputStr = TokenManager.getInstance().getCurrent().getTitle();
        if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.beta)) && inputStr.equalsIgnoreCase(Constants.ALPHA_SETTING)) {
            if (PrefSiempo.getInstance(context).read(PrefSiempo
                    .IS_ALPHA_SETTING_ENABLE, false)) {
                if (PhoneNumberUtils.isGlobalPhoneNumber(inputStr)) {
                    context.startActivity(new Intent(Intent.ACTION_INSERT).setType(ContactsContract.Contacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.PHONE, inputStr));
                } else {
                    context.startActivity(new Intent(Intent.ACTION_INSERT).setType(ContactsContract.Contacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.NAME, inputStr));
                }
                TokenManager.getInstance().clear();
            } else {
                PrefSiempo.getInstance(context).write(PrefSiempo
                        .IS_ALPHA_SETTING_ENABLE, true);
                new ActivityHelper(context).openSiempoAlphaSettingsApp();
                TokenManager.getInstance().clear();
            }
        } else {
            if (PhoneNumberUtils.isGlobalPhoneNumber(inputStr)) {
                context.startActivity(new Intent(Intent.ACTION_INSERT).setType(ContactsContract.Contacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.PHONE, inputStr));
            } else {
                context.startActivity(new Intent(Intent.ACTION_INSERT).setType(ContactsContract.Contacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.NAME, inputStr));
            }
            TokenManager.getInstance().clear();
        }

    }

    public void contactPicked(MainListItem item) {
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
                String strNumber = TokenManager.getInstance().get(TokenItemType.CONTACT).getExtra2();
                String strMessage = TokenManager.getInstance().get(TokenItemType.DATA).getTitle();
                if (!strMessage.trim().equalsIgnoreCase("")) {
                    new SmsObserver(context, strNumber, strMessage).start();
                    SmsManager smsManager = SmsManager.getDefault();

                    //Added as a part of SSA-1479, in case of long text
                    // messages, splitting the text and sending the message
                    // in multi-part
                    PendingIntent sentPendingIntent = PendingIntent
                            .getBroadcast(context, 0, new Intent(SMS_SENT),
                                    0);
                    PendingIntent deliveredPendingIntent = PendingIntent
                            .getBroadcast(context, 0, new Intent
                                    (SMS_DELIVERED), 0);
                    ArrayList<String> smsBodyParts = smsManager
                            .divideMessage(strMessage);
                    ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
                    ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();

                    for (int i = 0; i < smsBodyParts.size(); i++) {
                        sentPendingIntents.add(sentPendingIntent);
                        deliveredPendingIntents.add(deliveredPendingIntent);
                    }

                    smsManager.sendMultipartTextMessage(strNumber, null,
                            smsBodyParts, sentPendingIntents,
                            deliveredPendingIntents);


                    Toast.makeText(context, "Sending Message...", Toast.LENGTH_LONG).show();
                    String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);


                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + Uri.encode(strNumber)));
                    if (defaultSmsPackageName != null) // Can be null in case that there is no default, then the user would be able to choose any app that supports this intent.
                    {
                        intent.setPackage(defaultSmsPackageName);
                    }
                    context.startActivity(intent);
                    EventBus.getDefault().post(new SendSmsEvent(true));
                } else {
                    UIUtils.toast(context, "Please enter message.");
                }
            } else if (!TokenManager.getInstance().has(TokenItemType.CONTACT)) {
                TokenManager.getInstance().getCurrent().setCompleteType(TokenCompleteType.FULL);
                TokenManager.getInstance().add(new TokenItem(TokenItemType.CONTACT));
                route();
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }
    }

    public void call(Activity activity) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
            CoreApplication.getInstance().logException(e);
            e.printStackTrace();
            UIUtils.alert(activity, activity.getString(R.string.app_not_found));
        }
    }
}
