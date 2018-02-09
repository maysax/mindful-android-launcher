package co.siempo.phone.main;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.StringRes;
import android.widget.Toast;

import java.util.List;

import co.siempo.phone.MainActivity;
import co.siempo.phone.R;
import co.siempo.phone.activities.SiempoSettingsDefaultAppActivity;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.fragments.PaneFragment;
import co.siempo.phone.fragments.ToolsPaneFragment;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.models.MainListItemType;
import co.siempo.phone.utils.UIUtils;

/**
 * Created by Shahab on 5/4/2017.
 */
public class MainListItemLoader {

    private Context context;

    public MainListItemLoader(Context context) {
        this.context = context;
    }

    public void loadItemsDefaultApp(List<MainListItem> items) {
        items.add(new MainListItem(1, getString(R.string.title_map), R.drawable.ic_menu_map));
        items.add(new MainListItem(2, getString(R.string.title_transport), R.drawable.ic_menu_tranport));
        items.add(new MainListItem(3, getString(R.string.title_calendar), R.drawable.ic_menu_calender));
        items.add(new MainListItem(4, getString(R.string.title_weather), R.drawable.ic_menu_weather));
        items.add(new MainListItem(5, getString(R.string.title_note), R.drawable.ic_menu_notes, MainListItemType.ACTION));
        items.add(new MainListItem(6, getString(R.string.title_recorder), R.drawable.ic_menu_recorder));
        items.add(new MainListItem(7, getString(R.string.title_camera), R.drawable.ic_menu_camera));
        items.add(new MainListItem(8, getString(R.string.title_photos), R.drawable.ic_menu_photos));
        items.add(new MainListItem(9, getString(R.string.title_payment), R.drawable.ic_menu_payment));
        items.add(new MainListItem(10, getString(R.string.title_wellness), R.drawable.ic_menu_wellness));
        items.add(new MainListItem(11, getString(R.string.title_browser), R.drawable.ic_menu_browser));
        items.add(new MainListItem(12, "", 0));
        items.add(new MainListItem(13, getString(R.string.title_call), R.drawable.ic_menu_call, MainListItemType.ACTION));
        items.add(new MainListItem(14, getString(R.string.title_clock), R.drawable.ic_menu_clock));
        items.add(new MainListItem(15, getString(R.string.title_messages), R.drawable.ic_menu_msg, MainListItemType.ACTION));
        items.add(new MainListItem(16, getString(R.string.title_email), R.drawable.ic_menu_mail));
    }


