package com.moez.QKSMS.common.utils;

import android.content.Context;
import android.text.TextUtils;

import minium.co.messages.R;

/**
 * An utility class for managing messages.
 */
public class MessageUtils {

    private static String[] sNoSubjectStrings;

    private MessageUtils() {
        // Forbidden being instantiated.
    }

    /**
     * cleanseMmsSubject will take a subject that's says, "<Subject: no subject>", and return
     * a null string. Otherwise it will return the original subject string.
     *
     * @param context   a regular context so the function can grab string resources
     * @param subject   the raw subject
     * @param blacklist any extra strings to cleanse
     * @return
     */
    public static String cleanseMmsSubject(Context context, String subject, String... blacklist) {
        if (TextUtils.isEmpty(subject)) {
            return subject;
        }
        if (sNoSubjectStrings == null) {
            sNoSubjectStrings =
                    context.getResources().getStringArray(R.array.empty_subject_strings);

        }
        for (String string : sNoSubjectStrings) {
            if (subject.replaceAll("\\s+", "")
                    .equalsIgnoreCase(string.replaceAll("\\s+", ""))) {
                return null;
            }
        }

        for (String string : blacklist) {
            if (string != null) {
                if (subject.replaceAll("\\s+", "")
                        .equalsIgnoreCase(string.replaceAll("\\s+", ""))) {
                    return null;
                }
            }
        }

        return subject;
    }
}
