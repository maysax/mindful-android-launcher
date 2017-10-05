package co.siempo.phone.main;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.support.annotation.StringRes;

import java.util.Arrays;
import java.util.List;

import co.siempo.phone.MainActivity;
import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.applist.AppDrawerActivity_;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.mm.MMTimePickerActivity_;
import co.siempo.phone.mm.MindfulMorningActivity_;
import co.siempo.phone.model.MainListItem;
import co.siempo.phone.model.MainListItemType;
import co.siempo.phone.pause.PauseActivity_;
import co.siempo.phone.service.ApiClient_;
import co.siempo.phone.tempo.TempoActivity_;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

/**
 * Created by Shahab on 5/4/2017.
 */


public class MainListItemLoader {

    private Context context;
    Fragment fragment;
    public MainListItemLoader(Context context) {
        this.context = context;
    }

    public void loadItems(List<MainListItem> items, Fragment fragment) {
        items.add(new MainListItem(2, context.getString(R.string.title_calls), "fa-phone", R.drawable.icon_call, MainListItemType.ACTION));
        items.add(new MainListItem(1, getString(R.string.title_messages), "fa-users", R.drawable.icon_sms, MainListItemType.ACTION));
        items.add(new MainListItem(20, getString(R.string.title_calendar), "fa-calendar"));
        items.add(new MainListItem(3, getString(R.string.title_contacts), "fa-user", R.drawable.icon_create_user, MainListItemType.ACTION));
        items.add(new MainListItem(11, getString(R.string.title_map), "fa-street-view"));
        items.add(new MainListItem(6, getString(R.string.title_notes), "fa-sticky-note", R.drawable.icon_save_note, MainListItemType.ACTION));

        //if (new ActivityHelper(context).isAppInstalled(GOOGLE_PHOTOS))
        items.add(new MainListItem(22, getString(R.string.title_photos), "fa-picture-o"));
        items.add(new MainListItem(23, getString(R.string.title_camera), "fa-camera"));

        items.add(new MainListItem(21, getString(R.string.title_clock), "fa-clock-o"));
        items.add(new MainListItem(8, getString(R.string.title_settings), "fa-cogs", R.drawable.icon_settings, MainListItemType.ACTION));
        items.add(new MainListItem(4, getString(R.string.title_pause), "fa-ban"));
        items.add(new MainListItem(10, getString(R.string.title_tempo), "fa-bell", R.drawable.icon_tempo, MainListItemType.ACTION));
        items.add(new MainListItem(16, getString(R.string.title_email), "fa-envelope"));
        items.add(new MainListItem(19, getString(R.string.title_apps), "fa-list"));

//        items.add(new MainListItem(5, getString(R.string.title_voicemail), "fa-microphone"));
//        items.add(new MainListItem(7, getString(R.string.title_clock), "fa-clock-o"));
//        items.add(new MainListItem(9, getString(R.string.title_theme), "fa-tint"));
        //items.add(new MainListItem(17, getString(R.string.title_inbox), "fa-inbox"));

        /**
         * SSA-101 :  Comment "Switch home Launcher" & "Version" module.
         */

//        if (!Build.MODEL.toLowerCase().contains("siempo")) {
//            items.add(new MainListItem(12, getString(title_defaultLauncher), "fa-certificate"));
//        }
        items.add(new MainListItem(18, getString(R.string.title_feedback), "fa-question-circle"));
        // items.add(new MainListItem(13, getString(R.string.title_mindfulMorning), "fa-coffee"));
        //items.add(new MainListItem(14, getString(R.string.title_mindfulMorningAlarm), "fa-coffee"));
//        items.add(new MainListItem(15, getString(R.string.title_version, BuildConfig.VERSION_NAME), "fa-info-circle"));
        if (fragment instanceof MainFragment) {
            try {
                if (Launcher3App.getInstance().getPackagesList() != null && Launcher3App.getInstance().getPackagesList().size() > 0) {
                    for (ApplicationInfo applicationInfo : Launcher3App.getInstance().getPackagesList()) {
                        String defDialerApp = Settings.Secure.getString(context.getContentResolver(), "dialer_default_application");
                        String defSMSApp = Settings.Secure.getString(context.getContentResolver(), "sms_default_application");
                        String packageName = applicationInfo.packageName;
                        if (!packageName.equalsIgnoreCase(defDialerApp)
                                && !packageName.equalsIgnoreCase(defSMSApp)
                                && !packageName.equalsIgnoreCase(Constants.SETTINGS_APP_PACKAGE)
                                && !packageName.equalsIgnoreCase(Constants.CALL_APP_PACKAGE)
                                && !packageName.equalsIgnoreCase(Constants.CONTACT_APP_PACKAGE)
                                && !packageName.equalsIgnoreCase(Constants.GOOGLE_GMAIL_PACKAGE)
                                && !packageName.equalsIgnoreCase(Constants.GOOGLE_MAP_PACKAGE)
                                && !packageName.equalsIgnoreCase(Constants.GOOGLE_PHOTOS)
                                && !applicationInfo.name.equalsIgnoreCase("Camera")
                                && (!packageName.equalsIgnoreCase(Constants.GOOGLE_CAMERA))
                                && !Arrays.asList(Constants.CALENDAR_APP_PACKAGES).contains(packageName)
                                && !Arrays.asList(Constants.CALL_APP_PACKAGES).contains(packageName)
                                && !Arrays.asList(Constants.CLOCK_APP_PACKAGES).contains(packageName)) {
                            String appName = applicationInfo.name;
                            items.add(new MainListItem(-1, appName, applicationInfo));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getPackageName();
    }

    // get all default application package name
    private String getPackageName() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo res : listCam) {
//            Log.e("Camera Application Package Name and Activity Name",res.activityInfo.packageName + " " + res.activityInfo.name);
        }
        return "";
    }


    private final String getString(@StringRes int resId, Object... formatArgs) {
        return context.getString(resId, formatArgs);
    }

    public void listItemClicked(int id) {
        MainActivity mainActivity = (MainActivity)context;
        switch (id) {
            case 1:
                /**
                 *  Load native status bar
                 */
                if(mainActivity!=null) {
                    mainActivity.restoreSiempoNotificationBar();
                }
                new ActivityHelper(context).openMessagingApp();
                break;
            case 2:
                /**
                 *  Load native status bar
                 */
                if(mainActivity!=null) {
                    mainActivity.restoreSiempoNotificationBar();
                }
                new ActivityHelper(context).openCallApp();
                break;
            case 3:
                /**
                 *  Load native status bar
                 */
                if(mainActivity!=null) {
                    mainActivity.restoreSiempoNotificationBar();
                }
                new ActivityHelper(context).openContactsApp();
                break;
            case 4:
                PauseActivity_.intent(context).start();
                break;
            case 5:
                UIUtils.alert(context, getString(R.string.msg_not_yet_implemented));
                break;
            case 6:
                /**
                 *  Load native status bar
                 */
                if(mainActivity!=null) {
                    mainActivity.restoreSiempoNotificationBar();
                }
                new ActivityHelper(context).openNotesApp(false);
                break;
            case 7:
                UIUtils.alert(context, getString(R.string.msg_not_yet_implemented));
                break;
            case 8:
                new ActivityHelper(context).openSettingsApp();
                break;
            case 9:
                UIUtils.alert(context, getString(R.string.msg_not_yet_implemented));
                break;
            case 10:
                TempoActivity_.intent(context).start();
                break;
            case 11:
                /**
                 *  Load native status bar
                 */
                if(mainActivity!=null) {
                    mainActivity.restoreSiempoNotificationBar();
                }
                new ActivityHelper(context).openGMape(Constants.GOOGLE_MAP_PACKAGE);
                break;
            case 12:
                new ActivityHelper(context).handleDefaultLauncher((CoreActivity) context);
                ((CoreActivity) context).loadDialog();
                break;
            case 13:
                MMTimePickerActivity_.intent(context).start();
                break;
            case 14:
                MindfulMorningActivity_.intent(context).start();
                break;
            case 15:
                ApiClient_.getInstance_(context).checkAppVersion();
                break;
            case 16:
                /**
                 *  Load native status bar
                 */
                if(mainActivity!=null) {
                    mainActivity.restoreSiempoNotificationBar();
                }
                new ActivityHelper(context).openGmail();
                break;
            case 17: //new ActivityHelper(context).openGoogleInbox(); break;
            case 18:
                /**
                 *  Load native status bar
                 */
                if(mainActivity!=null) {
                    mainActivity.restoreSiempoNotificationBar();
                }
                new ActivityHelper(context).openFeedback();
                break;
            case 19:
                AppDrawerActivity_.intent(context).start();
                break;
            case 20:
                /**
                 *  Load native status bar
                 */
                if(mainActivity!=null) {
                    mainActivity.restoreSiempoNotificationBar();
                }
                new ActivityHelper(context).openCalenderApp();
                break;
            case 21:
                /**
                 *  Load native status bar
                 */
                if(mainActivity!=null) {
                    mainActivity.restoreSiempoNotificationBar();
                }
                new ActivityHelper(context).openClockApp();
                break;
            case 22:
                /**
                 *  Load native status bar
                 */
                if(mainActivity!=null) {
                    mainActivity.restoreSiempoNotificationBar();
                }
                new ActivityHelper(context).openPhotosApp();
                break;
            case 23:
                /**
                 *  Load native status bar
                 */
                if (mainActivity != null) {
                    mainActivity.restoreSiempoNotificationBar();
                }
                new ActivityHelper(context).openCameraApp();
                break;
            default:
                UIUtils.alert(context, getString(R.string.msg_not_yet_implemented));
                break;
        }
        MainActivity.isTextLenghGreater = "";

    }
}
