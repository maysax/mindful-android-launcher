package minium.co.core.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.EActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * This activity will be the base activity
 * All activity of all the modules should extend this activity
 *
 * Created by shahab on 3/17/16.
 */
@EActivity
public class CoreActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
