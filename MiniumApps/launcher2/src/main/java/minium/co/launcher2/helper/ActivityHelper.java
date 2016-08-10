package minium.co.launcher2.helper;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import minium.co.core.log.Tracer;


/**
 * Created by shahab on 3/17/16.
 */
public class ActivityHelper {

    private Context context;

    public ActivityHelper(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public boolean openContactsApp() {
        try {
            getContext().startActivity(new Intent().setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CONTACTS));
            return true;
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
        return false;
    }

    public boolean openMessagingApp() {
        try {
            getContext().startActivity(getContext().getPackageManager().getLaunchIntentForPackage("minium.co.messages"));
            return true;
        } catch (Exception e) {
            Tracer.e(e, "Minium-Messages app not found : " + e.getMessage());
        }

        try {
            getContext().startActivity(new Intent().setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MESSAGING));
            return true;
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }

        return false;
    }

    public boolean openNotesApp() {
        try {
            getContext().startActivity(getContext().getPackageManager().getLaunchIntentForPackage("minium.co.notes"));
            return true;
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
        return false;
    }

    public boolean openDialerApp() {
        try {
            getContext().startActivity(new Intent().setAction(Intent.ACTION_DIAL));
            return true;
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
        return false;
    }

    public boolean openCalculatorApp() {
        try {
            getContext().startActivity(new Intent().setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALCULATOR));
            return true;
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
        return false;
    }

    public boolean openSettingsApp() {
        try {
            getContext().startActivity(new Intent(Settings.ACTION_SETTINGS));
            return true;
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
        return false;
    }
}
