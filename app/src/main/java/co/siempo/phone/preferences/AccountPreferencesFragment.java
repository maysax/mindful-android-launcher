package co.siempo.phone.preferences;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;

import co.siempo.phone.R;
import co.siempo.phone.fragments.PrivacyPolicyFragment;
import co.siempo.phone.fragments.TempoUpdateEmailFragment;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.launcher.FakeLauncherActivity;

@Keep
public class AccountPreferencesFragment extends BasePreferenceFragment {
    private long startTime = 0;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_account);
        findPreference(Preferences.KEY_EMAIL_ADDRESS).setOnPreferenceClickListener(preference -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new TempoUpdateEmailFragment())
                    .addToBackStack(null)
                    .commit();
            return true;
        });
        findPreference("exit_siempo").setOnPreferenceClickListener(preference -> {
            showAlertForFirstTime();
            return true;
        });

        final SpannableString privacyPolicyLink = new SpannableString(getString(R.string.privacypolicy_link));
        privacyPolicyLink.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PrivacyPolicyFragment())
                        .addToBackStack(null)
                        .commit();
            }
        }, 0, privacyPolicyLink.length(), 0);

        final Preference privacyPolicyPreference = findPreference("privacy_policy");

        privacyPolicyPreference.setSummary(new SpannableStringBuilder(privacyPolicyPreference.getSummary()).append(privacyPolicyLink));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (Preferences.KEY_ANALYTICS_ENABLED.equals(key)) {
            FirebaseHelper.getInstance().getFirebaseAnalytics().setAnalyticsCollectionEnabled(preferences.getBoolean(key, true));
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

    /**
     * This dialog used when user press exit siempo service.
     */
    private void showAlertForFirstTime() {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.exiting_siempo))
                .setMessage(R.string.exiting_siempo_msg)
                .setPositiveButton(getString(R.string.exit), (dialog1, which) -> {
                    dialog1.dismiss();
                    resetPreferredLauncherAndOpenChooser(requireContext());
                })
                .setNegativeButton(getString(R.string.cancel), (dialog1, which) -> dialog1.dismiss())
                .show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialog_blue));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialog_blue));
    }

    /**
     * Method to reset the launcher activity and show default chooser
     *
     * @param context
     */
    private void resetPreferredLauncherAndOpenChooser(Context context) {
        try {
            final PackageManager packageManager = context.getPackageManager();
            final ComponentName componentName = new ComponentName(context, FakeLauncherActivity.class);
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

            final Intent selector = new Intent(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_HOME)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(selector);

            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            e.printStackTrace();
            //In case of exception start the default setting screen
        }
    }
}
