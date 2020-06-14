package co.siempo.phone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import co.siempo.phone.R;
import co.siempo.phone.activities.NotificationActivity;
import co.siempo.phone.databinding.FragmentTempoNotificationsBinding;
import co.siempo.phone.utils.PrefSiempo;

/**
 * Note : AllowPicking related stuff is now disable.
 */
public class TempoNotificationFragment extends CoreFragment {
    public TempoNotificationFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    protected ViewBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentTempoNotificationsBinding binding = FragmentTempoNotificationsBinding.inflate(inflater, container, false);
        binding.toolbar.setTitle(R.string.string_notification_title);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        binding.switchAllowPicking.setChecked(PrefSiempo.getInstance(requireContext()).read(PrefSiempo.ALLOW_PEAKING, true));
        if (binding.switchAllowPicking.isChecked()) {
            binding.txtAllowPickingtxt.setText(getString(R.string.msg_allowpeakingon));
        } else {
            binding.txtAllowPickingtxt.setText(getString(R.string.msg_allowpeaking));
        }

        binding.relContainerAllowSpecificApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NotificationActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });

        binding.relAllowPicking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTempoNotificationsBinding binding1 = requireViewBinding();
                boolean allowPeaking = PrefSiempo.getInstance(requireContext()).read(PrefSiempo.ALLOW_PEAKING, true);

                allowPeaking = !allowPeaking;
                binding1.switchAllowPicking.setChecked(allowPeaking);
                PrefSiempo.getInstance(requireContext()).write(PrefSiempo.ALLOW_PEAKING, allowPeaking);
                if (binding1.switchAllowPicking.isChecked()) {
                    binding1.txtAllowPickingtxt.setText(getString(R.string.msg_allowpeakingon));
                } else {
                    binding1.txtAllowPickingtxt.setText(getString(R.string.msg_allowpeaking));
                }
            }
        });
        return binding;
    }
}
