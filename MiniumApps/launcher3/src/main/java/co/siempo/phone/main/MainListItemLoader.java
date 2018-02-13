package co.siempo.phone.main;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.activities.DashboardActivity;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.fragments.PaneFragment;
import co.siempo.phone.fragments.ToolsPaneFragment;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.models.MainListItemType;
import co.siempo.phone.utils.UIUtils;

/**
 * Created by Shahab on 5/4/2017.
 */
public class MainListItemLoader {

    public static final int TOOLS_MAP = 1;
    public static final int TOOLS_TRANSPORT = 2;
    public static final int TOOLS_CALENDAR = 3;
    public static final int TOOLS_WEATHER = 4;
    public static final int TOOLS_NOTES = 5;
    public static final int TOOLS_RECORDER = 6;
    public static final int TOOLS_CAMERA = 7;
    public static final int TOOLS_PHOTOS = 8;
    public static final int TOOLS_PAYMENT = 9;
    public static final int TOOLS_WELLNESS = 10;
    public static final int TOOLS_BROWSER = 11;
    public static final int TOOLS_CALL = 13;
    public static final int TOOLS_CLOCK = 14;
    public static final int TOOLS_MESSAGE = 15;
    public static final int TOOLS_EMAIL = 16;
    private Context context;

    public MainListItemLoader(Context context) {
        this.context = context;
    }

    public void loadItemsDefaultApp(List<MainListItem> items) {
        items.add(new MainListItem(TOOLS_MAP, getString(R.string.title_map), R.drawable.ic_menu_map));
        items.add(new MainListItem(TOOLS_TRANSPORT, getString(R.string.title_transport), R.drawable.ic_menu_tranport));
        items.add(new MainListItem(TOOLS_CALENDAR, getString(R.string.title_calendar), R.drawable.ic_menu_calender));
        items.add(new MainListItem(TOOLS_WEATHER, getString(R.string.title_weather), R.drawable.ic_menu_weather));
        items.add(new MainListItem(TOOLS_NOTES, getString(R.string.title_note), R.drawable.ic_menu_notes, MainListItemType.ACTION));
        items.add(new MainListItem(TOOLS_RECORDER, getString(R.string.title_recorder), R.drawable.ic_menu_recorder));
        items.add(new MainListItem(TOOLS_CAMERA, getString(R.string.title_camera), R.drawable.ic_menu_camera));
        items.add(new MainListItem(TOOLS_PHOTOS, getString(R.string.title_photos), R.drawable.ic_menu_photos));
        items.add(new MainListItem(TOOLS_PAYMENT, getString(R.string.title_payment), R.drawable.ic_menu_payment));
        items.add(new MainListItem(TOOLS_WELLNESS, getString(R.string.title_wellness), R.drawable.ic_menu_wellness));
        items.add(new MainListItem(TOOLS_BROWSER, getString(R.string.title_browser), R.drawable.ic_menu_browser));
        items.add(new MainListItem(12, "", 0));
        items.add(new MainListItem(TOOLS_CALL, getString(R.string.title_call), R.drawable.ic_menu_call, MainListItemType.ACTION));
        items.add(new MainListItem(TOOLS_CLOCK, getString(R.string.title_clock), R.drawable.ic_menu_clock));
        items.add(new MainListItem(TOOLS_MESSAGE, getString(R.string.title_messages), R.drawable.ic_menu_msg, MainListItemType.ACTION));
        items.add(new MainListItem(TOOLS_EMAIL, getString(R.string.title_email), R.drawable.ic_menu_mail));
    }


