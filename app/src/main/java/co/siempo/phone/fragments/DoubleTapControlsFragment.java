package co.siempo.phone.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.databinding.FragmentDoubletapControlBinding;
import co.siempo.phone.receivers.ScreenOffAdminReceiver;
import co.siempo.phone.utils.PermissionUtil;
import co.siempo.phone.utils.PrefSiempo;

import static android.content.Context.NOTIFICATION_SERVICE;

public class DoubleTapControlsFragment extends CoreFragment {
    private PermissionUtil permissionUtil;

    public DoubleTapControlsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    protected ViewBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentDoubletapControlBinding binding = FragmentDoubletapControlBinding.inflate(inflater, container, false);
        // Download siempo images
        if (permissionUtil == null) {
            permissionUtil = new PermissionUtil(getActivity());
        }
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        binding.toolbar.setTitle(R.string.string_doubletap_title);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        binding.switchSleep.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo
                .IS_SLEEP_ENABLE, false));
        binding.switchSleep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    checkDeviceAdminAccessGranted(false);
                } else {
                    if (checkDeviceAdminAccessGranted(true)) {
                        // ((CoreActivity) getActivity()).disableSleepMode();
                    }
                }
                PrefSiempo.getInstance(getActivity()).write(PrefSiempo.IS_SLEEP_ENABLE, isChecked);
            }
        });

        if (PrefSiempo.getInstance(getActivity()).read(PrefSiempo.IS_DND_ENABLE, false)) {
            binding.switchDnD.setChecked(true);
        } else {
            binding.switchDnD.setChecked(false);
        }

        binding.switchDnD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkNotificationAccessGranted(false);
                } else {
                    if (checkNotificationAccessGranted(true)) {
                        ((CoreActivity) requireActivity()).changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_ALL);
                    }
                }
                PrefSiempo.getInstance(getActivity()).write(PrefSiempo.IS_DND_ENABLE, isChecked);
            }
        });

        binding.relSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.switchSleep.performClick();
            }
        });

        binding.relDnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.switchDnD.performClick();
            }
        });
        return binding;
    }

    private boolean checkNotificationAccessGranted(boolean onlyAccessCheck) {
        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // If api level minimum 23
            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                if (!onlyAccessCheck) {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivityForResult(intent, 111);
                }
                return false;
            }

            return true;

        }
        return false;
    }

    private boolean checkDeviceAdminAccessGranted(boolean onlyAccessCheck) {
        DevicePolicyManager policyManager = (DevicePolicyManager) requireContext()
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(requireContext(), ScreenOffAdminReceiver.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (onlyAccessCheck) {
            return admin;
        } else {
            if (!admin) {
                // ask for device administration rights
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                ComponentName mDeviceAdmin = new ComponentName(getActivity(), ScreenOffAdminReceiver.class);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, R.string.device_admin_description);
                startActivityForResult(intent, 112);
            }
            return false;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final FragmentDoubletapControlBinding binding = requireViewBinding();
        if (requestCode == 111) {
            if (resultCode == Activity.RESULT_OK) {
                if (!checkNotificationAccessGranted(true)) {
                    Toast.makeText(getActivity(), "Please grant notification access", Toast.LENGTH_LONG).show();
                }
            } else {
                binding.switchDnD.setChecked(false);
            }
        } else if (requestCode == 112) {
            if (resultCode == Activity.RESULT_OK) {
                if (!checkDeviceAdminAccessGranted(true)) {
                    Toast.makeText(getActivity(), "Please grant device admin access", Toast.LENGTH_LONG).show();
                }
            } else {
                binding.switchSleep.setChecked(false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
