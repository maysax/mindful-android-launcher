package co.siempo.phone.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.siempo.phone.R;
import co.siempo.phone.utils.PrefSiempo;

@EFragment(R.layout.fragment_tempo_update_email)
public class TempoUpdateEmailFragment extends CoreFragment {


    @ViewById
    Toolbar toolbar;

    @ViewById
    TextView titleActionBar;

    @ViewById
    RelativeLayout relUpdateEmail;

    @ViewById
    EditText edt_email;

    @ViewById
    TextInputLayout text_input_layout;


    public TempoUpdateEmailFragment() {
        // Required empty public constructor
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @AfterViews
    void afterViews() {
        setHasOptionsMenu(true);
        edt_email.setText("");
        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.showSoftInput(edt_email, 0);

        toolbar.inflateMenu(R.menu.update_email_menu);
        toolbar.getMenu().findItem(R.id.tick).setVisible(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.tick:
                        String val_email = edt_email.getText().toString().trim();
                        if (!PrefSiempo.getInstance(context).read(PrefSiempo
                                .USER_EMAILID, "").equals(val_email)) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.success_email), Toast.LENGTH_LONG).show();
                        }
                        PrefSiempo.getInstance(context).write(PrefSiempo
                                .USER_EMAILID, val_email);
//                        droidPrefs_.userEmailId().put(val_email);
                        hideSoftKeyboard();
                        FragmentManager fm = getFragmentManager();
                        fm.popBackStack();
                        return true;
                    default:
                        return TempoUpdateEmailFragment.super.onOptionsItemSelected(menuItem);
                }
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.string_update_email_title);
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();

                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });

        if (!TextUtils.isEmpty(PrefSiempo.getInstance(context).read(PrefSiempo.USER_EMAILID, ""))) {
            edt_email.setText(PrefSiempo.getInstance(context).read(PrefSiempo.USER_EMAILID, ""));
            edt_email.setSelection(edt_email.getText().length());
        }

        if (edt_email.getText().toString().trim().length() > 0) {
            if (isValidEmail(edt_email.getText().toString().trim())) {
                toolbar.getMenu().findItem(R.id.tick).setVisible(true);
                edt_email.setTextColor(getResources().getColor(R.color.black));
                text_input_layout.setErrorEnabled(false);
            } else {
                text_input_layout.setError(getResources().getString(R.string.feedback_email));
                text_input_layout.setErrorEnabled(true);
            }
        }
    }

    @AfterTextChange
    void edt_email() {
        if (!TextUtils.isEmpty(edt_email.getText().toString().trim())) {
            String val_email = edt_email.getText().toString().trim();
            boolean isValidEmail = isValidEmail(val_email);
            toolbar.getMenu().findItem(R.id.tick).setVisible(false);
            if (isValidEmail) {
                toolbar.getMenu().findItem(R.id.tick).setVisible(true);
                text_input_layout.setErrorEnabled(false);
            } else {
                text_input_layout.setError(getResources().getString(R.string.error_email));
                text_input_layout.setErrorEnabled(true);
            }
        } else {
            text_input_layout.setErrorEnabled(false);
        }
    }

    private void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