    public void loadItems(List<MainListItem> items, Fragment fragment) {

        HashMap<Integer, AppMenu> toolsSettings = CoreApplication.getInstance().getToolsSettings
                ();





        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_MAP)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_MAP, getString(R.string.title_map), R.drawable.ic_menu_map));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_TRANSPORT)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_TRANSPORT, getString(R.string.title_transport), R.drawable.ic_menu_tranport));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_CALENDAR)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_CALENDAR, getString(R.string.title_calendar), R.drawable.ic_menu_calender));

        }
        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_WEATHER)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_WEATHER, getString(R.string.title_weather), R.drawable.ic_menu_weather));
        }
        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_NOTES)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_NOTES, getString(R.string.title_note), R.drawable.ic_menu_notes, MainListItemType.ACTION));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_RECORDER)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_RECORDER, getString(R.string.title_recorder), R.drawable.ic_menu_recorder));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_CAMERA)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_CAMERA, getString(R.string.title_camera), R.drawable.ic_menu_camera));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_PHOTOS)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_PHOTOS, getString(R.string.title_photos), R.drawable.ic_menu_photos));
        }


        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_PAYMENT)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_PAYMENT, getString(R.string.title_payment), R.drawable.ic_menu_payment));
        }


        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_WELLNESS)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_WELLNESS, getString(R.string.title_wellness), R.drawable.ic_menu_wellness));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_BROWSER)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_BROWSER, getString(R.string.title_browser), R.drawable.ic_menu_browser));
        }
        if (!TextUtils.isEmpty(toolsSettings.get(12)
                .getApplicationName())) {
            items.add(new MainListItem(12, "", 0));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_CALL)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_CALL, getString(R.string.title_call), R.drawable.ic_menu_call, MainListItemType.ACTION));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_CLOCK)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_CLOCK, getString(R.string.title_clock), R.drawable.ic_menu_clock));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_MESSAGE)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_MESSAGE, getString(R.string.title_messages), R.drawable.ic_menu_msg, MainListItemType.ACTION));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_EMAIL)
                .getApplicationName())) {
            items.add(new MainListItem(TOOLS_EMAIL, getString(R.string.title_email), R.drawable.ic_menu_mail));
        }


        if (fragment instanceof PaneFragment || fragment instanceof ToolsPaneFragment) {
            try {
                if (Launcher3App.getInstance().getPackagesList() != null && Launcher3App.getInstance().getPackagesList().size() > 0) {
                    ArrayList<String> appList = new ArrayList<>();
                    for (AppMenu appMenu : toolsSettings.values()) {
                        if (!appMenu.getApplicationName().equalsIgnoreCase("")) {
                            appList.add(appMenu.getApplicationName());
                        }
                    }
                    for (ApplicationInfo applicationInfo : Launcher3App.getInstance().getPackagesList()) {
                        String packageName = applicationInfo.packageName;
                        if (!appList.contains(packageName)) {
                            String appName = applicationInfo.name;
                            items.add(new MainListItem(-1, appName, applicationInfo));
                        }
                    }
                }
            } catch (Exception e) {
                CoreApplication.getInstance().logException(e);
                e.printStackTrace();
            }
        }
    }

    private String getString(@StringRes int resId, Object... formatArgs) {
        return context.getString(resId, formatArgs);
    }

    public void listItemClicked(int id) {
        switch (id) {
            case TOOLS_MAP://Map

                new ActivityHelper(context).openAppWithPackageName
                        (CoreApplication.getInstance().getToolsSettings().get
                                (TOOLS_MAP).getApplicationName().trim());
                break;
            case TOOLS_TRANSPORT://Transport
                break;
            case TOOLS_CALENDAR://Calender
                new ActivityHelper(context).openAppWithPackageName
                        (CoreApplication.getInstance().getToolsSettings().get
                                (TOOLS_CALENDAR).getApplicationName().trim());

                break;
            case TOOLS_WEATHER://Weather
                break;
            case TOOLS_NOTES:// Notes

                new ActivityHelper(context).openAppWithPackageName
                        (CoreApplication.getInstance().getToolsSettings().get
                                (TOOLS_NOTES).getApplicationName().trim());

                break;
            case TOOLS_RECORDER://Recorder
                break;
            case TOOLS_CAMERA:// Camera

                new ActivityHelper(context).openAppWithPackageName
                        (CoreApplication.getInstance().getToolsSettings().get
                                (TOOLS_CAMERA).getApplicationName().trim());
                break;
            case TOOLS_PHOTOS://Photos
                new ActivityHelper(context).openAppWithPackageName
                        (CoreApplication.getInstance().getToolsSettings().get
                                (TOOLS_PHOTOS).getApplicationName().trim());
                break;
            case TOOLS_PAYMENT://Payment
                break;
            case TOOLS_WELLNESS://Wellness
                break;
            case TOOLS_BROWSER:// Browser
                new ActivityHelper(context).openAppWithPackageName
                        (CoreApplication.getInstance().getToolsSettings().get
                                (TOOLS_BROWSER).getApplicationName().trim());
                break;

            case TOOLS_CALL:// Call

                new ActivityHelper(context).openAppWithPackageName
                        (CoreApplication.getInstance().getToolsSettings().get
                                (TOOLS_CALL).getApplicationName().trim());
//                if (context instanceof DashboardActivity) {
//                    if (!((DashboardActivity) context).prefs.isCallClicked().get()) {
//                        if (CoreApplication.getInstance().getCallPackageList().size() > 1) {
//                            ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 2, false);
//                        } else {
//                            ((DashboardActivity) context).prefs.isCallClickedFirstTime().put(true);
//                            new ActivityHelper(context).openAppWithPackageName(((DashboardActivity) context).prefs.callPackage().get());
//                        }
//                    } else {
//                        new ActivityHelper(context).openAppWithPackageName(((DashboardActivity) context).prefs.callPackage().get());
//                    }
//                } else if (context instanceof SiempoSettingsDefaultAppActivity) {
//                    if (CoreApplication.getInstance().getCallPackageList().size() > 1) {
//                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 2, true);
//                    } else {
//                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
//                    }
//                }
                break;
            case TOOLS_CLOCK://Clock

                new ActivityHelper(context).openAppWithPackageName
                        (CoreApplication.getInstance().getToolsSettings().get
                                (TOOLS_CLOCK).getApplicationName().trim());
                break;
            case TOOLS_MESSAGE:// Message
                new ActivityHelper(context).openAppWithPackageName
                        (CoreApplication.getInstance().getToolsSettings().get
                                (TOOLS_MESSAGE).getApplicationName().trim());


                break;
            case TOOLS_EMAIL:// Email

                new ActivityHelper(context).openAppWithPackageName
                        (CoreApplication.getInstance().getToolsSettings().get
                                (TOOLS_EMAIL).getApplicationName().trim());

                break;
            case 18:
                new ActivityHelper(context).openFeedback();
                break;
            case 19:
                new ActivityHelper(context).openInstallledApp();
                break;
            //            case 3:// Contact
//                if (context instanceof SiempoSettingsDefaultAppActivity) {
//                    if (CoreApplication.getInstance().getContactPackageList().size() > 1) {
//                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 3, true);
//                    } else {
//                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
////                    new ActivityHelper(context).openAppWithPackageName(((DashboardActivity) context).prefs.contactPackage().get());
//                    if (!((DashboardActivity) context).prefs.isContactClicked().get()) {
//                        if (CoreApplication.getInstance().getContactPackageList().size() > 1) {
//                            ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 3, false);
//                        } else {
//                            new ActivityHelper(context).openAppWithPackageName(((DashboardActivity) context).prefs.contactPackage().get());
//                        }
//                    } else {
//                        new ActivityHelper(context).openAppWithPackageName(((DashboardActivity) context).prefs.contactPackage().get());
//                    }
//                }
//                break;
//            case 12:
//                new ActivityHelper(context).handleDefaultLauncher((CoreActivity) context);
//                ((CoreActivity) context).loadDialog();
//                break;
//            case 15:
//                if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.alpha))) {
//                    ApiClient_.getInstance_(context).checkAppVersion(CheckVersionEvent.ALPHA);
//                } else if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.beta))) {
//                    ApiClient_.getInstance_(context).checkAppVersion(CheckVersionEvent.BETA);
//                }
//                break;
            default:
                UIUtils.alert(context, getString(R.string.msg_not_yet_implemented));
                break;
        }
        DashboardActivity.isTextLenghGreater = "";

    }

    public void firebaseEvent(int id) {
//        switch (id) {
//            case 1:// Message
//                FirebaseHelper.getIntance().logSiempoMenuUsage(context.getString(R.string.title_messages), 1);
//                break;
//            case 2:// Call
//                FirebaseHelper.getIntance().logSiempoMenuUsage(context.getString(R.string.title_call), 1);
//                break;
//            case 3:// Contact
//                FirebaseHelper.getIntance().logSiempoMenuUsage(context.getString(R.string.title_contacts), 1);
//                break;
//            case 6://Notes
//                FirebaseHelper.getIntance().logSiempoMenuUsage(context.getString(R.string.title_note), 1);
//                break;
//            case 11://Map
//                FirebaseHelper.getIntance().logSiempoMenuUsage(context.getString(R.string.title_map), 1);
//                break;
//            case 16:// Email
//                FirebaseHelper.getIntance().logSiempoMenuUsage(context.getString(R.string.title_email), 1);
//                break;
//            case 18://FeedBack
//                FirebaseHelper.getIntance().logSiempoMenuUsage(context.getString(R.string.title_feedback), 1);
//                break;
//            case 19://Apps
//                FirebaseHelper.getIntance().logSiempoMenuUsage(context.getString(R.string.title_apps), 1);
//                break;
//            case 20://Calender
//                FirebaseHelper.getIntance().logSiempoMenuUsage(context.getString(R.string.title_calendar), 1);
//                break;
//            case 21://Clock
//                FirebaseHelper.getIntance().logSiempoMenuUsage(context.getString(R.string.title_clock), 1);
//                break;
//            case 8://Photos
//                FirebaseHelper.getIntance().logSiempoMenuUsage(context.getString(R.string.title_photos), 1);
//                break;
//            case 23:// Camera
//                FirebaseHelper.getIntance().logSiempoMenuUsage(context.getString(R.string.title_camera), 1);
//                break;
//            case 24:// Browser
//                FirebaseHelper.getIntance().logSiempoMenuUsage(context.getString(R.string.title_browser), 1);
//                break;
//            default:
//                break;
//        }

    }
}
