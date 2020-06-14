package co.siempo.phone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.databinding.FragmentTempoNotificationsBinding;

public class TempoNotificationAppsFragment extends CoreFragment {
    public TempoNotificationAppsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    protected ViewBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentTempoNotificationsBinding binding = FragmentTempoNotificationsBinding.inflate(inflater, container, false);
        binding.toolbar.setTitle(R.string.allow_specific_apps);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finish();
            }
        });
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireFragmentManager().popBackStack();
            }
        });
        binding.relAllowSpecificApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CoreActivity) requireActivity()).loadChildFragment(new TempoNotificationFragment(), R.id.tempoView);
            }
        });
        return binding;
    }
}
