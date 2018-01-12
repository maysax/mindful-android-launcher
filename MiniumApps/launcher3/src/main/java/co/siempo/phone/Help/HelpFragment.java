package co.siempo.phone.Help;

import android.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

/**
 * Created by hardik on 5/1/18.
 */


@EFragment(R.layout.fragment_help)
public class HelpFragment extends CoreFragment {
    @ViewById
    Toolbar toolbar;

    @ViewById
    TextView txtSendFeedback;

    @ViewById
    TextView txtVersionValue;


    @Click
    void txtSendFeedback() {

        ((CoreActivity) getActivity()).loadChildFragment(FeedbackFragment_.builder()
                .build(), R.id.helpView);
    }

    @AfterViews
    void afterViews() {

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.help);
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        String version="";
        if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.alpha))) {
            version= "Siempo version : ALPHA-" + BuildConfig.VERSION_NAME;
        } else if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.beta))) {
            version = "Siempo version : BETA-" + BuildConfig.VERSION_NAME;
        }
        txtVersionValue.setText(""+version);



    }


}
