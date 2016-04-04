package minium.co.messages.app;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.DefaultStringSet;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

import java.util.Set;

/**
 * Created by Shahab on 4/1/2016.
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface MessagesPref {

    /**
     * =================== MMS related configurations  ===================
     */
    @DefaultBoolean(false)
    boolean isMmsSetupDoNotAskAgain();

    @DefaultString("")
    String mmsURL();

    @DefaultString("")
    String mmsProxy();

    @DefaultString("")
    String mmsPort();

    @DefaultBoolean(false)
    boolean isUsing24HourTime();

    /**
     * =================== Conversation configurations  ===================
     */
    @DefaultStringSet({})
    Set<String> notificationDisabled();
}
