package co.siempo.phone.main;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.activities.DashboardActivity;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.fragments.PaneFragment;
import co.siempo.phone.fragments.ToolsPaneFragment;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.models.MainListItemType;
import co.siempo.phone.utils.Sorting;
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
    public static final int TOOLS_TODO = 17;
    public static final int TOOLS_PODCAST = 18;
    public static final int TOOLS_FOOD = 19;
    public static final int TOOLS_FITNESS = 20;
    public static final int TOOLS_MUSIC = 12;
    private Context context;

    public MainListItemLoader(Context context) {
        this.context = context;
    }

    public void loadItemsDefaultApp(List<MainListItem> items) {
        if (context != null) {
            items.add(new MainListItem(TOOLS_MAP, context.getResources()
                    .getString(R.string.title_map), R.drawable.ic_vector_map));
            items.add(new MainListItem(TOOLS_TRANSPORT, context.getResources
                    ().getString(R.string.title_transport), R.drawable
                    .ic_vector_transport));
            items.add(new MainListItem(TOOLS_CALENDAR, context.getResources()
                    .getString(R.string.title_calendar), R.drawable.ic_vector_calendar));
            items.add(new MainListItem(TOOLS_WEATHER, context.getResources()
                    .getString(R.string.title_weather), R.drawable.ic_vector_cloud));
            items.add(new MainListItem(TOOLS_NOTES, context.getResources()
                    .getString(R.string.title_note), R.drawable.ic_vector_note,
                    MainListItemType.ACTION));
            items.add(new MainListItem(TOOLS_RECORDER, context.getResources()
                    .getString(R.string.title_recorder), R.drawable
                    .ic_vector_recorder));

//            items.add(new MainListItem(TOOLS_TODO, context.getResources()
//                    .getString(R.string.title_recorder), R.drawable
//                    .ic_vector_todo));
            items.add(new MainListItem(TOOLS_CAMERA, context.getResources()
                    .getString(R.string.title_camera), R.drawable.ic_vector_camera));
            items.add(new MainListItem(TOOLS_PHOTOS, context.getResources()
                    .getString(R.string.title_photos), R.drawable.ic_vector_photo));
            items.add(new MainListItem(TOOLS_PAYMENT, context.getResources()
                    .getString(R.string.title_payment), R.drawable
                    .ic_vector_payment));
            items.add(new MainListItem(TOOLS_WELLNESS, context.getResources()
                    .getString(R.string.title_wellness), R.drawable
                    .ic_vector_wellness));
            items.add(new MainListItem(TOOLS_BROWSER, context.getResources()
                    .getString(R.string.title_browser), R.drawable
                    .ic_vector_browser));
            items.add(new MainListItem(12, "", 0));
            items.add(new MainListItem(TOOLS_CALL, context.getResources()
                    .getString(R.string.title_call), R.drawable.ic_vector_call,
                    MainListItemType.ACTION));
            items.add(new MainListItem(TOOLS_CLOCK, context.getResources()
                    .getString(R.string.title_clock), R.drawable
                    .ic_vector_clock));
            items.add(new MainListItem(TOOLS_MESSAGE, context.getResources()
                    .getString(R.string.title_messages), R.drawable
                    .ic_vector_messages, MainListItemType.ACTION));
            items.add(new MainListItem(TOOLS_EMAIL, context.getResources()
                    .getString(R.string.title_email), R.drawable.ic_vector_email));
        }
    }


    public void loadItems(List<MainListItem> items, Fragment fragment) {
        if (context != null) {
            List<MainListItem> allAppsData = new ArrayList<>();
            ArrayList<MainListItem> toolsItems = new ArrayList<>();
            HashMap<Integer, AppMenu> toolsSettings = CoreApplication.getInstance().getToolsSettings
                    ();
            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_MAP)
                    .getApplicationName()) && toolsSettings.get(TOOLS_MAP)
                    .getApplicationName().contains(".")) {
                toolsItems.add(new MainListItem(TOOLS_MAP, context.getResources().getString(R.string.title_map), R.drawable.ic_menu_map));
            }

            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_TRANSPORT)
                    .getApplicationName()) && toolsSettings.get(TOOLS_TRANSPORT)
                    .getApplicationName().contains(".")) {
                toolsItems.add(new MainListItem(TOOLS_TRANSPORT, context
                        .getResources().getString(R.string.title_transport),
                        R.drawable.ic_vector_transport));
            }

            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_CALENDAR)
                    .getApplicationName()) && toolsSettings.get(TOOLS_CALENDAR)
                    .getApplicationName().contains(".")) {
                toolsItems.add(new MainListItem(TOOLS_CALENDAR, context
                        .getResources().getString(R.string.title_calendar), R
                        .drawable.ic_vector_calendar));

            }
            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_WEATHER)
                    .getApplicationName()) && toolsSettings.get(TOOLS_WEATHER)
                    .getApplicationName().contains(".")) {
                toolsItems.add(new MainListItem(TOOLS_WEATHER, context
                        .getResources().getString(R.string.title_weather), R
                        .drawable.ic_vector_cloud));
            }

            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_NOTES)
                    .getApplicationName())) {
                toolsItems.add(new MainListItem(TOOLS_NOTES, context.getResources().getString(R.string.title_note), R.drawable.ic_vector_note, MainListItemType.ACTION));
            }

            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_RECORDER)
                    .getApplicationName()) && toolsSettings.get(TOOLS_RECORDER)
                    .getApplicationName().contains(".")) {
                toolsItems.add(new MainListItem(TOOLS_RECORDER, context.getResources().getString(R.string.title_recorder), R.drawable.ic_vector_recorder));
            }

