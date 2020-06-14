package co.siempo.phone.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.databinding.FragmentFeedbackBinding;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.SendMail;
import co.siempo.phone.utils.UIUtils;

/**
 * This fragment is use to send feedback to feedback@siempo.co mail
 */
public class FeedbackFragment extends CoreFragment implements Toolbar.OnMenuItemClickListener {
    private static final String[] FEEDBACK_ARR = {"Something I like", "Something I don't like", "I have a question", "I have an idea"};

    String selectedItemText = "";

    public static boolean isValidMessage(String msg) {
        boolean statusMessage = false;
        return !TextUtils.isEmpty(msg) && msg.length() >= 8 && (msg.contains(" ") || msg.contains("\n"));
    }

    private static String getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = null;
        if (wm != null) {
            display = wm.getDefaultDisplay();
        }
        DisplayMetrics metrics = new DisplayMetrics();
        if (display != null) {
            display.getMetrics(metrics);
        }
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return "{" + width + "," + height + "}";
    }

    @Nullable
    @Override
    protected ViewBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final FragmentFeedbackBinding binding = FragmentFeedbackBinding.inflate(inflater, container, false);

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        binding.toolbar.setTitle(R.string.feedback);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        binding.toolbar.inflateMenu(R.menu.update_email_menu);
        binding.toolbar.getMenu().findItem(R.id.tick).setVisible(false);
        binding.toolbar.setOnMenuItemClickListener(this);

        if (!TextUtils.isEmpty(PrefSiempo.getInstance(requireContext()).read(PrefSiempo
                .USER_EMAILID, ""))) {
            binding.layoutEmail.setVisibility(View.GONE);
        } else {
            binding.layoutEmail.setVisibility(View.VISIBLE);
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null) {
                im.showSoftInput(binding.edtEmail, 0);
            }
        }

        // Load Feedback Type
        final List<String> plantsList = new ArrayList<>(Arrays.asList(FEEDBACK_ARR));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                requireActivity(), android.R.layout.simple_spinner_dropdown_item, plantsList) {
            @Override
            public boolean isEnabled(int position) {
                return true;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.feedbackType.setAdapter(spinnerArrayAdapter);

        binding.feedbackType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                String email;
                // Check if email is store in database , If not get the latest value from email textbox
                if (!TextUtils.isEmpty(PrefSiempo.getInstance(requireContext()).read(PrefSiempo
                        .USER_EMAILID, ""))) {
                    email = PrefSiempo.getInstance(requireContext()).read(PrefSiempo
                            .USER_EMAILID, "");
                } else {
                    email = binding.edtEmail.getText().toString().trim();
                }
                // Validate if fields email, Message & feedback type is filled by user or not

                if (UIUtils.isValidEmail(email) && isValidMessage(binding.txtMessage.getText().toString().trim())) {
                    // Notify the selected item text
                    binding.toolbar.getMenu().findItem(R.id.tick).setVisible(true);
                } else {
                    binding.toolbar.getMenu().findItem(R.id.tick).setVisible(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        try {
            Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/robotocondensedregular.ttf");
            binding.edtEmail.setTypeface(myTypeface);
            binding.txtMessage.setTypeface(myTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final FragmentFeedbackBinding binding1 = requireViewBinding();

                if (!TextUtils.isEmpty(binding1.edtEmail.getText().toString())) {
                    String val_email = binding1.edtEmail.getText().toString().trim();
                    final boolean isValidEmail = UIUtils.isValidEmail(val_email);
                    if (isValidEmail) {
                        binding1.layoutEmail.setErrorEnabled(false);
                    } else {
                        binding1.layoutEmail.setError(getResources().getString(R.string.error_email));
                        binding1.layoutEmail.setErrorEnabled(true);
                    }

                    binding1.toolbar.getMenu().findItem(R.id.tick).setVisible(false);
                    if (isValidEmail && isValidMessage(binding1.txtMessage.getText().toString().trim())) {
                        binding1.toolbar.getMenu().findItem(R.id.tick).setVisible(true);
                        binding1.layoutEmail.setErrorEnabled(false);
                    } else {
                        binding1.toolbar.getMenu().findItem(R.id.tick).setVisible(false);

                    }
                } else {
                    binding1.layoutEmail.setErrorEnabled(false);
                    binding1.toolbar.getMenu().findItem(R.id.tick).setVisible(false);
                }
            }
        });

        binding.txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final FragmentFeedbackBinding binding1 = requireViewBinding();

                if (!TextUtils.isEmpty(binding1.txtMessage.getText().toString().trim())) {
                    String email;
                    // Check if email is store in database , If not get the latest value from email textbox
                    if (!TextUtils.isEmpty(PrefSiempo.getInstance(requireContext()).read(PrefSiempo
                            .USER_EMAILID, ""))) {
                        email = PrefSiempo.getInstance(requireContext()).read(PrefSiempo.USER_EMAILID, "");
                    } else {
                        email = binding1.edtEmail.getText().toString().trim();
                    }
                    // Validate if fields email, Message & feedback type is filled by user or not
                    if (UIUtils.isValidEmail(email) && isValidMessage(binding1.txtMessage.getText().toString().trim())) {
                        binding1.toolbar.getMenu().findItem(R.id.tick).setVisible(true);
                    } else {
                        binding1.toolbar.getMenu().findItem(R.id.tick).setVisible(false);
                    }
                } else {
                    binding1.toolbar.getMenu().findItem(R.id.tick).setVisible(false);
                }
            }
        });
        return binding;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final FragmentFeedbackBinding binding = requireViewBinding();

        if (item.getItemId() == R.id.tick) {
            if (!TextUtils.isEmpty(binding.edtEmail.getText().toString().trim())) {
                PrefSiempo.getInstance(requireContext()).write(PrefSiempo
                        .USER_EMAILID, binding.edtEmail.getText().toString().trim());
            }
            try {
                String version = "";
                if (BuildConfig.FLAVOR.equalsIgnoreCase(requireContext().getString(R.string.alpha))) {
                    version = "ALPHA-" + BuildConfig.VERSION_NAME;
                } else if (BuildConfig.FLAVOR.equalsIgnoreCase(requireContext().getString(R.string.beta))) {
                    version = "BETA-" + BuildConfig.VERSION_NAME;
                }


                final String body = "User Email :" + PrefSiempo.getInstance(requireContext()).read(PrefSiempo
                        .USER_EMAILID, "") + "\nFeedBack Type : " + selectedItemText + "\n" +
                        "Message :" + binding.txtMessage.getText().toString().trim() + "\n" +
                        "Phone AlarmData : Manufacturer - " + Build.MANUFACTURER +
                        ", Model - " + Build.MODEL +
                        ", OS Version - " + Build.VERSION.SDK_INT +
                        ", Display - " + getScreenResolution(getActivity()) + "\n" +
                        "App AlarmData : UserID - " + CoreApplication.getInstance().getDeviceId() +
                        ", Version - " + version;


                //Creating SendMail object
                SendMail sm = new SendMail(getActivity(), getActivity().getResources().getString(R.string.feedback_email), "Thanks for your feedback!  Siempo support ID: " + CoreApplication.getInstance().getDeviceId(), body);

                //Executing sendmail to send email
                sm.execute();

                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            getFragmentManager().popBackStack();

            UIUtils.feedbackAlert(getActivity(), getResources().getString(R.string.feedback_title), getResources().getString(R.string.feedback_success_message));
            return true;
        }
        return false;
    }
}
