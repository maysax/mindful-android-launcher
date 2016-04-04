package com.moez.QKSMS.common;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Set;

import minium.co.messages.app.MessagesPref_;

/**
 * Created by Shahab on 4/4/2016.
 */
@EBean
public class ConversationPrefsHelper {

    @Pref
    MessagesPref_ prefs;

    public boolean getNotificationsEnabled(long threadId) {
        Set<String> disabledThreads = prefs.notificationDisabled().get();
        return !disabledThreads.contains(String.format("%s", threadId));
    }

}
