package co.siempo.phone.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import co.siempo.phone.R;
import co.siempo.phone.adapters.PanePagerAdapter;
import co.siempo.phone.adapters.ToolsListAdapter;
import co.siempo.phone.customviews.ClearableEditText;
import co.siempo.phone.utils.PrefSiempo;
import me.relex.circleindicator.CircleIndicator;


public class PaneFragment extends Fragment implements View.OnClickListener {

    private View view;
    private LinearLayout linTopDoc;
    private CircleIndicator indicator;
    private ViewPager pagerPane;
    private PagerAdapter mPagerAdapter;
    private LinearLayout linPane;
    private LinearLayout linBottomDoc;
    private EditText edtSearchTools;
    private TextView txtTopDockDate;
    private View linSearchList;
    private ClearableEditText edtSearchListView;
    private RelativeLayout relSearchTools;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerSearchList;
    private ToolsListAdapter adapter;
    private CardView cardViewEdtSearch;
    private ArrayList<String> toolsList;
    private View blueLineView;
    private TextView txtIntentionLabelJunkPane;
    private TextView txtIntention;
    private Window mWindow;
    private int defaultStatusBarColor;
    //    private View btnClearSearch;
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
        edtSearchTools = view.findViewById(R.id.edtSearchTools);
        blueLineView = view.findViewById(R.id.blueLineView);
        cardViewEdtSearch = view.findViewById(R.id.cardViewEdtSearch);
        edtSearchListView = view.findViewById(R.id.edtSearchListView);
        relSearchTools = view.findViewById(R.id.relSearchTools);
        txtTopDockDate = view.findViewById(R.id.txtTopDockDate);
        txtIntentionLabelJunkPane = view.findViewById(R.id.txtIntentionLabelJunkPane);
        txtIntention = view.findViewById(R.id.txtIntention);
        linPane.setOnClickListener(this);
        linBottomDoc = view.findViewById(R.id.linBottomDoc);
        linSearchList = view.findViewById(R.id.linSearchList);
        recyclerSearchList = view.findViewById(R.id.recyclerView);
        linBottomDoc.setOnClickListener(this);
        indicator = view.findViewById(R.id.indicator);
        pagerPane = view.findViewById(R.id.pagerPane);

        mPagerAdapter = new PanePagerAdapter(getChildFragmentManager());
        pagerPane.setAdapter(mPagerAdapter);
        indicator.setViewPager(pagerPane);
        pagerPane.setCurrentItem(2);

        mWindow = getActivity().getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        defaultStatusBarColor = mWindow.getStatusBarColor();

        searchEditTextFocusChanged();

        //Code for Date setting

        setToolsPaneDate();

        //Code for List Setting
        setSearchToolsList();

        //Code for Page change
        setViewPagerPageChanged();


    }

    private void searchEditTextFocusChanged() {
        //Circular Edit Text
        edtSearchTools.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edtSearchListView.setVisibility(View.VISIBLE);
                    cardViewEdtSearch.setVisibility(View.VISIBLE);
                    relSearchTools.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInputFromWindow(
                            edtSearchListView.getApplicationWindowToken(),
                            InputMethodManager.SHOW_FORCED, 0);

                }
            }
        });


        //Listview edit Text
        edtSearchListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    linPane.setVisibility(View.GONE);
                    linBottomDoc.setVisibility(View.GONE);
                    blueLineView.setVisibility(View.VISIBLE);
                    linSearchList.setVisibility(View.VISIBLE);
                } else {

                    linPane.setVisibility(View.VISIBLE);
                    blueLineView.setVisibility(View.VISIBLE);
                    edtSearchListView.setVisibility(View.GONE);
                    cardViewEdtSearch.setVisibility(View.GONE);
                    relSearchTools.setVisibility(View.VISIBLE);
                    linBottomDoc.setVisibility(View.VISIBLE);
                    linSearchList.setVisibility(View.GONE);

                }
            }
        });
    }

    /**
     * Page Change Listener and modification of UI based on Page change
     */
    private void setViewPagerPageChanged() {
        pagerPane.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                //Make the junk food pane visible
                if (i == 0) {
                    linTopDoc.setBackgroundColor(getResources().getColor(R.color
                            .bg_junk_apps_top_dock));
                    txtTopDockDate.setVisibility(View.GONE);
                    edtSearchListView.setVisibility(View.GONE);
                    edtSearchTools.setVisibility(View.GONE);

                    txtIntention.setVisibility(View.VISIBLE);
                    txtIntentionLabelJunkPane.setVisibility(View.VISIBLE);

                    String strIntention = PrefSiempo.getInstance(getActivity()).read
                            (PrefSiempo.DEFAULT_INTENTION, "");
                    if (TextUtils.isEmpty(strIntention)) {
                        txtIntention.setText("You chose to hide these apps.");
                        txtIntentionLabelJunkPane.setVisibility(View.INVISIBLE);

                    } else {
                        txtIntentionLabelJunkPane.setText(getString(R.string
                                .you_ve_flag));
                        txtIntention.setText(strIntention);
                        txtIntention.setVisibility(View.VISIBLE);
                        txtIntentionLabelJunkPane.setVisibility(View.VISIBLE);
                    }


                    // finally change the color
                    mWindow.setStatusBarColor(getResources().getColor(R.color
                            .appland_blue_bright));


                }

                //Tools and Favourite Pane
                else {
                    linTopDoc.setBackground(getResources().getDrawable(R
                            .drawable.top_bar_bg));
                    txtTopDockDate.setVisibility(View.VISIBLE);
                    edtSearchListView.setVisibility(View.VISIBLE);
                    edtSearchTools.setVisibility(View.VISIBLE);
                    txtIntention.setVisibility(View.GONE);
                    txtIntentionLabelJunkPane.setVisibility(View.GONE);

                    // finally change the color
                    mWindow.setStatusBarColor(defaultStatusBarColor);


                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * Adpater settings for Tools List
     */
    private void setSearchToolsList() {


        //Code for listview adapter
        layoutManager = new LinearLayoutManager(getContext());
        recyclerSearchList.setLayoutManager(layoutManager);

        //Need to update the model
        toolsList = new ArrayList<>();
        toolsList.add("Calendar");
        toolsList.add("Calculator");
        toolsList.add("Calls");
        toolsList.add("Spotify");
        toolsList.add("Tools");
        toolsList.add("Travel");


        adapter = new ToolsListAdapter(toolsList, getContext());
        recyclerSearchList.setAdapter(adapter);
        edtSearchListView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                filter(arg0.toString());

            }
        });
    }

    /**
     * Set Date for Tools Pane
     */
    private void setToolsPaneDate() {
        Calendar c = Calendar.getInstance();
        DateFormat df = getDateInstanceWithoutYears(Locale
                .getDefault());
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

    /**
     * filter the list based on edit text value
     *
     * @param text
     */
    public void filter(String text) {
        ArrayList<String> temp = new ArrayList();
        for (String d : toolsList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //Changing the status bar default value on page change
        if (!isVisibleToUser && null != mWindow) {
            mWindow.setStatusBarColor(defaultStatusBarColor);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    public DateFormat getDateInstanceWithoutYears(Locale locale) {
        SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance
                (DateFormat.FULL, locale);
        sdf.applyPattern(sdf.toPattern().replaceAll("[^\\p{Alpha}]*y+[^\\p{Alpha}]*", ""));
        return sdf;
    }


}
