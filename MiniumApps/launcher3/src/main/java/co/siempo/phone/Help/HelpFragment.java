package co.siempo.phone.Help;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
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
    TextView txtPrivacyPolicy;

    @ViewById
    TextView txtFaq;



    @ViewById
    TextView txtVersionValue;


    @Click
    void txtSendFeedback() {

        ((CoreActivity) getActivity()).loadChildFragment(FeedbackFragment_.builder()
                .build(), R.id.helpView);
    }

    @Click
    void txtFaq() {

        ((CoreActivity) getActivity()).loadChildFragment(FaqFragment_.builder()
                .build(), R.id.helpView);
    }

    @Click
    void txtPrivacyPolicy() {

        ((CoreActivity) getActivity()).loadChildFragment(PrivacyPolicyFragment_.builder()
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

        String version = "";
        if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.alpha))) {
            version = "Siempo version : ALPHA-" + BuildConfig.VERSION_NAME;
        } else if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.beta))) {
            version = "Siempo version : BETA-" + BuildConfig.VERSION_NAME;
        }
        txtVersionValue.setText("" + version);


    }


}
