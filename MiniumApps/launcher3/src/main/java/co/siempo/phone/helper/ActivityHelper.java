package co.siempo.phone.helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.Telephony;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.applist.AppOpenEvent;
import co.siempo.phone.applist.AppOpenHandler;
import co.siempo.phone.inbox.GoogleInboxActivity_;
import co.siempo.phone.launcher.FakeLauncherActivity;

import co.siempo.phone.settings.SiempoAlphaSettingsActivity_;
import co.siempo.phone.settings.SiempoMainSettingsActivity_;
import co.siempo.phone.settings.SiempoPhoneSettingsActivity_;
import co.siempo.phone.settings.SiempoSettingsActivity_;

import minium.co.core.app.CoreApplication;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;
import minium.co.notes.ui.MainActivity;


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
//        try {
//            getContext().startActivity(getContext().getPackageManager().getLaunchIntentForPackage("minium.co.messages"));
//            return;
//        } catch (Exception e) {
//            Tracer.e(e, "Minium-Messages app not found : " + e.getMessage());
//        }

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
            SiempoMainSettingsActivity_.intent(getContext()).start();
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

    public boolean isMyLauncherDefault(CoreActivity activity) {
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

    public void openChooser(CoreActivity activity) {
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
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {

        }
//        UpdateActivity_.intent(context).start();
    }

    /**
     * Open the Gmail application from device.
     */
    public void openGmail() {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(Constants.GOOGLE_GMAIL_PACKAGE);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            openMail();
        }
    }

    /**
     * Open the mail application from device.
     */
    public void openMail() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            context.startActivity(intent);
        } catch (Exception e) {
            UIUtils.alert(context, "No email application found in your phone");
        }
    }
    /**
     * Open the application with predefine package name.
     */
    public void openGMape(String packageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.alert(context, "Application not found");
        }
    }

    /**
     * Open the Dialer application from device.
     */
    public void openCallApp() {
        if (checkDialerApp().isEmpty()) {
            try {
                String phone = "";
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                        "tel", phone, null));
                context.startActivity(phoneIntent);
            } catch (Exception e) {
                e.printStackTrace();
                UIUtils.alert(context, "Application not found");
            }
        } else {
            openGMape(checkDialerApp().toString());
        }
    }

    /**
     * Open the Calender application from device.
     */
    public void openCalenderApp() {
        if (checkCalenderApp().isEmpty()) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("content://com.android.calendar/time/")));
        } else {
            openGMape(checkCalenderApp().toString());
        }
    }

    /**
     * Open the Clock application from device.
     */
    public void openClockApp() {
        if (checkClockApp().isEmpty()) {
            Intent mClockIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
            mClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mClockIntent);
        } else {
            openGMape(checkClockApp().toString());
        }
    }

    /**
     * Open the Photos application from device.
     */
    public void openPhotsApp() {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(Constants.GOOGLE_PHOTOS);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            openGallaryApp();
        }
    }

    /**
     * Open the Default Gallary application from device.
     */
    public void openGallaryApp() {
        try {
            Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
            mediaChooser.setType("video/*, image/*");
            context.startActivity(mediaChooser);
        } catch (Exception e) {
            e.printStackTrace();
            Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
            mediaChooser.setType("video/*, image/*");
            context.startActivity(mediaChooser);
            UIUtils.alert(context, "Application not found");
        }
    }

    /**
     * This method is used to check Dialer app installed or not,
     * If the application is available it retuns the package name.
     *
     * @return package name
     */
    private String checkDialerApp() {
        for (String strLocal : Constants.CALL_APP_PACKAGES) {
            for (ApplicationInfo packageInfo : CoreApplication.getInstance().getPackagesList()) {
                if (strLocal.equalsIgnoreCase(packageInfo.packageName)) {
                    return strLocal;
                }
            }
        }
        return "";
    }

    /**
     * This method is used to check Calender app installed or not,
     * If the application is available it retuns the package name.
     *
     * @return package name
     */
    private String checkCalenderApp() {
        for (String strLocal : Constants.CALENDAR_APP_PACKAGES) {
            for (ApplicationInfo packageInfo : CoreApplication.getInstance().getPackagesList()) {
                if (strLocal.equalsIgnoreCase(packageInfo.packageName)) {
                    return strLocal;
                }
            }
        }
        return "";
    }

    /**
     * This method is used to check Clock app installed or not,
     * If the application is available it retuns the package name.
     *
     * @return package name
     */
    private String checkClockApp() {
        for (String strLocal : Constants.CLOCK_APP_PACKAGES) {
            for (ApplicationInfo packageInfo : CoreApplication.getInstance().getPackagesList()) {
                if (strLocal.equalsIgnoreCase(packageInfo.packageName)) {
                    return strLocal;
                }
            }
        }
        return "";
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

    public boolean openPhoneSettingsApp() {
        try {
            SiempoPhoneSettingsActivity_.intent(getContext()).start();
            return true;
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
        return false;
    }

    public boolean openSiempoSettingsApp() {
        try {
            SiempoSettingsActivity_.intent(getContext()).start();
            return true;
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
        return false;
    }

    public boolean openSiempoAlphaSettingsApp() {
        try {
            SiempoAlphaSettingsActivity_.intent(getContext()).start();
            return true;
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
        return false;
    }
}
