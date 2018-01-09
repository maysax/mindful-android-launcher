package co.siempo.phone.tempo;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.w3c.dom.Text;

import javax.net.ssl.HandshakeCompletedListener;

import co.siempo.phone.R;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

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

    @Pref
    DroidPrefs_ droidPrefs_;
    public TempoUpdateEmailFragment() {
        // Required empty public constructor
    }


    @AfterViews
    void afterViews() {
        setHasOptionsMenu(true);
        edt_email.setText("");
        InputMethodManager im = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.showSoftInput(edt_email, 0);

        toolbar.inflateMenu(R.menu.update_email_menu);
        toolbar.getMenu().findItem(R.id.tick).setVisible(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.tick:
                        String val_email=edt_email.getText().toString().trim();
                        droidPrefs_.userEmailId().put(val_email);
                        hideSoftKeyboard();
                        Toast.makeText(getActivity(),getResources().getString(R.string.success_email),Toast.LENGTH_LONG).show();
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
        toolbar.setElevation(100);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();

                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });
        if(!TextUtils.isEmpty(droidPrefs_.userEmailId().get())){
            edt_email.setText(droidPrefs_.userEmailId().get());
            edt_email.setSelection(edt_email.getText().length());
        }
    }


    @AfterTextChange
    void edt_email(){
        if(!TextUtils.isEmpty(edt_email.getText().toString())){
            String val_email=edt_email.getText().toString().trim();
            boolean isValidEmail= isValidEmail(val_email);

            toolbar.getMenu().findItem(R.id.tick).setVisible(false);
            if(isValidEmail){
                toolbar.getMenu().findItem(R.id.tick).setVisible(true);
            }
        }
    }



    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }
}
