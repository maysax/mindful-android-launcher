package co.siempo.phone.Help;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import co.siempo.phone.R;
import minium.co.core.ui.CoreFragment;

/**
 * This screen is use to display privacy policy which is load from assets folder.
 */
@EFragment(R.layout.fragment_privacy_policy)
public class PrivacyPolicyFragment extends CoreFragment {

    @ViewById
    WebView web_PrivacyPolicy;

    @ViewById
    Toolbar toolbar;


    @AfterViews
    void afterViews() {

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.privacypolicy);
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        try{
            web_PrivacyPolicy.setHorizontalScrollBarEnabled(false);
            web_PrivacyPolicy.getSettings().setJavaScriptEnabled(true);
            web_PrivacyPolicy.loadUrl("file:///android_asset/privacypolicy.htm");
            web_PrivacyPolicy.setBackgroundColor(Color.TRANSPARENT);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
