package co.siempo.phone;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import co.siempo.phone.Help.HelpFragment_;
import minium.co.core.ui.CoreActivity;


@EActivity(R.layout.activity_help)
public class HelpActivity extends CoreActivity {

    @AfterViews
    void afterViews() {
        loadFragment(HelpFragment_.builder().build(), R.id.helpView, "main");

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

}
