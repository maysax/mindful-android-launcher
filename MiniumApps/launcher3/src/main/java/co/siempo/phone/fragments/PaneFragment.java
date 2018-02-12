package co.siempo.phone.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import co.siempo.phone.R;
import co.siempo.phone.adapters.PanePagerAdapter;
import me.relex.circleindicator.CircleIndicator;


public class PaneFragment extends CoreFragment implements View.OnClickListener {

    private View view;
    private LinearLayout linTopDoc;
    private CircleIndicator indicator;
    private ViewPager pagerPane;
    private PanePagerAdapter mPagerAdapter;
    private LinearLayout linPane;
    private LinearLayout linBottomDoc;

    public PaneFragment() {
        // Required empty public constructor
    }

    public static PaneFragment newInstance() {
        return new PaneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pane, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        linTopDoc = view.findViewById(R.id.linTopDoc);
        linTopDoc.setOnClickListener(this);
        linPane = view.findViewById(R.id.linPane);
        linPane.setOnClickListener(this);
        linBottomDoc = view.findViewById(R.id.linBottomDoc);
        linBottomDoc.setOnClickListener(this);
        indicator = view.findViewById(R.id.indicator);
        pagerPane = view.findViewById(R.id.pagerPane);

        mPagerAdapter = new PanePagerAdapter(getChildFragmentManager());
        pagerPane.setAdapter(mPagerAdapter);
        indicator.setViewPager(pagerPane);
        pagerPane.setCurrentItem(2);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.linTopDoc:
                break;
            case R.id.pagerPane:
                break;
            case R.id.linPane:
                break;
            case R.id.linBottomDoc:
                break;
        }
    }
}