//            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_TODO)
//                    .getApplicationName()) && toolsSettings.get(TOOLS_TODO)
//                    .getApplicationName().contains(".")) {
//                toolsItems.add(new MainListItem(TOOLS_TODO, context
//                        .getResources().getString(R.string.title_todo), R
//                        .drawable.ic_vector_todo));
//            }

            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_CAMERA)
                    .getApplicationName()) && toolsSettings.get(TOOLS_CAMERA)
                    .getApplicationName().contains(".")) {
                toolsItems.add(new MainListItem(TOOLS_CAMERA, context.getResources().getString(R.string.title_camera), R.drawable.ic_vector_camera));
            }

            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_PHOTOS)
                    .getApplicationName()) && toolsSettings.get(TOOLS_PHOTOS)
                    .getApplicationName().contains(".")) {
                toolsItems.add(new MainListItem(TOOLS_PHOTOS, context.getResources().getString(R.string.title_photos), R.drawable.ic_vector_photo));
            }


            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_PAYMENT)
                    .getApplicationName()) && toolsSettings.get(TOOLS_PAYMENT)
                    .getApplicationName().contains(".")) {
                toolsItems.add(new MainListItem(TOOLS_PAYMENT, context.getResources().getString(R.string.title_payment), R.drawable.ic_vector_payment));
            }


            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_WELLNESS)
                    .getApplicationName()) && toolsSettings.get(TOOLS_WELLNESS)
                    .getApplicationName().contains(".")) {
                toolsItems.add(new MainListItem(TOOLS_WELLNESS, context.getResources().getString(R.string.title_wellness), R.drawable.ic_vector_wellness));
            }

            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_BROWSER)
                    .getApplicationName()) && toolsSettings.get(TOOLS_BROWSER)
                    .getApplicationName().contains(".")) {
                toolsItems.add(new MainListItem(TOOLS_BROWSER, context.getResources().getString(R.string.title_browser), R.drawable.ic_vector_browser));
            }
            if (!TextUtils.isEmpty(toolsSettings.get(12)
                    .getApplicationName())) {
                toolsItems.add(new MainListItem(12, "", 0));
            }

            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_CALL)
                    .getApplicationName()) && toolsSettings.get(TOOLS_CALL)
                    .getApplicationName().contains(".")) {
                toolsItems.add(new MainListItem(TOOLS_CALL, context.getResources().getString(R.string.title_call), R.drawable.ic_vector_call, MainListItemType.ACTION));
            }

            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_CLOCK)
                    .getApplicationName()) && toolsSettings.get(TOOLS_CLOCK)
                    .getApplicationName().contains(".")) {
                toolsItems.add(new MainListItem(TOOLS_CLOCK, context.getResources().getString(R.string.title_clock), R.drawable.ic_vector_clock));
            }

            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_MESSAGE)
                    .getApplicationName()) && toolsSettings.get(TOOLS_MESSAGE)
                    .getApplicationName().contains(".")) {
                toolsItems.add(new MainListItem(TOOLS_MESSAGE, context
                        .getResources().getString(R.string.title_messages), R
                        .drawable.ic_vector_messages, MainListItemType.ACTION));
            }

            if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_EMAIL)
                    .getApplicationName()) && toolsSettings.get(TOOLS_EMAIL)
                    .getApplicationName().contains(".")) {
                toolsItems.add(new MainListItem(TOOLS_EMAIL, context
                        .getResources().getString(R.string.title_email), R.drawable.ic_vector_email));
            }

            toolsItems = Sorting.sortToolAppAssignment(context, toolsItems);


            ArrayList<MainListItem> appItems = new ArrayList<>();

            if (fragment instanceof PaneFragment || fragment instanceof ToolsPaneFragment) {
                try {
                    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    List<ResolveInfo> installedPackageList = context.getPackageManager().queryIntentActivities(mainIntent, 0);
                    for (ResolveInfo resolveInfo : installedPackageList) {
                        if (!TextUtils.isEmpty(resolveInfo.activityInfo.packageName) && !TextUtils.isEmpty(resolveInfo.loadLabel(context.getPackageManager()))) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            boolean isEnable = UIUtils.isAppInstalledAndEnabled(context, packageName);
                            if (isEnable && !packageName.equalsIgnoreCase(context.getPackageName())) {
                                appItems.add(new MainListItem(-1, "" + resolveInfo.loadLabel(context.getPackageManager()), resolveInfo.activityInfo.packageName));
                            }
                        }
                    }

                } catch (Exception e) {
                    CoreApplication.getInstance().logException(e);
                    e.printStackTrace();
                }
            }


            appItems = Sorting.SortApplications(appItems);

            try {
                items.addAll(toolsItems);
                items.addAll(appItems);
            } catch (Exception ae) {
                ae.printStackTrace();
            }
        }
    }


    public void listItemClicked(int id) {
        String packageName, applicationName;
        if (context != null) {
            switch (id) {
                case TOOLS_MAP://Map
                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_MAP).getApplicationName().trim();
                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_map), applicationName);
                    new ActivityHelper(context).openAppWithPackageName
                            (packageName);
                    break;
                case TOOLS_TRANSPORT://Transport
                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_TRANSPORT).getApplicationName().trim();
                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_transport), applicationName);
                    new ActivityHelper(context).openAppWithPackageName
                            (packageName);
                    break;
                case TOOLS_CALENDAR://Calender
                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_CALENDAR).getApplicationName().trim();
                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_calendar), applicationName);
                    new ActivityHelper(context).openAppWithPackageName
                            (packageName);
                    break;
                case TOOLS_WEATHER://Weather
                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_WEATHER).getApplicationName().trim();
                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_weather), applicationName);
                    new ActivityHelper(context).openAppWithPackageName
                            (packageName);
                    break;
                case TOOLS_NOTES:// Notes
                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_NOTES).getApplicationName().trim();
                    if (packageName.equalsIgnoreCase("Notes")) {
                        FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_note), context.getResources().getString(R.string.title_note));
                        new ActivityHelper(context).openNotesApp(false);
                    } else {
                        applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                        FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_note), applicationName);
                        new ActivityHelper(context).openAppWithPackageName
                                (packageName);
                    }
                    break;
                case TOOLS_RECORDER://Recorder
                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_RECORDER).getApplicationName().trim();
                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_recorder), applicationName);
                    new ActivityHelper(context).openAppWithPackageName
                            (packageName);
                    break;

