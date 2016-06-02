package minium.co.core.app;

import android.graphics.Color;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Shahab on 6/2/2016.
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface DroidPrefs {

    @DefaultInt(Color.BLACK)
    int selectedThemeColor();

    @DefaultInt(0)
    int selectedThemeId();


}
