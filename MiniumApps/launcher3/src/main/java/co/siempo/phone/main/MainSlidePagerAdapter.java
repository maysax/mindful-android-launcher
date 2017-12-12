package co.siempo.phone.main;

import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import co.siempo.phone.old.OldMenuFragment_;

/**
 * Created by Shahab on 2/23/2017.
 */

public class MainSlidePagerAdapter extends FragmentPagerAdapter {

    public MainSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.app.Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MainFragment_.builder().build();
            case 1:
                return OldMenuFragment_.builder().build();
            default:
                break;
        }
        return null;
    }


    @Override
    public int getCount() {
        return 2;
    }
}
