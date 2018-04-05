package co.siempo.phone.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eyeem.chips.ChipsEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.activities.DashboardActivity;
import co.siempo.phone.activities.JunkfoodFlaggingActivity;
import co.siempo.phone.adapters.MainListAdapter;
import co.siempo.phone.adapters.PanePagerAdapter;
import co.siempo.phone.adapters.ToolsMenuAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.customviews.ItemOffsetDecoration;
import co.siempo.phone.customviews.SearchLayout;
import co.siempo.phone.event.HomePress;
import co.siempo.phone.event.NotifyBottomView;
import co.siempo.phone.event.NotifySearchRefresh;
import co.siempo.phone.event.OnBackPressedEvent;
import co.siempo.phone.event.SearchLayoutEvent;
import co.siempo.phone.event.SendSmsEvent;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.main.MainFragmentMediator;
import co.siempo.phone.main.MainListAdapterEvent;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.token.TokenCompleteType;
import co.siempo.phone.token.TokenItem;
import co.siempo.phone.token.TokenItemType;
import co.siempo.phone.token.TokenManager;
import co.siempo.phone.token.TokenParser;
import co.siempo.phone.token.TokenRouter;
import co.siempo.phone.token.TokenUpdateEvent;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import me.relex.circleindicator.CircleIndicator;

/**
 * Main class for Tools Pane, Favorites Pane and JunkFood Pane.
 * Ui is changed based on which pane the user is currently on.
 * 1. Tools Pane
 * 2. Favourites Pane
 * 3. Junkfood Pane
 */
public class PaneFragment extends CoreFragment {

    public static boolean isSearchVisable = false;
    public ViewPager pagerPane;
    public View linSearchList;
    PanePagerAdapter mPagerAdapter;
    private LinearLayout linTopDoc;
    private LinearLayout linPane;
    private LinearLayout linBottomDoc;
    private EditText edtSearchToolsRounded;
    private TextView txtTopDockDate;
    private SearchLayout searchLayout;
    private RelativeLayout relSearchTools;
    private ListView listView;
    private CardView cardViewEdtSearch;
    private View blueLineDivider;
    private TextView txtIntentionLabelJunkPane;
    private TextView txtIntention;
    private Window mWindow;
    private MainFragmentMediator mediator;
    private TokenRouter router;
    private MainListAdapter adapter;
    private TokenParser parser;
    private RecyclerView recyclerViewBottomDoc;
    private List<MainListItem> items = new ArrayList<>();
    private ToolsMenuAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemOffsetDecoration itemDecoration;
    private ChipsEditText chipsEditText;
    private ImageView imageClear;
    private View rootView;
    private CircleIndicator indicator;

    public PaneFragment() {
        // Required empty public constructor
    }

    public static PaneFragment newInstance() {
        return new PaneFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_pane, container, false);
        Log.d("Test", "P1");
        context = (CoreActivity) getActivity();
        getColorOfStatusBar();
        initView(rootView);
        changeColorOfStatusBar();

        mediator = new MainFragmentMediator(PaneFragment.this);
        mediator.loadData();

