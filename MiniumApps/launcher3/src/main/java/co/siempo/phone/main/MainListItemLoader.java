package co.siempo.phone.main;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;

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
import co.siempo.phone.utils.Sorting;
import co.siempo.phone.utils.UIUtils;

/**
 * Created by Shahab on 5/4/2017.
 */
public class MainListItemLoader {

    private static final int TOOLS_MAP = 1;
    private static final int TOOLS_TRANSPORT = 2;
    private static final int TOOLS_CALENDAR = 3;
    private static final int TOOLS_WEATHER = 4;
    private static final int TOOLS_NOTES = 5;
    private static final int TOOLS_RECORDER = 6;
    private static final int TOOLS_CAMERA = 7;
    private static final int TOOLS_PHOTOS = 8;
    private static final int TOOLS_PAYMENT = 9;
    private static final int TOOLS_WELLNESS = 10;
    private static final int TOOLS_BROWSER = 11;
    private static final int TOOLS_CALL = 13;
    private static final int TOOLS_CLOCK = 14;
    private static final int TOOLS_MESSAGE = 15;
    private static final int TOOLS_EMAIL = 16;
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

        List<MainListItem> allAppsData= new ArrayList<>();
        ArrayList<MainListItem> toolsItems=new ArrayList<>();
        HashMap<Integer, AppMenu> toolsSettings = CoreApplication.getInstance().getToolsSettings
                ();
        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_MAP)
                .getApplicationName()) && toolsSettings.get(TOOLS_MAP)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_MAP, getString(R.string.title_map), R.drawable.ic_menu_map));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_TRANSPORT)
                .getApplicationName()) && toolsSettings.get(TOOLS_TRANSPORT)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_TRANSPORT, getString(R.string.title_transport), R.drawable.ic_menu_tranport));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_CALENDAR)
                .getApplicationName()) && toolsSettings.get(TOOLS_CALENDAR)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_CALENDAR, getString(R.string.title_calendar), R.drawable.ic_menu_calender));

        }
        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_WEATHER)
                .getApplicationName()) && toolsSettings.get(TOOLS_WEATHER)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_WEATHER, getString(R.string.title_weather), R.drawable.ic_menu_weather));
        }
        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_NOTES)
                .getApplicationName()) && toolsSettings.get(TOOLS_NOTES)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_NOTES, getString(R.string.title_note), R.drawable.ic_menu_notes, MainListItemType.ACTION));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_RECORDER)
                .getApplicationName()) && toolsSettings.get(TOOLS_RECORDER)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_RECORDER, getString(R.string.title_recorder), R.drawable.ic_menu_recorder));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_CAMERA)
                .getApplicationName()) && toolsSettings.get(TOOLS_CAMERA)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_CAMERA, getString(R.string.title_camera), R.drawable.ic_menu_camera));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_PHOTOS)
                .getApplicationName()) && toolsSettings.get(TOOLS_PHOTOS)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_PHOTOS, getString(R.string.title_photos), R.drawable.ic_menu_photos));
        }


        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_PAYMENT)
                .getApplicationName()) && toolsSettings.get(TOOLS_PAYMENT)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_PAYMENT, getString(R.string.title_payment), R.drawable.ic_menu_payment));
        }


        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_WELLNESS)
                .getApplicationName()) && toolsSettings.get(TOOLS_WELLNESS)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_WELLNESS, getString(R.string.title_wellness), R.drawable.ic_menu_wellness));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_BROWSER)
                .getApplicationName()) && toolsSettings.get(TOOLS_BROWSER)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_BROWSER, getString(R.string.title_browser), R.drawable.ic_menu_browser));
        }
        if (!TextUtils.isEmpty(toolsSettings.get(12)
                .getApplicationName())) {
            toolsItems.add(new MainListItem(12, "", 0));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_CALL)
                .getApplicationName()) && toolsSettings.get(TOOLS_CALL)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_CALL, getString(R.string.title_call), R.drawable.ic_menu_call, MainListItemType.ACTION));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_CLOCK)
                .getApplicationName()) && toolsSettings.get(TOOLS_CLOCK)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_CLOCK, getString(R.string.title_clock), R.drawable.ic_menu_clock));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_MESSAGE)
                .getApplicationName()) && toolsSettings.get(TOOLS_MESSAGE)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_MESSAGE, getString(R.string.title_messages), R.drawable.ic_menu_msg, MainListItemType.ACTION));
        }

        if (!TextUtils.isEmpty(toolsSettings.get(TOOLS_EMAIL)
                .getApplicationName()) && toolsSettings.get(TOOLS_EMAIL)
                .getApplicationName().contains(".")) {
            toolsItems.add(new MainListItem(TOOLS_EMAIL, getString(R.string.title_email), R.drawable.ic_menu_mail));
        }

        toolsItems=Sorting.sortToolAppAssignment(context,toolsItems);


        ArrayList<MainListItem> appItems=new ArrayList<>();

        if (fragment instanceof PaneFragment || fragment instanceof ToolsPaneFragment) {
            try {
                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> installedPackageList = context.getPackageManager().queryIntentActivities(mainIntent, 0);
                for (ResolveInfo resolveInfo : installedPackageList) {
                    if (!TextUtils.isEmpty(resolveInfo.activityInfo.packageName) && !TextUtils.isEmpty(resolveInfo.loadLabel(context.getPackageManager()))) {
                        String packageName=resolveInfo.activityInfo.packageName;
                        boolean isEnable=UIUtils.isAppInstalledAndEnabled(context, packageName);
                        if(isEnable) {
                            appItems.add(new MainListItem(-1, "" + resolveInfo.loadLabel(context.getPackageManager()), resolveInfo.activityInfo.packageName));
                        }
                    }
                }
            } catch (Exception e) {
                CoreApplication.getInstance().logException(e);
                e.printStackTrace();
            }
        }


        appItems= Sorting.SortApplications(appItems);



        items.addAll(toolsItems);
        items.addAll(appItems);
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
            default:
                UIUtils.alert(context, getString(R.string.msg_not_yet_implemented));
                break;
        }
        DashboardActivity.isTextLenghGreater = "";

    }


}
