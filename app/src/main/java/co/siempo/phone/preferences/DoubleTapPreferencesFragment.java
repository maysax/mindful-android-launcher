package co.siempo.phone.preferences;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.preference.SwitchPreferenceCompat;

import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.receivers.ScreenOffAdminReceiver;

import static android.content.Context.NOTIFICATION_SERVICE;

@Keep
public class DoubleTapPreferencesFragment extends BasePreferenceFragment {
    private static final int REQUEST_CODE_NOTIFICATION_ACCESS = 111;
    private static final int REQUEST_CODE_DEVICE_ADMIN = 112;

    private SwitchPreferenceCompat sleepPreference;
    private SwitchPreferenceCompat dndPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        sleepPreference = findPreference(Preferences.KEY_SLEEP_MODE_ENABLED);
        dndPreference = findPreference(Preferences.KEY_DND_ENABLED);

        sleepPreference.setOnPreferenceChangeListener((preference, newValue)
                -> checkDeviceAdminAccessGranted(!Boolean.TRUE.equals(newValue)));
        dndPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (Boolean.TRUE.equals(newValue)) {
                return checkNotificationAccessGranted(false);
            } else if (checkNotificationAccessGranted(true)) {
                ((CoreActivity) requireActivity()).changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_ALL);
                return true;
            }
            return true;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NOTIFICATION_ACCESS) {
            if (resultCode == Activity.RESULT_OK) {
                if (!checkNotificationAccessGranted(true)) {
                    Toast.makeText(getActivity(), "Please grant notification access", Toast.LENGTH_LONG).show();
                }
            } else {
                dndPreference.setChecked(false);
            }
        } else if (requestCode == REQUEST_CODE_DEVICE_ADMIN) {
            if (resultCode == Activity.RESULT_OK) {
                if (!checkDeviceAdminAccessGranted(true)) {
                    Toast.makeText(getActivity(), "Please grant device admin access", Toast.LENGTH_LONG).show();
                }
            } else {
                sleepPreference.setChecked(false);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean checkNotificationAccessGranted(boolean onlyAccessCheck) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // If api level minimum 23
            final NotificationManager nm = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            if (!nm.isNotificationPolicyAccessGranted()) {
                if (!onlyAccessCheck) {
                    startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS),
                            REQUEST_CODE_NOTIFICATION_ACCESS);
                }
                return false;
            }
            return true;
        }
        return true;
    }

    private boolean checkDeviceAdminAccessGranted(boolean onlyAccessCheck) {
        final DevicePolicyManager policyManager = (DevicePolicyManager) requireContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        final ComponentName adminReceiver = new ComponentName(requireContext(), ScreenOffAdminReceiver.class);
        final boolean admin = policyManager.isAdminActive(adminReceiver);
        if (onlyAccessCheck) {
            return admin;
        }
        if (!admin) {
            // ask for device administration rights
            final Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                    .putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                            new ComponentName(requireContext(), ScreenOffAdminReceiver.class))
                    .putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, R.string.device_admin_description);
            startActivityForResult(intent, REQUEST_CODE_DEVICE_ADMIN);
        }
        return false;
    }
}
