package minium.co.core.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.androidannotations.annotations.EActivity;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.R;
import minium.co.core.helper.Validate;
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
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Load fragment by replacing all previous fragments
     * @param fragment
     */
    public void loadFragment(Fragment fragment) {
        loadFragment(fragment, R.id.mainView);
    }

    public void loadFragment(Fragment fragment, int containerViewId) {
        FragmentManager fragmentManager = getFragmentManager();
        // clear back stack
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
        FragmentTransaction t = fragmentManager.beginTransaction();
        t.replace(containerViewId, fragment, "main");
        fragmentManager.popBackStack();
        // TODO: we have to allow state loss here
        // since this function can get called from an AsyncTask which
        // could be finishing after our app has already committed state
        // and is about to get shutdown.  What we *should* do is
        // not commit anything in an AsyncTask, but that's a bigger
        // change than we want now.
        t.commitAllowingStateLoss();
    }

    /**
     * Load Fragment on top of other fragments
     * @param fragment
     */
    public void loadChildFragment(Fragment fragment) {
        Validate.notNull(fragment);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.mainView, fragment, "main")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                handleBackPress();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void genericEvent(Object event) {
        // DO NOT code here, it is a generic catch event method
    }

    @Override
    public void onBackPressed() {
        handleBackPress();
    }

    private void handleBackPress() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
