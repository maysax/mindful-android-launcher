package co.siempo.phone.preferences;

import android.os.Bundle;
import android.text.Html;

import androidx.annotation.Nullable;
import androidx.preference.Preference;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.helper.ActivityHelper;

public class SettingsActivity extends CoreActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar(findViewById(R.id.toolbar));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, getIntent().hasExtra("FlagApp")
                            ? new AppearancePreferencesFragment() : new MainFragment())
                    .commit();
        }
    }

    public static class MainFragment extends BasePreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences_main);

            final Preference alphaSettings = findPreference("alpha");
            alphaSettings.setVisible(BuildConfig.FLAVOR.equals("alpha"));
            alphaSettings.setTitle(Html.fromHtml("<font color='#EB5757'>" + alphaSettings.getTitle() + "</font>"));
            alphaSettings.setOnPreferenceClickListener(preference -> {
                new ActivityHelper(requireContext()).openSiempoAlphaSettingsApp();
                return true;
            });
        }
    }
}
