package co.siempo.phone.preferences;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Keep;

import co.siempo.phone.R;
import co.siempo.phone.activities.NotificationActivity;

@Keep
public class NotificationPreferencesFragment extends BasePreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_notification);
        findPreference("select_apps_that_may_interrupt").setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(getActivity(), NotificationActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            return true;
        });
    }
}
