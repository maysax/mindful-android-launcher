package co.siempo.phone.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import co.siempo.phone.R;
import co.siempo.phone.fragments.HelpFragment;


public class HelpActivity extends CoreActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        loadFragment(new HelpFragment(), R.id.helpView, "main");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
