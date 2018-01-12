package co.siempo.phone.main;

import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.old.OldMenuFragment_;

/**
 * Created by Shahab on 2/23/2017.
 */

public class MainSlidePagerAdapter extends FragmentPagerAdapter {
    private Context context;

    public MainSlidePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public android.app.Fragment getItem(int position) {

        if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.alpha))) {
            switch (position) {
                case 0:
                    return MainFragment_.builder().build();
                case 1:
                    return IntentionFieldFragment_.builder().build();
                case 2:
                    return OldMenuFragment_.builder().build();
                default:
                    break;
            }
        } else {
            switch (position) {
                case 0:
                    return MainFragment_.builder().build();
                case 1:
                    return OldMenuFragment_.builder().build();
                default:
                    break;
            }
        }
        return null;
    }


    @Override
    public int getCount() {
        if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.alpha)))
            return 3;
        else return 2;
    }
}