//                    case TOOLS_TODO://TODO
//                    packageName = CoreApplication.getInstance().getToolsSettings().get
//                            (TOOLS_TODO).getApplicationName().trim();
//                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
//                    FirebaseHelper.getInstance().logSiempoMenuUsage(3,
//                            context.getResources().getString(R.string
//                                    .title_todo), applicationName);
//                    new ActivityHelper(context).openAppWithPackageName
//                            (packageName);
//                    break;
                case TOOLS_CAMERA:// Camera
                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_CAMERA).getApplicationName().trim();
                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_camera), applicationName);
                    new ActivityHelper(context).openAppWithPackageName
                            (packageName);
                    break;
                case TOOLS_PHOTOS://Photos
                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_PHOTOS).getApplicationName().trim();
                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_photos), applicationName);
                    new ActivityHelper(context).openAppWithPackageName
                            (packageName);

                    break;
                case TOOLS_PAYMENT://Payment
                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_PAYMENT).getApplicationName().trim();
                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_payment), applicationName);
                    new ActivityHelper(context).openAppWithPackageName
                            (packageName);
                    break;
                case TOOLS_WELLNESS://Wellness
                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_WELLNESS).getApplicationName().trim();
                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_wellness), applicationName);
                    new ActivityHelper(context).openAppWithPackageName
                            (packageName);
                    break;
                case TOOLS_BROWSER:// Browser
                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_BROWSER).getApplicationName().trim();
                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_browser), applicationName);
                    new ActivityHelper(context).openAppWithPackageName
                            (packageName);
                    break;
                case TOOLS_CALL:// Call
                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_CALL).getApplicationName().trim();
                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_call), applicationName);
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setPackage(packageName);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        new ActivityHelper(context).openAppWithPackageName
                                (packageName);
                    }
                    break;
                case TOOLS_CLOCK://Clock
                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_CLOCK).getApplicationName().trim();
                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_clock), applicationName);
                    new ActivityHelper(context).openAppWithPackageName
                            (packageName);
                    break;
                case TOOLS_MESSAGE:// Message
                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_MESSAGE).getApplicationName().trim();
                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_messages), applicationName);
                    new ActivityHelper(context).openAppWithPackageName
                            (packageName);

                    break;
                case TOOLS_EMAIL:// Email

                    packageName = CoreApplication.getInstance().getToolsSettings().get
                            (TOOLS_EMAIL).getApplicationName().trim();
                    applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_email), applicationName);
                    new ActivityHelper(context).openAppWithPackageName
                            (packageName);

                    break;
                case 18:
                    FirebaseHelper.getInstance().logSiempoMenuUsage(3, context.getResources().getString(R.string.title_feedback), context.getResources().getString(R.string.title_feedback));
                    new ActivityHelper(context).openFeedback();
                    break;
                default:
                    UIUtils.alert(context, context.getResources().getString(R.string.msg_not_yet_implemented));
                    break;
            }
            DashboardActivity.isTextLenghGreater = "";

        }
    }

}
