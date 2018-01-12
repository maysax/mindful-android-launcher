package co.siempo.phone.Help;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.tempo.SendMail;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;

/**
 * Created by hardik on 5/1/18.
 */

/**
 * This fragment is use to send feedback to feedback@siempo.co mail
 */

@EFragment(R.layout.fragment_feedback)
public class FeedbackFragment extends CoreFragment {

    @ViewById
    Spinner feedbackType;

    @ViewById
    Toolbar toolbar;

    String[] feedbackArr = {"Something I like", "Something I don't like", "I have a question", "I have an idea"};


    @ViewById
    TextInputLayout layout_email;

    @ViewById
    EditText edt_email;

    @Pref
    DroidPrefs_ droidPrefs_;

    @ViewById
    EditText txtMessage;

    String selectedItemText = "";


    @SystemService
    TelephonyManager telephonyManager;

    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public final static boolean isValidMessage(String msg) {
        boolean statusMessage = false;
        Log.d("hardikkamothi", "New Line ::: " + msg.contains("\n"));
        return !TextUtils.isEmpty(msg) && msg.length() >= 8 && (msg.contains(" ") || msg.contains("\n"));
    }

    private static String getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return "{" + width + "," + height + "}";
    }

    @AfterViews
    void afterViews() {
        setHasOptionsMenu(true);
        toolbar.inflateMenu(R.menu.update_email_menu);
        toolbar.getMenu().findItem(R.id.tick).setVisible(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.tick:
                        if (!TextUtils.isEmpty(edt_email.getText().toString().trim())) {
                            droidPrefs_.userEmailId().put(edt_email.getText().toString().trim());
                        }
                        try {
                            String version = "";
                            if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.alpha))) {
                                version = "ALPHA-" + BuildConfig.VERSION_NAME;
                            } else if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.beta))) {
                                version = "BETA-" + BuildConfig.VERSION_NAME;
                            }
                            String body = "User Email :" + droidPrefs_.userEmailId().get() + "\nFeedBack Type : " + selectedItemText + "\n" +
                                    "Message :" + txtMessage.getText().toString().trim() + "\n" +
                                    "Phone Data : Manufacturer - " + android.os.Build.MANUFACTURER +
                                    ", Model - " + android.os.Build.MODEL +
                                    ", OS Version - " + android.os.Build.VERSION.SDK_INT +
                                    ", Display - " + getScreenResolution(getActivity()) + "\n" +
                                    "App Data : UserID - " + telephonyManager.getDeviceId() +
                                    ", Version - " + version;

                            long currentTimeMills = System.currentTimeMillis();


                            //Creating SendMail object
                            SendMail sm = new SendMail(getActivity(), getActivity().getResources().getString(R.string.feedback_email), "Thanks for your feedback!  Siempo support ID: " + currentTimeMills, body);

                            //Executing sendmail to send email
                            sm.execute();

                            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        FragmentManager fm = getFragmentManager();
                        fm.popBackStack();

                        UIUtils.feedbackAlert(getActivity(), getResources().getString(R.string.feedback_title), getResources().getString(R.string.feedback_success_message));
                        return true;
                }
                return false;
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.feedback);
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });

        if (!TextUtils.isEmpty(droidPrefs_.userEmailId().get())) {
            layout_email.setVisibility(View.GONE);
        } else {
            layout_email.setVisibility(View.VISIBLE);
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.showSoftInput(edt_email, 0);
        }

        // Load Feedback Type
        final List<String> plantsList = new ArrayList<>(Arrays.asList(feedbackArr));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item, plantsList) {
            @Override
            public boolean isEnabled(int position) {

                return true;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.BLACK);
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feedbackType.setAdapter(spinnerArrayAdapter);

        feedbackType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                String email = "";
                // Check if email is store in database , If not get the latest value from email textbox
                if (!TextUtils.isEmpty(droidPrefs_.userEmailId().get())) {
                    email = droidPrefs_.userEmailId().get();
                } else {
                    email = edt_email.getText().toString().trim();
                }
                // Validate if fields email, Message & feedback type is filled by user or not

                if (isValidEmail(email) && isValidMessage(txtMessage.getText().toString().trim())) {
                    // Notify the selected item text
                    toolbar.getMenu().findItem(R.id.tick).setVisible(true);
                } else {
                    toolbar.getMenu().findItem(R.id.tick).setVisible(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @AfterTextChange
    void edt_email() {
        if (!TextUtils.isEmpty(edt_email.getText().toString())) {
            String val_email = edt_email.getText().toString().trim();
            boolean isValidEmail = isValidEmail(val_email);
            if (isValidEmail) {
                layout_email.setErrorEnabled(false);
            } else {
                layout_email.setError(getResources().getString(R.string.error_email));
                layout_email.setErrorEnabled(true);
            }

            toolbar.getMenu().findItem(R.id.tick).setVisible(false);
            if (isValidEmail && isValidMessage(txtMessage.getText().toString().trim())) {
                toolbar.getMenu().findItem(R.id.tick).setVisible(true);
                layout_email.setErrorEnabled(false);
            } else {
                toolbar.getMenu().findItem(R.id.tick).setVisible(false);

            }
        } else {
            layout_email.setErrorEnabled(false);
            toolbar.getMenu().findItem(R.id.tick).setVisible(false);
        }
    }

    @AfterTextChange
    void txtMessage() {
        if (!TextUtils.isEmpty(txtMessage.getText().toString().trim())) {
            String email = "";
            // Check if email is store in database , If not get the latest value from email textbox
            if (!TextUtils.isEmpty(droidPrefs_.userEmailId().get())) {
                email = droidPrefs_.userEmailId().get();
            } else {
                email = edt_email.getText().toString().trim();
            }
            // Validate if fields email, Message & feedback type is filled by user or not
            if (isValidEmail(email) && isValidMessage(txtMessage.getText().toString().trim())) {
                toolbar.getMenu().findItem(R.id.tick).setVisible(true);
            } else {
                toolbar.getMenu().findItem(R.id.tick).setVisible(false);
            }
        } else {
            toolbar.getMenu().findItem(R.id.tick).setVisible(false);
        }
    }


}
