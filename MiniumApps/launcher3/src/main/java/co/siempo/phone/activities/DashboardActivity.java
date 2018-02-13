package co.siempo.phone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import co.siempo.phone.R;
import co.siempo.phone.adapters.DashboardPagerAdapter;
import co.siempo.phone.utils.PrefSiempo;

public class DashboardActivity extends CoreActivity {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private DashboardPagerAdapter mPagerAdapter;

    private int index = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        if (PrefSiempo.getInstance(this).read(PrefSiempo.IS_APP_INSTALLED_FIRSTTIME, true)) {
            PrefSiempo.getInstance(this).write(PrefSiempo.IS_APP_INSTALLED_FIRSTTIME, false);
            Intent intent = new Intent(this, JunkfoodFlaggingActivity.class);
            startActivity(intent);
        }

    }

    private void initView() {
        mPager = findViewById(R.id.pager);
        mPagerAdapter = new DashboardPagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(index == -1 ? 1 : index);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        index = mPager.getCurrentItem();
    }

    @Override
    public void onBackPressed() {
        if (mPager != null && mPager.getCurrentItem() == 0) {
            mPager.setCurrentItem(1);
        } else {
            super.onBackPressed();
        }
    }
}
