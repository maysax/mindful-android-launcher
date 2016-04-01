package minium.co.messages.app;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

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
}
