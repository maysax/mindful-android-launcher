package co.siempo.phone.fragments;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;

import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.databinding.FragmentTempoAccountSettingsBinding;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.launcher.FakeLauncherActivity;
import co.siempo.phone.utils.PrefSiempo;

public class AccountSettingFragment extends CoreFragment {
    private long startTime = 0;

    public AccountSettingFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    protected ViewBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentTempoAccountSettingsBinding binding = FragmentTempoAccountSettingsBinding.inflate(inflater, container, false);
        binding.toolbar.setTitle(R.string.string_account_service_title);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        binding.txtPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CoreActivity) requireActivity()).loadChildFragment(new PrivacyPolicyFragment(), R.id.tempoView);
            }
        });


        if (PrefSiempo.getInstance(requireContext()).read(PrefSiempo.IS_FIREBASE_ANALYTICS_ENABLE, true)) {
            binding.swtchAnalytics.setChecked(true);
        } else {
            binding.swtchAnalytics.setChecked(false);
        }
        binding.swtchAnalytics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PrefSiempo.getInstance(requireContext()).write(PrefSiempo
                            .IS_FIREBASE_ANALYTICS_ENABLE, true);
                    FirebaseHelper.getInstance().getFirebaseAnalytics().setAnalyticsCollectionEnabled(true);
                } else {

                    FirebaseHelper.getInstance().getFirebaseAnalytics().setAnalyticsCollectionEnabled(false);
                    PrefSiempo.getInstance(requireContext()).write(PrefSiempo
                            .IS_FIREBASE_ANALYTICS_ENABLE, false);
                }
            }
        });

        binding.relUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CoreActivity) requireActivity()).loadChildFragment(new TempoUpdateEmailFragment(), R.id.tempoView);
            }
        });

        binding.relAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.swtchAnalytics.performClick();
            }
        });

        binding.relChangeHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForFirstTime();
            }
        });
        return binding;
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        FirebaseHelper.getInstance().logScreenUsageTime(this.getClass().getSimpleName(), startTime);
    }

    /**
     * This dialog used when user press exit siempo service.
     */
    private void showAlertForFirstTime() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.exiting_siempo));
        builder.setMessage(R.string.exiting_siempo_msg);
        builder.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                resetPreferredLauncherAndOpenChooser(requireContext());

            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.dialog_blue));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.dialog_blue));
    }

    /**
     * Method to reset the launcher activity and show default chooser
     *
     * @param context
     */
    private void resetPreferredLauncherAndOpenChooser(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, FakeLauncherActivity.class);
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

            Intent selector = new Intent(Intent.ACTION_MAIN);
            selector.addCategory(Intent.CATEGORY_HOME);
            selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(selector);

            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);

        } catch (Exception e) {
            e.printStackTrace();
            //In case of exception start the default setting screen
        }
    }
}