    public void loadItems(List<MainListItem> items, Fragment fragment) {
        items.add(new MainListItem(1, getString(R.string.title_map), R.drawable.ic_menu_map));
        items.add(new MainListItem(2, getString(R.string.title_transport), R.drawable.ic_menu_tranport));
        items.add(new MainListItem(3, getString(R.string.title_calendar), R.drawable.ic_menu_calender));
        items.add(new MainListItem(4, getString(R.string.title_weather), R.drawable.ic_menu_weather));
        items.add(new MainListItem(5, getString(R.string.title_note), R.drawable.ic_menu_notes, MainListItemType.ACTION));
        items.add(new MainListItem(6, getString(R.string.title_recorder), R.drawable.ic_menu_recorder));
        items.add(new MainListItem(7, getString(R.string.title_camera), R.drawable.ic_menu_camera));
        items.add(new MainListItem(8, getString(R.string.title_photos), R.drawable.ic_menu_photos));
        items.add(new MainListItem(9, getString(R.string.title_payment), R.drawable.ic_menu_payment));
        items.add(new MainListItem(10, getString(R.string.title_wellness), R.drawable.ic_menu_wellness));
        items.add(new MainListItem(11, getString(R.string.title_browser), R.drawable.ic_menu_browser));
        items.add(new MainListItem(12, "", 0));
        items.add(new MainListItem(13, getString(R.string.title_call), R.drawable.ic_menu_call, MainListItemType.ACTION));
        items.add(new MainListItem(14, getString(R.string.title_clock), R.drawable.ic_menu_clock));
        items.add(new MainListItem(15, getString(R.string.title_messages), R.drawable.ic_menu_msg, MainListItemType.ACTION));
        items.add(new MainListItem(16, getString(R.string.title_email), R.drawable.ic_menu_mail));

        if (fragment instanceof PaneFragment || fragment instanceof ToolsPaneFragment) {
            try {
                if (Launcher3App.getInstance().getPackagesList() != null && Launcher3App.getInstance().getPackagesList().size() > 0) {
                    for (ApplicationInfo applicationInfo : Launcher3App.getInstance().getPackagesList()) {
                        String packageName = applicationInfo.packageName;
                        if (!packageName.equalsIgnoreCase(((MainActivity) context).prefs.callPackage().get())
                                && !packageName.equalsIgnoreCase(((MainActivity) context).prefs.messagePackage().get())
                                && !packageName.equalsIgnoreCase(((MainActivity) context).prefs.calenderPackage().get())
                                && !packageName.equalsIgnoreCase(((MainActivity) context).prefs.contactPackage().get())
                                && !packageName.equalsIgnoreCase(((MainActivity) context).prefs.mapPackage().get())
                                && !packageName.equalsIgnoreCase(((MainActivity) context).prefs.photosPackage().get())
                                && !packageName.equalsIgnoreCase(((MainActivity) context).prefs.cameraPackage().get())
                                && !packageName.equalsIgnoreCase(((MainActivity) context).prefs.browserPackage().get())
                                && !packageName.equalsIgnoreCase(((MainActivity) context).prefs.clockPackage().get())
                                && !packageName.equalsIgnoreCase(((MainActivity) context).prefs.emailPackage().get())
                                && !packageName.equalsIgnoreCase(Constants.SETTINGS_APP_PACKAGE)) {
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
            case 1://Map
                if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getMapPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 11, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!((MainActivity) context).prefs.isMapClicked().get()) {
                        if (CoreApplication.getInstance().getMapPackageList().size() > 1) {
                            ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 11, false);
                        } else {
                            new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.mapPackage().get());
                        }
                    } else {
                        new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.mapPackage().get());
                    }
                }
                break;
            case 2://Transport
                break;
            case 3://Calender
                if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getCalenderPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 20, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!((MainActivity) context).prefs.isCalenderClicked().get()) {
                        if (CoreApplication.getInstance().getCalenderPackageList().size() > 1) {
                            ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 20, false);
                        } else {
                            new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.calenderPackage().get());
                        }
                    } else {
                        new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.calenderPackage().get());
                    }
                }
                break;
            case 4://Weather
                break;
            case 5:// Notes
                if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getNotesPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 6, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!((MainActivity) context).prefs.notesPackage().get().equalsIgnoreCase("Notes")) {
                        new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.notesPackage().get());
                    } else {
                        new ActivityHelper(context).openNotesApp(false);
                    }
                }
                break;
            case 6://Recorder
                break;
            case 7:// Camera
                if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getCameraPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 23, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!((MainActivity) context).prefs.isCameraClicked().get()) {
                        if (CoreApplication.getInstance().getCameraPackageList().size() > 1) {
                            ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 23, false);
                        } else {
                            new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.cameraPackage().get());
                        }
                    } else {
                        new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.cameraPackage().get());
                    }
                }
                break;
            case 8://Photos
                if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getPhotosPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 22, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!((MainActivity) context).prefs.isPhotosClicked().get()) {
                        if (CoreApplication.getInstance().getPhotosPackageList().size() > 1) {
                            ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 22, false);
                        } else {
                            new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.photosPackage().get());
                        }
                    } else {
                        new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.photosPackage().get());
                    }

                }
                break;
            case 9://Payment
                break;
            case 10://Wellness
                break;
            case 11:// Browser
                if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getBrowserPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 24, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!((MainActivity) context).prefs.isBrowserClicked().get()) {
                        if (CoreApplication.getInstance().getBrowserPackageList().size() > 1) {
                            ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 24, false);
                        } else {
                            new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.browserPackage().get());
                        }
                    } else {
                        new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.browserPackage().get());
                    }
                }
                break;

            case 13:// Call
                if (context instanceof MainActivity) {
                    if (!((MainActivity) context).prefs.isCallClicked().get()) {
                        if (CoreApplication.getInstance().getCallPackageList().size() > 1) {
                            ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 2, false);
                        } else {
                            ((MainActivity) context).prefs.isCallClickedFirstTime().put(true);
                            new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.callPackage().get());
                        }
                    } else {
                        new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.callPackage().get());
                    }
                } else if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getCallPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 2, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case 14://Clock
                if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getClockPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 21, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!((MainActivity) context).prefs.isClockClicked().get()) {
                        if (CoreApplication.getInstance().getClockPackageList().size() > 1) {
                            ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 21, false);
                        } else {
                            new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.clockPackage().get());
                        }
                    } else {
                        new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.clockPackage().get());
                    }
                }
                break;
            case 15:// Message
                if (context instanceof MainActivity) {
                    if (!((MainActivity) context).prefs.isMessageClicked().get()) {
                        if (CoreApplication.getInstance().getMessagePackageList().size() > 1) {
                            ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 1, false);
                        } else {
                            ((MainActivity) context).prefs.isMessageClickedFirstTime().put(true);
                            new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.messagePackage().get());
                        }
                    } else {
                        new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.messagePackage().get());
                    }
                } else if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getMessagePackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 1, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case 16:// Email
                if (context instanceof MainActivity) {
                    if (!((MainActivity) context).prefs.isEmailClicked().get()) {
                        if (CoreApplication.getInstance().getEmailPackageList().size() > 1) {
                            ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 16, false);
                        } else {
                            ((MainActivity) context).prefs.isEmailClickedFirstTime().put(true);
                            new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.emailPackage().get());
                        }
                    } else {
                        new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.emailPackage().get());
                    }
                } else if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getEmailPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 16, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                }
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
////                    new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.contactPackage().get());
//                    if (!((MainActivity) context).prefs.isContactClicked().get()) {
//                        if (CoreApplication.getInstance().getContactPackageList().size() > 1) {
//                            ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 3, false);
//                        } else {
//                            new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.contactPackage().get());
//                        }
//                    } else {
//                        new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.contactPackage().get());
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
        MainActivity.isTextLenghGreater = "";

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
