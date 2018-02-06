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
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import co.siempo.phone.R;
import co.siempo.phone.adapters.PanePagerAdapter;
import co.siempo.phone.adapters.ToolsListAdapter;
import co.siempo.phone.customviews.ClearableEditText;
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
        cardViewEdtSearch = view.findViewById(R.id.cardViewEdtSearch);
        edtSearchListView = view.findViewById(R.id.edtSearchListView);
        relSearchTools = view.findViewById(R.id.relSearchTools);
        txtTopDockDate = view.findViewById(R.id.txtTopDockDate);
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
                    linSearchList.setVisibility(View.VISIBLE);
                } else {

                    linPane.setVisibility(View.VISIBLE);
                    edtSearchListView.setVisibility(View.GONE);
                    cardViewEdtSearch.setVisibility(View.GONE);
                    relSearchTools.setVisibility(View.VISIBLE);
                    linBottomDoc.setVisibility(View.VISIBLE);
                    linSearchList.setVisibility(View.GONE);

                }
            }
        });

        //Code for Date setting
        Calendar c = Calendar.getInstance();
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        txtTopDockDate.setText(df.format(c.getTime()));


        //Code for listview adapter
        layoutManager = new LinearLayoutManager(getContext());
        recyclerSearchList.setLayoutManager(layoutManager);

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
}
