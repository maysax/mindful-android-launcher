package com.moez.QKSMS.common.formatter;

import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.moez.QKSMS.data.Contact;

import minium.co.messages.app.MessagesApp_;

public class NumberToContactFormatter implements Formatter {
    String mCountryIso;

    @Override
    public String format(String text) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Iterable<PhoneNumberMatch> matches = phoneNumberUtil.findNumbers(text, getCurrentCountryIso());
        for (PhoneNumberMatch match : matches) {
            Contact contact = Contact.get(match.rawString(), true);
            if (contact.isNamed()) {
                String nameAndNumber = phoneNumberUtil.format(match.number(), PhoneNumberFormat.NATIONAL)
                        + " (" + contact.getName() + ")";
                text = text.replace(match.rawString(), nameAndNumber);
            } // If the contact doesn't exist yet, leave the number as-is
        }
        return text;
    }

    public String getCurrentCountryIso() {
        if (mCountryIso == null) {
            mCountryIso = MessagesApp_.getInstance().getCurrentCountryIso();
            // Just in case the TelephonyManager method failed, fallback to US
            if (mCountryIso == null) {
                mCountryIso = "US";
            }
            mCountryIso = mCountryIso.toUpperCase();
        }
        return mCountryIso;
    }

}
