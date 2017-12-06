package co.siempo.phone.settings;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.joanzapata.iconify.IconDrawable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.applist.AppDrawerActivity;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.mm.model.Utilities;
import co.siempo.phone.util.PackageUtil;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;


/**
 * Created by hardik on 17/8/17.
 */

/**
 * This class contain all the siempo settings feature.
 * 1. Switc home app
 * 2. Keyboard hide & show in IF Screen when launch
 * 3. Version of Current App & update
 */
@EActivity(R.layout.activity_app_list_notification)
public class AppListNotification extends CoreActivity {
    private String TAG = "AppListNotification";
    private Context context;

    @ViewById
    LinearLayout linSocial;

    @ViewById
    ImageView icon_AllowNotificationFacebook;

    @ViewById
    SwitchCompat switch_AllowNotificationFacebook;

    @ViewById
    RelativeLayout relMessenger;

    @ViewById
    ImageView icon_AllowNotificationMessenger;

    @ViewById
    SwitchCompat switch_AllowNotificationMessenger;


    @ViewById
    RelativeLayout relLite;

    @ViewById
    ImageView icon_AllowNotificationLite;

    @ViewById
    SwitchCompat switch_AllowNotificationLite;

    @ViewById
    RelativeLayout relWhatsApp;

    @ViewById
    ImageView icon_AllowNotificationWhatsApp;

    @ViewById
    SwitchCompat switch_AllowNotificationWhatsApp;

    @ViewById
    RelativeLayout relHangOut;

    @ViewById
    ImageView icon_AllowNotificationHangout;

    @ViewById
    SwitchCompat switch_AllowNotificationHangout;


    @Pref
    Launcher3Prefs_ launcherPrefs;

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
        }
    }

    @AfterViews
    void afterViews() {
        initView();
        onClickEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentIndex = 0;
    }


    public void initView() {
        context = AppListNotification.this;

        //Facebook
        if (UIUtils.isAppInstalled(context, Constants.FACEBOOK_PACKAGE)) {
            linSocial.setVisibility(View.VISIBLE);
            icon_AllowNotificationFacebook.setImageBitmap(CoreApplication.getInstance().iconList.get(Constants.FACEBOOK_PACKAGE));
            switch_AllowNotificationFacebook.setChecked(prefs.isFacebookAllowed().get());
            linSocial.setVisibility(View.GONE);
        } else {
            linSocial.setVisibility(View.GONE);
        }

        //Messenger
        if (UIUtils.isAppInstalled(context, Constants.FACEBOOK_MESSENGER_PACKAGE)) {
            relMessenger.setVisibility(View.VISIBLE);
            icon_AllowNotificationMessenger.setImageBitmap(CoreApplication.getInstance().iconList.get(Constants.FACEBOOK_MESSENGER_PACKAGE));
            switch_AllowNotificationMessenger.setChecked(prefs.isFacebooKMessangerAllowed().get());
        } else {
            relMessenger.setVisibility(View.GONE);
        }

        //Lite
        if (UIUtils.isAppInstalled(context, Constants.FACEBOOK_LITE_PACKAGE)) {
            relLite.setVisibility(View.VISIBLE);
            icon_AllowNotificationLite.setImageBitmap(CoreApplication.getInstance().iconList.get(Constants.FACEBOOK_LITE_PACKAGE));
            switch_AllowNotificationLite.setChecked(prefs.isFacebooKMessangerLiteAllowed().get());
        } else {
            relLite.setVisibility(View.GONE);
        }

        //WhatsApp
        if (UIUtils.isAppInstalled(context, Constants.WHATSAPP_PACKAGE)) {
            relWhatsApp.setVisibility(View.VISIBLE);
            icon_AllowNotificationWhatsApp.setImageBitmap(CoreApplication.getInstance().iconList.get(Constants.WHATSAPP_PACKAGE));
            switch_AllowNotificationWhatsApp.setChecked(prefs.isWhatsAppAllowed().get());
        } else {
            relWhatsApp.setVisibility(View.GONE);
        }

        //Hangouts
        if (UIUtils.isAppInstalled(context, Constants.GOOGLE_HANGOUTS_PACKAGES)) {
            relHangOut.setVisibility(View.VISIBLE);
            icon_AllowNotificationHangout.setImageBitmap(CoreApplication.getInstance().iconList.get(Constants.GOOGLE_HANGOUTS_PACKAGES));
            switch_AllowNotificationHangout.setChecked(prefs.isHangOutAllowed().get());
        } else {
            relHangOut.setVisibility(View.GONE);
        }


    }

    private void onClickEvents() {

        switch_AllowNotificationFacebook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.isFacebookAllowed().put(isChecked);
            }
        });

        switch_AllowNotificationMessenger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.isFacebooKMessangerAllowed().put(isChecked);
            }
        });
        switch_AllowNotificationLite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.isFacebooKMessangerLiteAllowed().put(isChecked);
            }
        });

        switch_AllowNotificationWhatsApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.isWhatsAppAllowed().put(isChecked);
            }
        });

        switch_AllowNotificationHangout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.isHangOutAllowed().put(isChecked);
            }
        });


    }

    long startTime=0;
    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        PackageUtil.checkPermission(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getIntance().logScreenUsageTime(AppListNotification.this.getClass().getSimpleName(),startTime);
    }

    @Override
    protected void onStop() {

        super.onStop();
        currentIndex = 0;
    }


}
