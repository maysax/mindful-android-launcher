package co.siempo.phone.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

import co.siempo.phone.R;
import co.siempo.phone.adapters.PanePagerAdapter;
import me.relex.circleindicator.CircleIndicator;


public class PaneFragment extends Fragment implements View.OnClickListener {

    private View view;
    private LinearLayout linTopDoc;
    private CircleIndicator indicator;
    private ViewPager pagerPane;
    private PagerAdapter mPagerAdapter;
    private LinearLayout linPane;
    private LinearLayout linBottomDoc;
    private EditText autoTextTopDockSearch;
    private TextView txtTopDockDate;
//    private View btnClearSearch;

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
        autoTextTopDockSearch = view.findViewById(R.id.autoTextTopDockSearch);
//        btnClearSearch = view.findViewById(R.id.btnClearSearch);
        txtTopDockDate = view.findViewById(R.id.txtTopDockDate);
        linPane.setOnClickListener(this);
        linBottomDoc = view.findViewById(R.id.linBottomDoc);
        linBottomDoc.setOnClickListener(this);
        indicator = view.findViewById(R.id.indicator);
        pagerPane = view.findViewById(R.id.pagerPane);

        mPagerAdapter = new PanePagerAdapter(getChildFragmentManager());
        pagerPane.setAdapter(mPagerAdapter);
        indicator.setViewPager(pagerPane);
        pagerPane.setCurrentItem(2);
        autoTextTopDockSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    autoTextTopDockSearch.setBackground(getResources()
                            .getDrawable(R.drawable.auto_text_rectangle));
//                    btnClearSearch.setVisibility(View.VISIBLE);
                    autoTextTopDockSearch.setElevation(15);
                } else

                {
//                    btnClearSearch.setVisibility(View.GONE);
                }
            }
        });

//        btnClearSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                autoTextTopDockSearch.clearFocus();
//                autoTextTopDockSearch.setInputType(InputType.TYPE_NULL);
//                autoTextTopDockSearch.setText("");
//            }
//        });

        Calendar c = Calendar.getInstance();
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        txtTopDockDate.setText(df.format(c.getTime()));


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
