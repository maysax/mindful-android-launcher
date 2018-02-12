package co.siempo.phone.activities;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.helper.FirebaseHelper;

/**
 * This class contain all the native settings feature.
 */
//@EActivity
public class SiempoPhoneSettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static String TAG = "SiempoPhoneSettings";
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
    long startTime = 0;

    public static int retrieveStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static boolean isAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setPadding(0, retrieveStatusBarHeight(this), 0, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getIntance().logScreenUsageTime(SiempoPhoneSettingsActivity.class.getSimpleName(), startTime);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || ConnectionFragment.class.getName().equals(fragmentName)
                || DeviceSettingsFragment.class.getName().equals(fragmentName)
                || PersonalSettingsFragment.class.getName().equals(fragmentName)
                || SystemSettingsFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        long startTime = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                SiempoSettingsActivity_.intent(getActivity()).start();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            if (view != null) {
                ListView preferencesList = view.findViewById(android.R.id.list);
                preferencesList.setPadding(0, SiempoPhoneSettingsActivity.retrieveStatusBarHeight(getActivity()), 0, 0);
            }
            return view;
        }

        @Override
        public void onPause() {
            super.onPause();
            FirebaseHelper.getIntance().logScreenUsageTime(GeneralPreferenceFragment.class.getSimpleName(), startTime);
        }

        @Override
        public void onResume() {
            super.onResume();
            startTime = System.currentTimeMillis();
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        long startTime = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                SiempoSettingsActivity_.intent(getActivity()).start();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            if (view != null) {
                ListView preferencesList = view.findViewById(android.R.id.list);
                preferencesList.setPadding(0, SiempoPhoneSettingsActivity.retrieveStatusBarHeight(getActivity()), 0, 0);
            }
            return view;
        }

        @Override
        public void onPause() {
            super.onPause();
            FirebaseHelper.getIntance().logScreenUsageTime(NotificationPreferenceFragment.class.getSimpleName(), startTime);
        }

        @Override
        public void onResume() {
            super.onResume();
            startTime = System.currentTimeMillis();
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        long startTime = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                SiempoSettingsActivity_.intent(getActivity()).start();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            if (view != null) {
                ListView preferencesList = view.findViewById(android.R.id.list);
                preferencesList.setPadding(0, SiempoPhoneSettingsActivity.retrieveStatusBarHeight(getActivity()), 0, 0);
            }
            return view;
        }

        @Override
        public void onPause() {
            super.onPause();
            FirebaseHelper.getIntance().logScreenUsageTime(DataSyncPreferenceFragment.class.getSimpleName(), startTime);
        }

        @Override
        public void onResume() {
            super.onResume();
            startTime = System.currentTimeMillis();
        }
    }

    /**
     * This fragment shows wifi preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ConnectionFragment extends PreferenceFragment {
        long startTime = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.connections);
            setHasOptionsMenu(true);

            // Check wifi preference in local system
            checkPreferenceAvailable(getString(R.string.wifi_key), Settings.ACTION_WIFI_SETTINGS);

            // Check bluetooth preference in local system
            checkPreferenceAvailable(getString(R.string.bluetooth_key), Settings.ACTION_BLUETOOTH_SETTINGS);

            // Check Data Usage preference in local system
            try {
                Preference data_usage_preference = findPreference(getString(R.string.data_usage_key));
                Intent intent_data_usage = new Intent();
                intent_data_usage.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                //     intent_data_usage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (!isAvailable(getActivity(), intent_data_usage)) {
                    PreferenceScreen preferenceScreen = getPreferenceScreen();
                    if (preferenceScreen != null) {
                        preferenceScreen.removePreference(data_usage_preference);
                    }
                } else {
                    data_usage_preference.setIntent(intent_data_usage);
                    bindPreferenceSummaryToValue(data_usage_preference);
                }
            } catch (Exception e) {
                CoreApplication.getInstance().logException(e);
                e.printStackTrace();
            }

            // Check WireLess preference in local system
            checkPreferenceAvailable(getString(R.string.wireless_key), Settings.ACTION_WIRELESS_SETTINGS);

        }

        private void checkPreferenceAvailable(String string, String actionWirelessSettings) {
            try {
                Preference wireless_preference = findPreference(string);
                Intent intent_wireless_preference = new Intent(actionWirelessSettings);
                if (!isAvailable(getActivity(), intent_wireless_preference)) {
                    PreferenceScreen preferenceScreen = getPreferenceScreen();
                    if (preferenceScreen != null) {
                        preferenceScreen.removePreference(wireless_preference);
                    }
                } else {
                    wireless_preference.setIntent(intent_wireless_preference);
                    bindPreferenceSummaryToValue(wireless_preference);
                }
            } catch (Exception e) {
                CoreApplication.getInstance().logException(e);
                e.printStackTrace();
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                SiempoSettingsActivity_.intent(getActivity()).start();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            if (view != null) {
                ListView preferencesList = view.findViewById(android.R.id.list);
                preferencesList.setPadding(0, SiempoPhoneSettingsActivity.retrieveStatusBarHeight(getActivity()), 0, 0);
            }
            return view;
        }

        @Override
        public void onPause() {
            super.onPause();
            FirebaseHelper.getIntance().logScreenUsageTime(ConnectionFragment.class.getSimpleName(), startTime);
        }

        @Override
        public void onResume() {
            super.onResume();
            startTime = System.currentTimeMillis();
        }
    }

    /**
     * This fragment shows Device preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DeviceSettingsFragment extends PreferenceFragment {
        long startTime = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.device_settings);
            setHasOptionsMenu(true);

            // Check home preference in local system
            checkPreferenceAvailable(getString(R.string.home_key), Settings.ACTION_HOME_SETTINGS);

            // Check display preference in local system
            checkPreferenceAvailable(getString(R.string.display_key), Settings.ACTION_DISPLAY_SETTINGS);

            // Check sound preference in local system
            checkPreferenceAvailable(getString(R.string.sound_key), Settings.ACTION_SOUND_SETTINGS);

            // Check Apps preference in local system
            checkPreferenceAvailable(getString(R.string.apps_key), Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);

            // Check Memory card preference in local system
            checkPreferenceAvailable(getString(R.string.memory_card_setting_key), Settings.ACTION_MEMORY_CARD_SETTINGS);

            // Check Battery card preference in local system
            checkPreferenceAvailable(getString(R.string.key_battery), Intent.ACTION_POWER_USAGE_SUMMARY);

            // Check Users preference in local system
            try {
                Preference preference = findPreference(getString(R.string.key_memory));
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$MemorySettingsActivity"));
                if (!isAvailable(getActivity(), intent)) {
                    PreferenceScreen preferenceScreen = getPreferenceScreen();
                    if (preferenceScreen != null) {
                        preferenceScreen.removePreference(preference);
                    }
                } else {
                    preference.setIntent(intent);
                    bindPreferenceSummaryToValue(preference);
                }
            } catch (Exception e) {
                CoreApplication.getInstance().logException(e);
                e.printStackTrace();
            }
            // Check User Setting preference in local system
            checkPreferenceAvailable(getString(R.string.key_user_setting), "android.settings.USER_SETTINGS");

            // Check Tap and Pay preference in local system
            checkPreferenceAvailable(getString(R.string.key_tap_pay), Settings.ACTION_NFC_PAYMENT_SETTINGS);

        }

        private void checkPreferenceAvailable(String string, String actionNfcPaymentSettings) {
            try {
                Preference preference = findPreference(string);
                Intent intent = new Intent(actionNfcPaymentSettings);
                if (!isAvailable(getActivity(), intent)) {
                    PreferenceScreen preferenceScreen = getPreferenceScreen();
                    if (preferenceScreen != null) {
                        preferenceScreen.removePreference(preference);
                    }
                } else {
                    preference.setIntent(intent);
                    bindPreferenceSummaryToValue(preference);
                }

                //This is the listener for Preference being clicked
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        //In case if the device is Huawei and Users
                        // preference is clicked then it will throw Security
                        // Exception hence to prevent it we will handle the
                        // click by showing a message and not navigating to
                        // the activity
                        if (preference.getTitle().toString().equalsIgnoreCase
                                (getString(R.string.pref_users)) && Build
                                .MANUFACTURER
                                .equalsIgnoreCase(Constants.HUAWEI)) {
                            Toast.makeText(getActivity(), R.string.huawei_limitation, Toast
                                    .LENGTH_SHORT).show();
                            return true;
                        } else {
                            return false;

                        }

                    }
                });
            } catch (Exception e) {
                CoreApplication.getInstance().logException(e);
                e.printStackTrace();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            if (view != null) {
                ListView preferencesList = view.findViewById(android.R.id.list);
                preferencesList.setPadding(0, SiempoPhoneSettingsActivity.retrieveStatusBarHeight(getActivity()), 0, 0);
            }
            return view;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                SiempoSettingsActivity_.intent(getActivity()).start();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onPause() {
            super.onPause();
            FirebaseHelper.getIntance().logScreenUsageTime(DeviceSettingsFragment.class.getSimpleName(), startTime);
        }

        @Override
        public void onResume() {
            super.onResume();
            startTime = System.currentTimeMillis();
        }

    }


    /**
     * This fragment shows Device preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PersonalSettingsFragment extends PreferenceFragment {
        long startTime = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.personal_settings);
            setHasOptionsMenu(true);

            // Check Location Source preference in local system
            checkPreferenceAvailable(getString(R.string.location_key), Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            // Check Security preference in local system
            checkPreferenceAvailable(getString(R.string.security_key), Settings.ACTION_SECURITY_SETTINGS);

            // Check Account preference in local system
            checkPreferenceAvailable(getString(R.string.account_key), Settings.ACTION_SYNC_SETTINGS);

            // Check Account preference in local system
            checkPreferenceAvailable(getString(R.string.account_key), Settings.ACTION_SYNC_SETTINGS);

            // Check Language preference in local system
            checkPreferenceAvailable(getString(R.string.pref_language_key), Settings.ACTION_INPUT_METHOD_SETTINGS);

            // Check backup preference in local system
            checkPreferenceAvailable(getString(R.string.backup_key), "android.settings.BACKUP_AND_RESET_SETTINGS");

        }

        private void checkPreferenceAvailable(String string, String action) {
            try {
                Preference preference = findPreference(string);
                Intent intent = new Intent(action);
                if (!isAvailable(getActivity(), intent)) {
                    PreferenceScreen preferenceScreen = getPreferenceScreen();
                    if (preferenceScreen != null) {
                        preferenceScreen.removePreference(preference);
                    }
                } else {
                    preference.setIntent(intent);
                    bindPreferenceSummaryToValue(preference);
                }
            } catch (Exception e) {
                CoreApplication.getInstance().logException(e);
                e.printStackTrace();
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                SiempoSettingsActivity_.intent(getActivity()).start();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            if (view != null) {
                ListView preferencesList = view.findViewById(android.R.id.list);
                preferencesList.setPadding(0, SiempoPhoneSettingsActivity.retrieveStatusBarHeight(getActivity()), 0, 0);
            }
            return view;
        }

        @Override
        public void onPause() {
            super.onPause();
            FirebaseHelper.getIntance().logScreenUsageTime(PersonalSettingsFragment.class.getSimpleName(), startTime);
        }

        @Override
        public void onResume() {
            super.onResume();
            startTime = System.currentTimeMillis();
        }
    }


    /**
     * This fragment shows Device preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SystemSettingsFragment extends PreferenceFragment {
        long startTime = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.system_settings);
            setHasOptionsMenu(true);

            // Check Date Time preference in local system
            checkPreferenceAvailable(getString(R.string.date_time_key), Settings.ACTION_DATE_SETTINGS);

            // Check ACCESSIBILITY preference in local system
            checkPreferenceAvailable(getString(R.string.accessibility_key), Settings.ACTION_ACCESSIBILITY_SETTINGS);

            // Check Print preference in local system
            checkPreferenceAvailable(getString(R.string.print_key), Settings.ACTION_PRINT_SETTINGS);

            // Check About Phone preference in local system
            checkPreferenceAvailable(getString(R.string.about_key), Settings.ACTION_DEVICE_INFO_SETTINGS);


        }

        private void checkPreferenceAvailable(String string, String actionDeviceInfoSettings) {
            try {
                Preference preference = findPreference(string);
                Intent intent = new Intent(actionDeviceInfoSettings);
                if (!isAvailable(getActivity(), intent)) {
                    PreferenceScreen preferenceScreen = getPreferenceScreen();
                    if (preferenceScreen != null) {
                        preferenceScreen.removePreference(preference);
                    }
                } else {
                    preference.setIntent(intent);
                    bindPreferenceSummaryToValue(preference);
                }
            } catch (Exception e) {
                CoreApplication.getInstance().logException(e);
                e.printStackTrace();
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                SiempoSettingsActivity_.intent(getActivity()).start();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            if (view != null) {
                ListView preferencesList = view.findViewById(android.R.id.list);
                preferencesList.setPadding(0, SiempoPhoneSettingsActivity.retrieveStatusBarHeight(getActivity()), 0, 0);
            }
            return view;
        }

        @Override
        public void onPause() {
            super.onPause();
            FirebaseHelper.getIntance().logScreenUsageTime(System.class.getSimpleName(), startTime);
        }

        @Override
        public void onResume() {
            super.onResume();
            startTime = System.currentTimeMillis();
        }
    }


}
