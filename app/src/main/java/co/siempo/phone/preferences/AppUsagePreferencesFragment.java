package co.siempo.phone.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreferenceCompat;

import org.greenrobot.eventbus.EventBus;

import co.siempo.phone.R;
import co.siempo.phone.activities.JunkfoodFlaggingActivity;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.NotifyBottomView;
import co.siempo.phone.event.NotifyFavortieView;
import co.siempo.phone.event.NotifyJunkFoodView;
import co.siempo.phone.event.NotifyToolView;
import co.siempo.phone.event.ReduceOverUsageEvent;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;

import static com.rvalerio.fgchecker.Utils.hasUsageStatsPermission;

@Keep
public class AppUsagePreferencesFragment extends BasePreferenceFragment {
    private static final int REQUEST_CODE_USAGE_ACCESS = 100;

    private long startTime = 0;
    private AlertDialog confirmDisableIconHidingDialog;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_app_usage);

        findPreference("choose_flagged_apps").setOnPreferenceClickListener(preference -> {
            final Intent junkFoodFlagIntent = new Intent(requireContext(), JunkfoodFlaggingActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    .putExtra("FromAppMenu", true);
            startActivity(junkFoodFlagIntent);
            return true;
        });
        findPreference(Preferences.KEY_HIDE_ICON_BRANDING)
                .setOnPreferenceChangeListener((preference, newValue) -> {
                    if (confirmDisableIconHidingDialog != null && confirmDisableIconHidingDialog.isShowing()) {
                        // Do not intercept changes made by the dialog
                        Log.d("TAG_AUPF", "onCreatePreferences() called with: savedInstanceState = [" + savedInstanceState + "], rootKey = [" + rootKey + "]");
                        return true;
                    }
                    if (Boolean.FALSE.equals(newValue)) {
                        showDialogOnHideIconBranding();
                        return false;
                    }
                    return true;
                });
        findPreference(Preferences.KEY_DETER_FROM_JUNKFOOD_AFTER).setOnPreferenceChangeListener((preference, newValue) -> {
            if (!hasUsageStatsPermission(requireContext())) {
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), REQUEST_CODE_USAGE_ACCESS);
                return false;
            }
            return true;
        });
        findPreference(Preferences.KEY_DETER_FROM_JUNKFOOD_AFTER).setSummaryProvider(
                (Preference.SummaryProvider<ListPreference>) preference -> {
                    if ("-1".equals(preference.getValue())) {
                        return "Disabled. Tap to enable.";
                    }
                    return Html.fromHtml(getString(R.string.reduce_overuse_Flagged_description_setting,
                            "<font color='#42A4FF'>" + preference.getEntry() + "</font>"));
                });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key == null) {
            return;
        }
        switch (key) {
            case Preferences.KEY_HIDE_ICON_BRANDING: {
                final boolean hidden = preferences.getBoolean(Preferences.KEY_HIDE_ICON_BRANDING, true);
                FirebaseHelper.getInstance().logIntention_IconBranding_Randomize(FirebaseHelper.HIDE_ICON_BRANDING, hidden ? 1 : 0);
                CoreApplication.getInstance().setHideIconBranding(hidden);
                EventBus.getDefault().postSticky(new NotifyJunkFoodView(true));
                EventBus.getDefault().postSticky(new NotifyFavortieView(true));
                EventBus.getDefault().postSticky(new NotifyToolView(true));
                EventBus.getDefault().postSticky(new NotifyBottomView(true));
                break;
            }
            case Preferences.KEY_RANDOMIZE_JUNKFOOD: {
                final boolean enabled = preferences.getBoolean(Preferences.KEY_RANDOMIZE_JUNKFOOD, true);
                FirebaseHelper.getInstance().logIntention_IconBranding_Randomize(FirebaseHelper.RANDOMIZED_JUNK_FOOD, enabled ? 1 : 0);
                CoreApplication.getInstance().setRandomize(enabled);
                EventBus.getDefault().postSticky(new NotifyJunkFoodView(true));
                break;
            }
            case Preferences.KEY_DETER_FROM_JUNKFOOD_AFTER: {
                final int intValue = Integer.parseInt(
                        preferences.getString(Preferences.KEY_DETER_FROM_JUNKFOOD_AFTER, "-1")
                );
                preferences.edit().putInt(PrefSiempo.DETER_AFTER, intValue).apply();
                FirebaseHelper.getInstance().logDeterUseEvent(intValue);
                EventBus.getDefault().post(new ReduceOverUsageEvent(true));
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        FirebaseHelper.getInstance().logScreenUsageTime(getClass().getSimpleName(), startTime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_USAGE_ACCESS) {
            if (!UIUtils.hasUsageStatsPermission(requireContext())) {
                Toast.makeText(requireContext(), R.string.msg_control_access, Toast.LENGTH_SHORT).show();
            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (!Settings.canDrawOverlays(context)) {
//                        if (null == overlayDialogPermission || !overlayDialogPermission.isShowing())
//                            showOverLayForDrawingPermission();
//                    } else {
//                        showDialog();
//                    }
//                } else {
                //showDialog();
//                }
            }
        } else if (requestCode == 1000) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(requireContext())) {
                Toast.makeText(requireContext(), R.string.msg_draw_over_app, Toast.LENGTH_SHORT).show();
            } else {
                //showDialog();
            }
        }
    }

    private boolean requestUsageStatsPermission() {
        if (!hasUsageStatsPermission(requireContext())) {
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), REQUEST_CODE_USAGE_ACCESS);
            return true;
        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (!Settings.canDrawOverlays(context)) {
//                    if (null == overlayDialogPermission || !overlayDialogPermission.isShowing())
//                        showOverLayForDrawingPermission();
//                } else {
//                    showDialog();
//                }
//            } else {
            //showDialog();
//            }
            return false;
        }
    }

    private void showDialogOnHideIconBranding() {
        if (!isAdded()) {
            return;
        }
        if (confirmDisableIconHidingDialog == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(R.string.msg_hide_icon_branding)
                    .setPositiveButton(getString(R.string.yes_unhide), (dialog, which) -> {
                        dialog.dismiss();
                        final SwitchPreferenceCompat preference = findPreference(Preferences.KEY_HIDE_ICON_BRANDING);
                        if (preference != null) {
                            preference.setChecked(false);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
            confirmDisableIconHidingDialog = builder.show();
            confirmDisableIconHidingDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialog_blue));
            confirmDisableIconHidingDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialog_red));
        } else {
            confirmDisableIconHidingDialog.show();
        }
    }
}
