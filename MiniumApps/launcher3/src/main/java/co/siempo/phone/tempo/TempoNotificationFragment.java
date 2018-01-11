package co.siempo.phone.tempo;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreFragment;

/**
 * Note : AllowPicking related stuff is now disable.
 */
@EFragment(R.layout.fragment_tempo_notifications)
public class TempoNotificationFragment extends CoreFragment {


    @ViewById
    Toolbar toolbar;

    @ViewById
    TextView titleActionBar;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @Pref
    DroidPrefs_ droidPrefs;

    @ViewById
    Switch switchDisableNotificationControls;
    @ViewById
    Switch switchAllowOnLockScreen;
    @ViewById
    Switch switchAllowPeaking;
    @ViewById
    TextView txtAllowPeakingText;
    @ViewById
    TextView txtAllowOnLockScreenText;
    @ViewById
    TextView txtAllowAppsText;
    @ViewById
    TextView txtAllowApps;
    @ViewById
    TextView txtAllowPeaking;
    @ViewById
    TextView txtAllowOnLockScreen;
    @ViewById
    TextView txtDisableNotificationControls;
    @ViewById
    TextView txtDisableNotificationControlsTxt;

    @ViewById
    RelativeLayout relAllowSpecificApps;
    private boolean isDisableChecked;
    private AlertDialog alertDialog;


    public TempoNotificationFragment() {
        // Required empty public constructor
    }


