package co.siempo.phone.helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;

import java.util.Locale;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.applist.AppOpenEvent;
import co.siempo.phone.applist.AppOpenHandler;
import co.siempo.phone.inbox.GoogleInboxActivity_;
import co.siempo.phone.launcher.FakeLauncherActivity;
import co.siempo.phone.ui.UpdateActivity_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;
import minium.co.notes.ui.MainActivity;
import minium.co.settings.SiempoSettingsActivity_;


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
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI));
            return true;
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
        return false;
    }

    public void openMessagingApp() {
        try {
            getContext().startActivity(getContext().getPackageManager().getLaunchIntentForPackage("minium.co.messages"));
            return;
        } catch (Exception e) {
            Tracer.e(e, "Minium-Messages app not found : " + e.getMessage());
        }

        try {
            PackageManager pm = getContext().getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(Telephony.Sms.getDefaultSmsPackage(context));
            if (intent != null) {
                getContext().startActivity(intent);
            }

            new AppOpenHandler().handle(context, new AppOpenEvent(Telephony.Sms.getDefaultSmsPackage(context)));

            return;
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }

        UIUtils.alert(context, context.getString(R.string.msg_not_yet_implemented));
    }

    public boolean openNotesApp(boolean openLast) {
        try {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_OPEN_LATEST, openLast);
            getContext().startActivity(intent);
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
//            getContext().startActivity(new Intent(Settings.ACTION_SETTINGS));
            SiempoSettingsActivity_.intent(getContext()).start();
            return true;
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
        return false;
    }

    public void handleDefaultLauncher(CoreActivity activity) {
        if (isMyLauncherDefault(activity)) {
            Tracer.d("Launcher3 is the default launcher");
            activity.getPackageManager().clearPackagePreferredActivities(activity.getPackageName());
            openChooser(activity);
        } else {
            Tracer.d("Launcher3 is not the default launcher: " + getLauncherPackageName(activity));
            if (getLauncherPackageName(activity).equals("android")) {
                openChooser(activity);
            } else
                resetPreferredLauncherAndOpenChooser(activity);
//                openSettings();
        }
    }

    private boolean isMyLauncherDefault(CoreActivity activity) {
        return getLauncherPackageName(activity).equals(activity.getPackageName());
    }

    private String getLauncherPackageName(CoreActivity activity) {
        PackageManager localPackageManager = activity.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        String str = localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
        return str;
    }

    private void resetPreferredLauncherAndOpenChooser(CoreActivity activity) {
        PackageManager packageManager = activity.getPackageManager();
        ComponentName componentName = new ComponentName(activity, FakeLauncherActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        activity.startActivity(startMain);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
    }

    private void openChooser(CoreActivity activity) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(startMain);
    }

    public void openFeedback() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "feedback@siempo.co", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, String.format("Feedback on app [%s]", BuildConfig.VERSION_NAME));
            context.startActivity(emailIntent);
        } catch (Exception e) {
            UIUtils.alert(context, "No email application found in your phone");
        }
    }

    public void openGoogleInbox() {
        GoogleInboxActivity_.intent(context).start();
    }

    public void openEmail() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            context.startActivity(intent);
        } catch (Exception e) {
            UIUtils.alert(context, "No email application found in your phone");
        }
    }
    public void openGMape(String packageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.alert(context, "Application not found");
        }
    }

    public void openPlayStoreApp() {
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void openBecomeATester() {
        UpdateActivity_.intent(context).start();
    }

    public void openCallApp() {
        openGMape(Constants.CALL_APP_PACKAGE);
        new AppOpenHandler().handle(context, new AppOpenEvent(Constants.CALL_APP_PACKAGE));
    }

    public boolean isAppInstalled(String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Tracer.e(e);
        }

        return false;
    }
}
