package co.siempo.phone.main;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.StringRes;
import android.widget.Toast;

import java.util.List;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.MainActivity;
import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.applist.AppDrawerActivity_;
import co.siempo.phone.event.SendSmsEvent;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.mm.MMTimePickerActivity_;
import co.siempo.phone.mm.MindfulMorningActivity_;
import co.siempo.phone.model.MainListItem;
import co.siempo.phone.model.MainListItemType;
import co.siempo.phone.pause.PauseActivity_;
import co.siempo.phone.service.ApiClient_;
import co.siempo.phone.settings.SiempoSettingsDefaultAppActivity;
import co.siempo.phone.tempo.TempoActivity_;
import de.greenrobot.event.EventBus;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.CheckVersionEvent;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

/**
 * Created by Shahab on 5/4/2017.
 */
public class MainListItemLoader {

    private Context context;

    public MainListItemLoader(Context context) {
        this.context = context;
    }

    public void loadItemsDefaultApp(List<MainListItem> items) {
        items.add(new MainListItem(2, getString(R.string.title_calls), "fa-phone", R.drawable.icon_call, MainListItemType.ACTION));
        items.add(new MainListItem(1, getString(R.string.title_messages), "fa-users", R.drawable.icon_sms, MainListItemType.ACTION));
        items.add(new MainListItem(20, getString(R.string.title_calendar), "fa-calendar"));
        items.add(new MainListItem(3, getString(R.string.title_contacts), "fa-user", R.drawable.icon_create_user, MainListItemType.ACTION));
        items.add(new MainListItem(11, getString(R.string.title_map), "fa-street-view"));
        items.add(new MainListItem(22, getString(R.string.title_photos), "fa-picture-o"));
        items.add(new MainListItem(23, getString(R.string.title_camera), "fa-camera"));
        items.add(new MainListItem(24, getString(R.string.title_browser), "fa-hand-pointer-o"));
        items.add(new MainListItem(21, getString(R.string.title_clock), "fa-clock-o"));
        items.add(new MainListItem(16, getString(R.string.title_email), "fa-envelope"));
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
        items.add(new MainListItem(24, getString(R.string.title_browser), "fa-hand-pointer-o"));

        items.add(new MainListItem(21, getString(R.string.title_clock), "fa-clock-o"));
        if (fragment instanceof MainFragment) {
            items.add(new MainListItem(8, getString(R.string.title_settings), "fa-cogs", R.drawable.icon_settings, MainListItemType.ACTION));
        }
        // items.add(new MainListItem(4, getString(R.string.title_pause), "fa-ban"));
        //items.add(new MainListItem(10, getString(R.string.title_tempo), "fa-bell", R.drawable.icon_tempo, MainListItemType.ACTION));
        items.add(new MainListItem(16, getString(R.string.title_email), "fa-envelope"));
        items.add(new MainListItem(19, getString(R.string.title_apps), "fa-list"));

//        items.add(new MainListItem(5, getString(R.string.title_voicemail), "fa-microphone"));
//        items.add(new MainListItem(7, getString(R.string.title_clock), "fa-clock-o"));
//        items.add(new MainListItem(9, getString(R.string.title_theme), "fa-tint"));
        //items.add(new MainListItem(17, getString(R.string.title_inbox), "fa-inbox"));

//        if (!Build.MODEL.toLowerCase().contains("siempo")) {
//            items.add(new MainListItem(12, getString(title_defaultLauncher), "fa-certificate"));
//        }
//        items.add(new MainListItem(18, getString(R.string.title_feedback), "fa-question-circle"));
        // items.add(new MainListItem(13, getString(R.string.title_mindfulMorning), "fa-coffee"));
        //items.add(new MainListItem(14, getString(R.string.title_mindfulMorningAlarm), "fa-coffee"));
//        items.add(new MainListItem(15, getString(R.string.title_version, BuildConfig.VERSION_NAME), "fa-info-circle"));
        if (fragment instanceof MainFragment) {
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
                e.printStackTrace();
            }
        }
    }

    private String getString(@StringRes int resId, Object... formatArgs) {
        return context.getString(resId, formatArgs);
    }

    public void listItemClicked(int id) {
        switch (id) {
            case 1:// Message
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
            case 2:// Call
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
            case 3:// Contact
                if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getContactPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 3, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.contactPackage().get());
                }
                break;
            case 4:
                PauseActivity_.intent(context).start();
                break;
            case 5:
                UIUtils.alert(context, getString(R.string.msg_not_yet_implemented));
                break;
            case 6:
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
            case 11://Map
                if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getMapPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 11, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.mapPackage().get());
                }
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
                if (BuildConfig.FLAVOR.equalsIgnoreCase("alpha")) {
                    ApiClient_.getInstance_(context).checkAppVersion(CheckVersionEvent.ALPHA);
                } else if (BuildConfig.FLAVOR.equalsIgnoreCase("beta")) {
                    ApiClient_.getInstance_(context).checkAppVersion(CheckVersionEvent.BETA);
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
            case 17: //new ActivityHelper(context).openGoogleInbox();
                break;
            case 18:
                new ActivityHelper(context).openFeedback();
                break;
            case 19:
                AppDrawerActivity_.intent(context).start();
                break;
            case 20://Calender
                if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getCalenderPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 20, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.calenderPackage().get());
                }
                break;
            case 21://Clock
                if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getClockPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 21, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.clockPackage().get());
                }
                break;
            case 22://Photos
                if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getPhotosPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 22, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.photosPackage().get());
                }
                break;
            case 23:// Camera
                if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getCameraPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 23, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.cameraPackage().get());
                }
                break;
            case 24:// Browser
                if (context instanceof SiempoSettingsDefaultAppActivity) {
                    if (CoreApplication.getInstance().getBrowserPackageList().size() > 1) {
                        ((Launcher3App) CoreApplication.getInstance()).showPreferenceAppListDialog(context, 24, true);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_no_more_application), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new ActivityHelper(context).openAppWithPackageName(((MainActivity) context).prefs.browserPackage().get());
                }
                break;
            default:
                UIUtils.alert(context, getString(R.string.msg_not_yet_implemented));
                break;
        }
        MainActivity.isTextLenghGreater = "";

    }


}
