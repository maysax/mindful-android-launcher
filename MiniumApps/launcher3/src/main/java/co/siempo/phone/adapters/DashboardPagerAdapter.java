package co.siempo.phone.adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import co.siempo.phone.fragments.IntentionFragment;
import co.siempo.phone.fragments.PaneFragment;

/**
 * A simple pager adapter that represents 2 Fragment objects(Intention,Pane) in
 * sequence.
 * Created by rajeshjadi on 2/2/18.
 */

public class DashboardPagerAdapter extends FragmentStatePagerAdapter {
    public DashboardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = PaneFragment.newInstance();
                break;
            case 1:
                fragment = IntentionFragment.newInstance();
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

