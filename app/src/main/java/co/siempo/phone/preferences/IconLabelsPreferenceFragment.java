package co.siempo.phone.preferences;

import android.os.Bundle;

import androidx.annotation.Keep;

import co.siempo.phone.R;

@Keep
public class IconLabelsPreferenceFragment extends BasePreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_icon_labels);
    }
}