        bindView();
        Log.d("Test", "P2");
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        UIUtils.hideSoftKeyboard(getActivity(), getActivity().getWindow().getDecorView().getWindowToken());

    }

    @Override
    public void onResume() {
        super.onResume();
        pagerPane.setAlpha(1);
        if (DashboardActivity.currentIndexPaneFragment == 0 && DashboardActivity.isJunkFoodOpen) {
            DashboardActivity.currentIndexPaneFragment = 1;
            DashboardActivity.isJunkFoodOpen = false;
            pagerPane.setCurrentItem(DashboardActivity.currentIndexPaneFragment, true);
        }
        if (DashboardActivity.currentIndexDashboard == 1) {
            if (DashboardActivity.currentIndexPaneFragment == 0) {
                Log.d("Firebase", "Junkfood Start");
                DashboardActivity.startTime = System.currentTimeMillis();
            } else if (DashboardActivity.currentIndexPaneFragment == 1) {
                Log.d("Firebase", "Favorite Start");
                DashboardActivity.startTime = System.currentTimeMillis();
            } else if (DashboardActivity.currentIndexPaneFragment == 2) {
                Log.d("Firebase", "Tools Start");
                DashboardActivity.startTime = System.currentTimeMillis();
            }
        }
        setToolsPaneDate();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //Changing the status bar default value on page change from dashboard
        // to direct Junk Food pane
        if (isVisibleToUser && null != mWindow && pagerPane != null && pagerPane.getCurrentItem() == 0) {
            mWindow.setStatusBarColor(getResources().getColor(R.color
                    .appland_blue_bright));
        } else {
            if (null != mWindow) {
                mWindow.setStatusBarColor(DashboardActivity.defaultStatusBarColor);
            }
        }
        if (!isVisibleToUser && null != imageClear && linSearchList != null &&
                linSearchList.getVisibility() == View.VISIBLE) {
            //Perform click in order to set it when user moves from search
            // pane to DashboardActivity and comes back so as to hide the list
            if (imageClear != null) imageClear.performClick();
            linSearchList.setVisibility(View.GONE);
            if (linPane != null) linPane.setAlpha(1);
        }

        //Added as part of SSA-1332 , when user clicks home button on empty
        // junk food app and swipes back from Intention , empty screen was
        // showing , but now flagging screen will open
        if (isVisibleToUser && pagerPane != null && pagerPane.getCurrentItem() == 0) {
            if (PrefSiempo.getInstance(getActivity()).read(PrefSiempo.JUNKFOOD_APPS, new HashSet<String>()).size() == 0) {
                //Applied for smooth transition
                Intent intent = new Intent(getActivity(), JunkfoodFlaggingActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R
                        .anim.fade_in_junk, R.anim.fade_out_junk);
                DashboardActivity.currentIndexPaneFragment = 0;
            }
        }


    }

    public void loadView() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediator != null && mediator.getItems() != null) {
                        adapter = new MainListAdapter(getActivity(), mediator.getItems());
                        listView.setAdapter(adapter);
                        adapter.getFilter().filter(TokenManager.getInstance().getCurrent().getTitle());
                    }
                }
            });
        }
    }

    private void changeColorOfStatusBar() {
        if (pagerPane != null && pagerPane.getCurrentItem() == 0 && isVisible
                () && DashboardActivity.currentIndexDashboard == 0) {
            mWindow.setStatusBarColor(getResources().getColor(R.color
                    .appland_blue_bright));
        } else {
            mWindow.setStatusBarColor(DashboardActivity.defaultStatusBarColor);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MainThread)
    public void onEvent(NotifyBottomView notifyBottomView) {
        if (notifyBottomView != null) {
            if (notifyBottomView.isNotify()) {
                items = CoreApplication.getInstance().getToolBottomItemsList();
                mAdapter = new ToolsMenuAdapter(getActivity(), CoreApplication.getInstance().isHideIconBranding(), true, items);
                recyclerViewBottomDoc.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                EventBus.getDefault().removeStickyEvent(notifyBottomView);
            }
        }
    }


    private void initView(View view) {
        linTopDoc = view.findViewById(R.id.linTopDoc);
        linPane = view.findViewById(R.id.linPane);
        recyclerViewBottomDoc = rootView.findViewById(R.id.recyclerViewBottomDoc);
        edtSearchToolsRounded = view.findViewById(R.id.edtSearchTools);
        blueLineDivider = view.findViewById(R.id.blueLineView);
        cardViewEdtSearch = view.findViewById(R.id.cardViewEdtSearch);
        searchLayout = view.findViewById(R.id.edtSearchListView);
        relSearchTools = view.findViewById(R.id.relSearchTools);
        txtTopDockDate = view.findViewById(R.id.txtTopDockDate);
        txtIntentionLabelJunkPane = view.findViewById(R.id.txtIntentionLabelJunkPane);
        txtIntention = view.findViewById(R.id.txtIntention);
        linBottomDoc = view.findViewById(R.id.linBottomDoc);
        linSearchList = view.findViewById(R.id.linSearchList);
        listView = view.findViewById(R.id.listView);
        indicator = view.findViewById(R.id.indicator);
        pagerPane = view.findViewById(R.id.pagerPane);
        pagerPane.setAlpha(1);
        chipsEditText = searchLayout.getTxtSearchBox();
        imageClear = searchLayout.getBtnClear();
        edtSearchToolsRounded.clearFocus();
        chipsEditText.clearFocus();
    }

    private void bindView() {
        bindViewPager();

        bindBottomDock();


        bindSearchView();
    }

    private void bindViewPager() {
        mPagerAdapter = new PanePagerAdapter(getChildFragmentManager());
        pagerPane.setAdapter(mPagerAdapter);
        indicator.setViewPager(pagerPane);
        pagerPane.setOffscreenPageLimit(3);
        if (DashboardActivity.isJunkFoodOpen) {
            DashboardActivity.currentIndexPaneFragment = 1;
            DashboardActivity.isJunkFoodOpen = false;
        }
        if (DashboardActivity.currentIndexPaneFragment == -1) {
            DashboardActivity.currentIndexPaneFragment = 2;
            DashboardActivity.startTime = System.currentTimeMillis();
        }
        //Code for Page change
        pagerPane.setCurrentItem(DashboardActivity.currentIndexPaneFragment);
        pagerPane.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i1) {
                edtSearchToolsRounded.clearFocus();
                chipsEditText.clearFocus();
            }


            @Override
            public void onPageSelected(int i) {

                if (i == 0) {
                      /* Junkfood Pane */
                    if (PrefSiempo.getInstance(getActivity()).read(PrefSiempo.JUNKFOOD_APPS, new HashSet<String>()).size() == 0) {
                        //Applied for smooth transition
                        pagerPane.setAlpha(0);
                        Intent intent = new Intent(getActivity(), JunkfoodFlaggingActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R
                                .anim.fade_in_junk, R.anim.fade_out_junk);
                    }
                    UIUtils.hideSoftKeyboard(getActivity(), getActivity().getWindow().getDecorView().getWindowToken());

                    if (linSearchList.getVisibility() == View.VISIBLE) {
                        linSearchList.setVisibility(View.GONE);
                        linPane.setAlpha(1);
                    }
                    if (linPane.getVisibility() == View.GONE)
                        linPane.setVisibility(View.VISIBLE);
                    if (linBottomDoc.getVisibility() == View.GONE)
                        linBottomDoc.setVisibility(View.VISIBLE);
                    if (blueLineDivider.getVisibility() == View.GONE)
                        blueLineDivider.setVisibility(View.VISIBLE);
                    if (searchLayout.getVisibility() == View.VISIBLE)
                        searchLayout.setVisibility(View.GONE);
                    if (cardViewEdtSearch.getVisibility() == View.VISIBLE)
                        cardViewEdtSearch.setVisibility(View.GONE);
                    if (relSearchTools.getVisibility() == View.GONE)
                        relSearchTools.setVisibility(View.VISIBLE);
                    isSearchVisable = false;
                    imageClear.setVisibility(View.VISIBLE);
                    if (searchLayout != null && chipsEditText != null && chipsEditText.getText().toString().length() > 0) {
                        if (linSearchList.getVisibility() == View.VISIBLE)
                            searchLayout.txtSearchBox.setText("");
                    }

                    junkFoodAppPane();
                    mWindow.setStatusBarColor(getResources().getColor(R.color
                            .appland_blue_bright));
                    linTopDoc.setElevation(20);

                } else {
                    /* Tools and Favourite Pane */
                    linTopDoc.setElevation(0);
                    linTopDoc.setBackground(getResources().getDrawable(R
                            .drawable.top_bar_bg));
                    txtTopDockDate.setVisibility(View.VISIBLE);
                    edtSearchToolsRounded.setVisibility(View.VISIBLE);
                    txtIntention.setVisibility(View.GONE);
                    txtIntentionLabelJunkPane.setVisibility(View.GONE);

                    // finally change the color
                    mWindow.setStatusBarColor(DashboardActivity.defaultStatusBarColor);
                }

                //Indicator to be set here so that when coming from another
                // application, the sliding dots retain the shape as previous
                indicator.setViewPager(pagerPane);

                if (DashboardActivity.currentIndexPaneFragment == 0 && i == 1) {
                    Log.d("Firebase ", "JunkFood End");
                    Log.d("Firebase ", "Favorite Start");
                    FirebaseHelper.getInstance().logScreenUsageTime(JunkFoodPaneFragment.class.getSimpleName(), DashboardActivity.startTime);
                    DashboardActivity.startTime = System.currentTimeMillis();
                } else if (DashboardActivity.currentIndexPaneFragment == 1 && i == 2) {
                    Log.d("Firebase ", "Favorite End");
                    Log.d("Firebase ", "Tools Start");
                    FirebaseHelper.getInstance().logScreenUsageTime(FavoritePaneFragment.class.getSimpleName(), DashboardActivity.startTime);
                    DashboardActivity.startTime = System.currentTimeMillis();
                } else if (DashboardActivity.currentIndexPaneFragment == 2 && i == 1) {
                    Log.d("Firebase ", "Tools End");
                    Log.d("Firebase ", "Favorite Start");
                    FirebaseHelper.getInstance().logScreenUsageTime(ToolsPaneFragment.class.getSimpleName(), DashboardActivity.startTime);
                    DashboardActivity.startTime = System.currentTimeMillis();
                } else if (DashboardActivity.currentIndexPaneFragment == 1 && i == 0) {
                    Log.d("Firebase ", "Favorite End");
                    Log.d("Firebase ", "JunkFood Start");
                    FirebaseHelper.getInstance().logScreenUsageTime(FavoritePaneFragment.class.getSimpleName(), DashboardActivity.startTime);
                    DashboardActivity.startTime = System.currentTimeMillis();
                }
                DashboardActivity.currentIndexPaneFragment = i;
                //Make the junk food pane visible
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        if (DashboardActivity.currentIndexPaneFragment == 0) {
            junkFoodAppPane();
        }
    }

    private void bindBottomDock() {
//        ArrayList<MainListItem> itemsLocal = new ArrayList<>();
//        new MainListItemLoader(getActivity()).loadItemsDefaultApp(itemsLocal);
//        itemsLocal = PackageUtil.getToolsMenuData(getActivity(), itemsLocal);
//        Set<Integer> list = new HashSet<>();
//
//        for (Map.Entry<Integer, AppMenu> entry : CoreApplication.getInstance().getToolsSettings().entrySet()) {
//            if (entry.getValue().isBottomDoc()) {
//                list.add(entry.getKey());
//            }
//        }
//
//        for (MainListItem mainListItem : itemsLocal) {
//            if (list.contains(mainListItem.getId())) {
//                items.add(mainListItem);
//            }
//        }


        mLayoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerViewBottomDoc.setLayoutManager(mLayoutManager);
        if (itemDecoration != null) {
            recyclerViewBottomDoc.removeItemDecoration(itemDecoration);
        }
        itemDecoration = new ItemOffsetDecoration(context, R.dimen.dp_10);
        recyclerViewBottomDoc.addItemDecoration(itemDecoration);
        items = CoreApplication.getInstance().getToolBottomItemsList();
        mAdapter = new ToolsMenuAdapter(getActivity(), CoreApplication.getInstance().isHideIconBranding(), true, items);
        recyclerViewBottomDoc.setAdapter(mAdapter);
    }

    private void getColorOfStatusBar() {
        mWindow = getActivity().getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        if (null != mWindow) {
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        }
    }

    public MainListAdapter getAdapter() {
        return adapter;
    }

    private void bindSearchView() {
//        //Circular Edit Text
        router = new TokenRouter();
        parser = new TokenParser(router);
        loadView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (router != null && searchLayout != null && searchLayout.getTxtSearchBox() != null) {
                    mediator.listItemClicked(router, position, searchLayout.getTxtSearchBox().getStrText());
//                    imageClear.performClick();
                }
            }
        });
        edtSearchToolsRounded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchLayout.getVisibility() == View.GONE) {
                    hidePaneAndBottomView(context);
                    listView.setAdapter(adapter);
                    imageClear.setVisibility(View.VISIBLE);
                    blueLineDivider.setVisibility(View.GONE);
                    searchLayout.setVisibility(View.VISIBLE);
                    searchLayout.getTxtSearchBox().requestFocus();
                    cardViewEdtSearch.setVisibility(View.VISIBLE);
                    relSearchTools.setVisibility(View.GONE);
                    UIUtils.showKeyboard(chipsEditText);
                    if (adapter != null) {
                        adapter.getFilter().filter("");
                    }

                } else {
                    UIUtils.hideSoftKeyboard(getActivity(), getActivity().getWindow().getDecorView().getWindowToken());
                    showPaneAndBottomView(context);
                    blueLineDivider.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.GONE);
                    cardViewEdtSearch.setVisibility(View.GONE);
                    relSearchTools.setVisibility(View.VISIBLE);
                    imageClear.setVisibility(View.GONE);

                }
            }
        });

        imageClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtSearchToolsRounded != null) {
                    edtSearchToolsRounded.performClick();
                }
                if (searchLayout != null && chipsEditText != null && chipsEditText.getText().toString().length() > 0) {
                    searchLayout.txtSearchBox.setText("");
                }

            }
        });

    }

    private void junkFoodAppPane() {
        linTopDoc.setBackgroundColor(getResources().getColor(R.color
                .bg_junk_apps_top_dock));
        txtTopDockDate.setVisibility(View.GONE);
        searchLayout.setVisibility(View.GONE);
        edtSearchToolsRounded.setVisibility(View.GONE);

        txtIntention.setVisibility(View.VISIBLE);
        txtIntentionLabelJunkPane.setVisibility(View.VISIBLE);

        String strIntention = PrefSiempo.getInstance(getActivity()).read
                (PrefSiempo.DEFAULT_INTENTION, "");

        //If Intentions are enabled and intention field is not empty then show
        //it in Junk food Top dock else not
        if (!TextUtils.isEmpty(strIntention) && !PrefSiempo.getInstance
                (context).read(PrefSiempo
                .IS_INTENTION_ENABLE, false)) {
            txtIntentionLabelJunkPane.setText(getString(R.string
                    .you_ve_flag));
            txtIntention.setText(strIntention);
            txtIntention.setVisibility(View.VISIBLE);
            txtIntentionLabelJunkPane.setVisibility(View.VISIBLE);

        } else {
            txtIntention.setText("You flagged these apps to use them less.");
            txtIntentionLabelJunkPane.setVisibility(View.INVISIBLE);
        }


    }

    /**
     * Set Date for Tools Pane
     */
    private void setToolsPaneDate() {
        Calendar c = Calendar.getInstance();
        DateFormat df = getDateInstanceWithoutYears(Locale.getDefault());
        if (getActivity() != null && txtTopDockDate != null) {
            txtTopDockDate.setText(df.format(c.getTime()));
            txtTopDockDate.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (null != imageClear && imageClear.getVisibility() == View
                            .VISIBLE && searchLayout.getVisibility() == View.VISIBLE) {
                        imageClear.performClick();
                    }
                    return true;
                }
            });
        }

    }

    public DateFormat getDateInstanceWithoutYears(Locale locale) {


        SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance
                (DateFormat.FULL, locale);
        try {
            sdf.applyPattern(sdf.toPattern().replaceAll(
                    "([^\\p{Alpha}']|('[\\p{Alpha}]+'))*y+([^\\p{Alpha}']|('[\\p{Alpha}]+'))*",
                    ""));
        } catch (Exception e) {
            Tracer.d("Exception  :: " + e.toString());
        }

        return sdf;
    }

    public TokenManager getManager() {
        return TokenManager.getInstance();
    }

    public void setCurrentPage(int viewPagerPage) {
        pagerPane.setCurrentItem(viewPagerPage);

    }

    public void hidePaneAndBottomView(final Context context) {
        linPane.setVisibility(View.GONE);
        linBottomDoc.setVisibility(View.GONE);
        searchListVisible(context);
    }

    public void showPaneAndBottomView(final Context context) {
        linSearchList.setVisibility(View.GONE);
        linPane.setVisibility(View.VISIBLE);
        linBottomDoc.setVisibility(View.VISIBLE);
        isSearchVisable = false;
        FirebaseHelper.getInstance().logScreenUsageTime(FirebaseHelper.SEARCH_PANE, DashboardActivity.startTime);
        DashboardActivity.startTime = System.currentTimeMillis();
    }

    private void showViews(boolean wantToShow, ArrayList<View> viewArrayList) {
        for (View view : viewArrayList) {
            if (wantToShow) {
                if (view.getVisibility() == View.GONE) {
                    view.setVisibility(View.VISIBLE);
                }
            } else {
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    public void searchListVisible(Context context) {
        linSearchList.setVisibility(View.VISIBLE);
        isSearchVisable = true;
        imageClear.setVisibility(View.VISIBLE);
        if (DashboardActivity.currentIndexDashboard == 0) {
            if (DashboardActivity.currentIndexPaneFragment == 1) {
                FirebaseHelper.getInstance().logScreenUsageTime("FavoritePaneFragment", DashboardActivity.startTime);
            } else if (DashboardActivity.currentIndexPaneFragment == 2) {
                FirebaseHelper.getInstance().logScreenUsageTime("ToolsPaneFragment", DashboardActivity.startTime);
            }
        }
        DashboardActivity.startTime = System.currentTimeMillis();

    }

    private void logSearchViewShow() {
        DashboardActivity.startTime = System.currentTimeMillis();
    }

    private void logSearchViewEnd() {
        if (DashboardActivity.currentIndexDashboard == 0) {
            if (DashboardActivity.currentIndexPaneFragment == 1) {
                Log.d("Firebase", "Favorite Start");
                FirebaseHelper.getInstance().logScreenUsageTime("SearchPaneFragment", DashboardActivity.startTime);
                DashboardActivity.startTime = System.currentTimeMillis();
            } else if (DashboardActivity.currentIndexPaneFragment == 2) {
                Log.d("Firebase", "Tools Start");
                FirebaseHelper.getInstance().logScreenUsageTime("SearchPaneFragment", DashboardActivity.startTime);
                DashboardActivity.startTime = System.currentTimeMillis();
            }
        }
    }


    //Commented this as part of SSA-1476, as app update was getting called
    // multiple times creating the issue of recent apps being deleted and
    // added many times. Moreover, app update is being captured in this class
    // by NotifySearchRefresh Event hence removing the below logic
//    @Subscribe
//    public void appInstalledEvent(AppInstalledEvent appInstalledEvent) {
//        if (appInstalledEvent.isAppInstalledSuccessfully()) {
//            if (mediator != null) {
//                mediator.loadData();
//            }
//        }
//    }

    @Subscribe
    public void onBackPressedEvent(OnBackPressedEvent onBackPressedEvent) {
        if (onBackPressedEvent.isBackPressed()) {
            if (linSearchList.getVisibility() == View.VISIBLE) {
                UIUtils.hideSoftKeyboard(getActivity(), getActivity().getWindow().getDecorView().getWindowToken());
                blueLineDivider.setVisibility(View.VISIBLE);
                searchLayout.setVisibility(View.GONE);
                cardViewEdtSearch.setVisibility(View.GONE);
                relSearchTools.setVisibility(View.VISIBLE);
                linSearchList.setVisibility(View.GONE);
                linBottomDoc.setVisibility(View.VISIBLE);
                linPane.setVisibility(View.VISIBLE);
                isSearchVisable = false;
                linPane.setAlpha(1);
                imageClear.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe
    public void filterDataMainAdapter(MainListAdapterEvent event) {
        try {

            List<MainListItem> mainListItems = event.getData();
            if (null != mainListItems && mainListItems.size() > 0 && mainListItems
                    .size() <= 3) {
                listView.setOnTouchListener(new OnSwipeTouchListener(context,
                        listView));
            } else {
                listView.setOnTouchListener(null);
            }


        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }
    }

    @Subscribe
    public void searchLayoutEvent(final SearchLayoutEvent event) {
        try {
            if (getActivity() != null) {

                if (event.getString().equalsIgnoreCase("") && searchLayout
                        .getVisibility() == View.GONE) {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            parser.parse(event.getString());
                            if (adapter != null) {
                                adapter.getFilter().filter(TokenManager.getInstance().getCurrent().getTitle());
                            }
                        }
                    }, 40);

                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            parser.parse(event.getString());
                            if (adapter != null) {
                                adapter.getFilter().filter(TokenManager.getInstance().getCurrent().getTitle());
                            }

                            //Cancelling the result of previous async task
                            // for empty token in case of Edit Text string
                            // not being empty
                            if (!event.getString().equalsIgnoreCase("")) {
                                mediator.cancelAsync();
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }
    }

    @Subscribe
    public void sendSmsEvent(SendSmsEvent event) {
        if (event.isClearList()) {
            mediator.resetData();
            imageClear.performClick();
        }
    }

    @Subscribe
    public void homePress(HomePress event) {
        if (event != null) {
            pagerPane.setCurrentItem(event.getCurrentIndexPaneFragment(), true);
        }
    }

    @Subscribe
    public void tokenManagerEvent(TokenUpdateEvent event) {
        try {
            final TokenItem current = TokenManager.getInstance().getCurrent();
            if (current != null) {
                if (current.getItemType() == TokenItemType.END_OP) {
                    mediator.defaultData();
                } else if (current.getItemType() == TokenItemType.CONTACT) {
                    if (current.getCompleteType() == TokenCompleteType.HALF) {
                        mediator.contactNumberPicker(Integer.parseInt(current.getExtra1()));
                    } else {
                        mediator.contactPicker();
                    }
                } else if (current.getItemType() == TokenItemType.DATA) {
                    if (TokenManager.getInstance().get(0).getItemType() == TokenItemType.DATA) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //If the task of async is running, then no
                                // need to run its another instance. This
                                // will happen in case of list item click
                                // where already the data is being reset and
                                // after it the empty token ("") is being
                                // called. Hence in order to prevent this,
                                // empty token will be called only in case if
                                // no previous async task of similar type is
                                // running.
                                if (!mediator.getRunningStatus()) {
                                    mediator.resetData();
                                }
                                if (adapter != null)
                                    adapter.getFilter().filter(current.getTitle());
                            }
                        });

                    } else {
                        if (current.getTitle().trim().isEmpty()) {
                            if (adapter != null) {
                                mediator.loadDefaultData();
                                adapter.getFilter().filter("^");
                            }
                        } else {
                            if (adapter != null) {
                                mediator.loadDefaultData();
                                adapter.getFilter().filter(current.getTitle());
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MainThread)
    public void onEvent(NotifySearchRefresh notifySearchRefresh) {
        if (notifySearchRefresh != null && notifySearchRefresh.isNotify()) {
            mediator = new MainFragmentMediator(PaneFragment.this);
            mediator.loadData();

            if (adapter != null) {
                adapter.getFilter().filter("");
            }
            EventBus.getDefault().removeStickyEvent(notifySearchRefresh);
        }

    }

    private class OnSwipeTouchListener implements View.OnTouchListener {

        ListView list;
        private GestureDetector gestureDetector;
        private Context context;

        public OnSwipeTouchListener(Context ctx, ListView list) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
            context = ctx;
            this.list = list;
        }

        public OnSwipeTouchListener() {
            super();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            //Added as a part of SSA-1475, in case if GestureDetector is not
            // initialised and null, it will be assigned and then its event
            // will be captured
            if (null != gestureDetector) {
                return gestureDetector.onTouchEvent(event);
            } else {
                gestureDetector = new GestureDetector(context, new GestureListener
                        ());
                return gestureDetector.onTouchEvent(event);
            }
        }

        void onSwipeRight(int pos) {
            //Do what you want after swiping left to right
            if (pagerPane != null) {
                pagerPane.setCurrentItem(0);
            }

        }

        void onSwipeLeft(int pos) {

            //Do what you want after swiping right to left
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            private int getPostion(MotionEvent e1) {
                return list.pointToPosition((int) e1.getX(), (int) e1.getY());
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceX) > Math.abs(distanceY)
                        && Math.abs(distanceX) > SWIPE_THRESHOLD
                        && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0)
                        onSwipeRight(getPostion(e1));
                    else
                        onSwipeLeft(getPostion(e1));
                    return true;
                }
                return false;
            }

        }
    }

}
