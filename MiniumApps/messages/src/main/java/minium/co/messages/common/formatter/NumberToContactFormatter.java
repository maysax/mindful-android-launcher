package minium.co.messages.common.formatter;

import android.content.Context;
import android.telephony.TelephonyManager;

import minium.co.core.app.CoreApplication;

public class NumberToContactFormatter implements Formatter {
    String mCountryIso;

    @Override
    public String format(String text) {
/* SKIP       PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Iterable<PhoneNumberMatch> matches = phoneNumberUtil.findNumbers(text, getCurrentCountryIso());
        for (PhoneNumberMatch match : matches) {
            Contact contact = Contact.get(match.rawString(), true);
            if (contact.isNamed()) {
                String nameAndNumber = phoneNumberUtil.format(match.number(), PhoneNumberFormat.NATIONAL)
                        + " (" + contact.getName() + ")";
                text = text.replace(match.rawString(), nameAndNumber);
            } // If the contact doesn't exist yet, leave the number as-is
        }*/
        return text;
    }

    public String getCurrentCountryIso() {
        if (mCountryIso == null) {
            TelephonyManager tm = (TelephonyManager) CoreApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            mCountryIso = tm.getNetworkCountryIso();
            // Just in case the TelephonyManager method failed, fallback to US
            if (mCountryIso == null) {
                mCountryIso = "US";
            }
            mCountryIso = mCountryIso.toUpperCase();
        }
        return mCountryIso;
    }

}
