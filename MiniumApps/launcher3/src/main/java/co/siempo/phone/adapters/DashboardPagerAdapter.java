package co.siempo.phone.adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import co.siempo.phone.fragments.IntentionFragment;
import co.siempo.phone.fragments.PaneFragment;

/**
 * A simple pager adapter that represents 2 Fragment objects(Intention,Pane) in
 * sequence.
 * Created by rajeshjadi on 2/2/18.
 */

public class DashboardPagerAdapter extends FragmentStatePagerAdapter {
    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;

    public DashboardPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags = new HashMap<>();
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
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
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            String tag = fragment.getTag();
            mFragmentTags.put(position, tag);
        }
        return object;
    }

    public Fragment getFragment(int position) {
        Fragment fragment = null;
        String tag = mFragmentTags.get(position);
        if (tag != null) {
            fragment = mFragmentManager.findFragmentByTag(tag);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

