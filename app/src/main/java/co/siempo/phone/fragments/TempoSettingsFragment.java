package co.siempo.phone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.databinding.FragmentTempoSettingsBinding;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.utils.PrefSiempo;

public class TempoSettingsFragment extends CoreFragment implements View.OnClickListener {

    public TempoSettingsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    protected ViewBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentTempoSettingsBinding binding = FragmentTempoSettingsBinding.inflate(inflater, container, false);
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        binding.toolbar.setTitle(R.string.settings);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        if (BuildConfig.FLAVOR.equalsIgnoreCase(requireContext().getString(R.string.alpha))) {
            binding.relAlphaSettings.setVisibility(View.VISIBLE);
        } else {
            if (PrefSiempo.getInstance(requireContext()).read(PrefSiempo.IS_ALPHA_SETTING_ENABLE, false)) {
                binding.relAlphaSettings.setVisibility(View.VISIBLE);
            } else {
                binding.relAlphaSettings.setVisibility(View.GONE);
            }
        }

        binding.relHome.setOnClickListener(this);
        binding.relNotification.setOnClickListener(this);
        binding.relAppMenu.setOnClickListener(this);
        binding.relDoubleTap.setOnClickListener(this);
        binding.relAccount.setOnClickListener(this);
        binding.relAlphaSettings.setOnClickListener(this);
        return binding;
    }

    @Override
    public void onClick(View v) {
        final CoreActivity activity = (CoreActivity) requireActivity();
        if (v.getId() == R.id.relHome) {
            activity.loadChildFragment(new TempoHomeFragment(), R.id.tempoView);
        } else if (v.getId() == R.id.relNotification) {
            activity.loadChildFragment(new TempoNotificationFragment(), R.id.tempoView);
        } else if (v.getId() == R.id.relAppMenu) {
            activity.loadChildFragment(AppMenuFragment.newInstance(false), R.id.tempoView);
        } else if (v.getId() == R.id.relDoubleTap) {
            activity.loadChildFragment(new DoubleTapControlsFragment(), R.id.tempoView);
        } else if (v.getId() == R.id.relAccount) {
            activity.loadChildFragment(new AccountSettingFragment(), R.id.tempoView);
        } else if (v.getId() == R.id.relAlphaSettings) {
            new ActivityHelper(requireContext()).openSiempoAlphaSettingsApp();
        }
    }
}
