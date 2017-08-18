package co.siempo.phone.main;

import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;

import java.util.List;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
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

import static co.siempo.phone.R.string.title_defaultLauncher;
import static co.siempo.phone.app.Constants.GOOGLE_PHOTOS;

/**
 * Created by Shahab on 5/4/2017.
 */

public class MainListItemLoader {

    private Context context;

    public MainListItemLoader(Context context) {
        this.context = context;
    }

    public void loadItems(List<MainListItem> items) {
        items.add(new MainListItem(2, context.getString(R.string.title_calls), "fa-phone", R.drawable.icon_call, MainListItemType.ACTION));
        items.add(new MainListItem(1, getString(R.string.title_messages), "fa-users", R.drawable.icon_sms, MainListItemType.ACTION));
        items.add(new MainListItem(20, getString(R.string.title_calendar), "fa-calendar"));
        items.add(new MainListItem(3, getString(R.string.title_contacts), "fa-user", R.drawable.icon_create_user, MainListItemType.ACTION));
        items.add(new MainListItem(11, getString(R.string.title_map), "fa-street-view"));
        items.add(new MainListItem(6, getString(R.string.title_notes), "fa-sticky-note", R.drawable.icon_save_note, MainListItemType.ACTION));

        //if (new ActivityHelper(context).isAppInstalled(GOOGLE_PHOTOS))
            items.add(new MainListItem(22, getString(R.string.title_photos), "fa-picture-o"));

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


    }

    private final String getString(@StringRes int resId, Object... formatArgs) {
        return context.getString(resId, formatArgs);
    }

    public void listItemClicked(int id) {
        switch (id) {
            case 1:
                new ActivityHelper(context).openMessagingApp();
                break;
            case 2:
                new ActivityHelper(context).openCallApp();
                break;
            case 3:
                new ActivityHelper(context).openContactsApp();
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
            case 11:
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
                new ActivityHelper(context).openGmail();
                break;
            case 17: //new ActivityHelper(context).openGoogleInbox(); break;
            case 18:
                new ActivityHelper(context).openFeedback();
                break;
            case 19:
                AppDrawerActivity_.intent(context).start();
                break;
            case 20:
                new ActivityHelper(context).openCalenderApp();
                break;
            case 21:
                new ActivityHelper(context).openClockApp();
                break;
            case 22:
                new ActivityHelper(context).openPhotsApp();
                break;
            default:
                UIUtils.alert(context, getString(R.string.msg_not_yet_implemented));
                break;
        }
    }
}