    @AfterViews
    void afterViews() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.string_notification_title);
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });

        if (droidPrefs.isTempoNotificationControlsDisabled().get()) {
            switchDisableNotificationControls.setChecked(true);
            txtAllowOnLockScreen.setVisibility(View.GONE);
//            txtAllowPeaking.setVisibility(View.GONE);
            txtAllowApps.setVisibility(View.GONE);
            txtAllowAppsText.setVisibility(View.GONE);
            txtAllowOnLockScreenText.setVisibility(View.GONE);
            switchAllowPeaking.setVisibility(View.GONE);
            switchAllowOnLockScreen.setVisibility(View.GONE);
//            txtAllowPeakingText.setVisibility(View.GONE);
            isDisableChecked = true;
            txtDisableNotificationControlsTxt.setText("All Siempo notifications options have been disabled, including Tempo and blocking apps by category. Use Android system settings to adjust notifications or re-enable this setting.");
        } else

        {
            switchDisableNotificationControls.setChecked(false);
            txtAllowOnLockScreen.setVisibility(View.VISIBLE);
//            txtAllowPeaking.setVisibility(View.VISIBLE);
            txtAllowApps.setVisibility(View.VISIBLE);
            txtAllowAppsText.setVisibility(View.VISIBLE);
            txtAllowOnLockScreenText.setVisibility(View.VISIBLE);
//            switchAllowPeaking.setVisibility(View.VISIBLE);
            switchAllowOnLockScreen.setVisibility(View.VISIBLE);
//            txtAllowPeakingText.setVisibility(View.VISIBLE);
            isDisableChecked = false;
            txtDisableNotificationControlsTxt.setText("Disabling Siempo's notifications controls means that you can no longer schedule nor control the appearance of notifications.");
        }

        switchDisableNotificationControls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isDisableChecked) {


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                            .setMessage("Most users report that phone notifications are a primary cause of unwanted distraction and encourage them to spend too much time on their phones.")
                            .setCancelable(false)
                            .setTitle("Are you sure ?")
                            .setPositiveButton("YES,DISABLE", null)
                            .setNegativeButton("CANCEL", null);

                    alertDialog = alertDialogBuilder.create();
//                                alertDialog.setCanceledOnTouchOutside(false);


                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                                      @Override
                                                      public void onShow(DialogInterface dialog) {

                                                          Button buttonNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                                                          buttonNegative.setTypeface(null, Typeface.BOLD);
                                                          buttonNegative
                                                                  .setOnClickListener(new View.OnClickListener() {
                                                                      @Override
                                                                      public void onClick(View v) {
                                                                          alertDialog.dismiss();
                                                                          switchDisableNotificationControls.setChecked(false);
                                                                          txtAllowOnLockScreen.setVisibility(View.VISIBLE);
//                                                                          txtAllowPeaking.setVisibility(View.VISIBLE);
                                                                          txtAllowApps.setVisibility(View.VISIBLE);
                                                                          txtAllowAppsText.setVisibility(View.VISIBLE);
                                                                          txtAllowOnLockScreenText.setVisibility(View.VISIBLE);
//                                                                          switchAllowPeaking.setVisibility(View.VISIBLE);
                                                                          switchAllowOnLockScreen.setVisibility(View.VISIBLE);
//                                                                          txtAllowPeakingText.setVisibility(View.VISIBLE);
                                                                          isDisableChecked = false;
                                                                          droidPrefs.isTempoNotificationControlsDisabled().put(false);
                                                                      }
                                                                  });

                                                          Button buttonPositive = alertDialog.getButton(AlertDialog
                                                                  .BUTTON_POSITIVE);

                                                          buttonPositive
                                                                  .setTextColor(context.getResources().getColor(R.color.pinktext_background_active_end));

                                                          buttonPositive.setTypeface(null, Typeface.BOLD);
                                                          buttonPositive
                                                                  .setOnClickListener(new View.OnClickListener() {
                                                                      @Override
                                                                      public void onClick(View v) {
                                                                          alertDialog.dismiss();

                                                                          switchDisableNotificationControls.setChecked(true);
                                                                          txtAllowOnLockScreen.setVisibility(View.GONE);
//                                                                          txtAllowPeaking.setVisibility(View.GONE);
                                                                          txtAllowApps.setVisibility(View.GONE);
                                                                          txtAllowAppsText.setVisibility(View.GONE);
                                                                          txtAllowOnLockScreenText.setVisibility(View.GONE);
//                                                                          switchAllowPeaking.setVisibility(View.GONE);
                                                                          switchAllowOnLockScreen.setVisibility(View.GONE);
//                                                                          txtAllowPeakingText.setVisibility(View.GONE);
                                                                          isDisableChecked = true;
                                                                          droidPrefs.isTempoNotificationControlsDisabled().put(true);
                                                                          txtDisableNotificationControlsTxt.setText("All Siempo notifications options have been disabled, including Tempo and blocking apps by category. Use Android system settings to adjust notifications or re-enable this setting.");

                                                                      }
                                                                  });
                                                      }
                                                  }


                    );

                    alertDialog.show();


                } else {
                    switchDisableNotificationControls.setChecked(false);
                    txtAllowOnLockScreen.setVisibility(View.VISIBLE);
//                    txtAllowPeaking.setVisibility(View.VISIBLE);
                    txtAllowApps.setVisibility(View.VISIBLE);
                    txtAllowAppsText.setVisibility(View.VISIBLE);
                    txtAllowOnLockScreenText.setVisibility(View.VISIBLE);
//                    switchAllowPeaking.setVisibility(View.VISIBLE);
                    switchAllowOnLockScreen.setVisibility(View.VISIBLE);
//                    txtAllowPeakingText.setVisibility(View.VISIBLE);
                    isDisableChecked = false;
                    droidPrefs.isTempoNotificationControlsDisabled().put(false);
                    txtDisableNotificationControlsTxt.setText("Disabling Siempo's notifications controls means that you can no longer schedule nor control the appearance of notifications.");
                }


            }
        });

        switchAllowOnLockScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                launcherPrefs.isHidenotificationOnLockScreen().put(!isChecked);
                if (isChecked) {
                    txtAllowOnLockScreenText.setText("On. New notifications will be visible from the lock screen.");
                } else {
                    txtAllowOnLockScreenText.setText("Off. All notifications will be hidden from the lock screen.");
                }
            }
        });


        if (launcherPrefs.isHidenotificationOnLockScreen().get()) {
            switchAllowOnLockScreen.setChecked(false);
        } else {
            switchAllowOnLockScreen.setChecked(true);
        }


    }


    @CheckedChange
    void switchAllowPeaking(CompoundButton btn, boolean isChecked) {
        if (isChecked) {
            txtAllowPeakingText.setText("On. When new notifications arrive, they may pop up over the current app. The status bar will also show when you have new notifications.");

        } else {
            txtAllowPeakingText.setText("Off. The status bar will show you when you have new notifications in your tray, but your tray won't pop up automatically.");
        }
    }

    @Click
    void relAllowSpecificApps() {

        try {
            Intent i = new Intent(getActivity(), TempoAppNotificationActivity.class);
            getActivity().startActivity(i);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
            CoreApplication.getInstance().logException(e);
        }
    }


}
