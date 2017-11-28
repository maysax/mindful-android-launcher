package co.siempo.phone.old;


import android.content.Context;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.iconify.IconDrawable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.main.OnCustomerListChangedListener;
import co.siempo.phone.main.OnStartDragListener;
import co.siempo.phone.main.SimpleItemTouchHelperCallback;
import co.siempo.phone.mm.model.Utilities;
import co.siempo.phone.model.MainListItem;
import co.siempo.phone.notification.RecyclerListAdapter;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_old_menu)
public class OldMenuFragment extends CoreFragment implements OnCustomerListChangedListener,
        OnStartDragListener {

    private List<MainListItem> items = new ArrayList<>();

    @Pref
    public DroidPrefs_ prefs;

    @Pref
    public Launcher3Prefs_ launcher3Prefs_;


    @ViewById
    RecyclerView activity_grid_view;

    @ViewById
    ImageView btnListOrGrid;


    @ViewById
    ImageView icon;

    @ViewById
    CardView cardView;

    private MenuAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ItemOffsetDecoration itemDecoration;
    private ItemTouchHelper mItemTouchHelper;
    private Parcelable mListState;


    public OldMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        mListState = mLayoutManager.onSaveInstanceState();
    }

    @Override
    public void onResume() {
        super.onResume();
        items = new ArrayList<>();
        new MainListItemLoader(getActivity()).loadItems(items, this);
        if (prefs.isMenuGrid().get()) {
            items = getSampleData();
            bindAsGrid();
        } else {
            items = getSampleData();
            bindAsList();
        }

        // Listener for the grid and list icon.
        btnListOrGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.isMenuGrid().get()) {
                    items = getSampleData();
                    bindAsList();
                } else {
                    items = getSampleData();
                    bindAsGrid();
                }
            }
        });

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (ViewConfiguration.get(getActivity()).hasPermanentMenuKey()) {
            layoutParams.setMargins(UIUtils.dpToPx(getActivity(), 8), UIUtils.dpToPx(getActivity(), 8), UIUtils.dpToPx(getActivity(), 8), UIUtils.dpToPx(getActivity(), 8));
            cardView.setLayoutParams(layoutParams);
        } else {
            layoutParams.setMargins(UIUtils.dpToPx(getActivity(), 8), UIUtils.dpToPx(getActivity(), 8), UIUtils.dpToPx(getActivity(), 8), UIUtils.dpToPx(getActivity(), 54));
            cardView.setLayoutParams(layoutParams);
        }
        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    @AfterViews
    void afterViews() {


    }

    @Override
    public void onNoteListChanged(List<MainListItem> customers) {
        List<Long> listOfSortedCustomerId = new ArrayList<>();

        for (MainListItem customer : customers) {
            listOfSortedCustomerId.add((long) customer.getId());
        }

        Gson gson = new Gson();
        String jsonListOfSortedCustomerIds = gson.toJson(listOfSortedCustomerId);
        prefs.sortedMenu().put(jsonListOfSortedCustomerIds);

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private List<MainListItem> getSampleData() {

        //Get the sample data
        List<MainListItem> customerList = items;

        //create an empty array to hold the list of sorted Customers
        List<MainListItem> sortedCustomers = new ArrayList<>();

        //get the JSON array of the ordered of sorted customers
        String jsonListOfSortedCustomerId = prefs.sortedMenu().get();


        //check for null
        if (!jsonListOfSortedCustomerId.isEmpty()) {

            //convert JSON array into a List<Long>
            Gson gson = new Gson();
            List<Long> listOfSortedCustomersId = gson.fromJson(jsonListOfSortedCustomerId, new TypeToken<List<Long>>() {
            }.getType());

            //build sorted list
            if (listOfSortedCustomersId != null && listOfSortedCustomersId.size() > 0) {
                for (Long id : listOfSortedCustomersId) {
                    for (MainListItem customer : customerList) {
                        if (customer.getId() == id) {
                            sortedCustomers.add(customer);
                            customerList.remove(customer);
                            break;
                        }
                    }
                }
            }

            //if there are still customers that were not in the sorted list
            //maybe they were added after the last drag and drop
            //add them to the sorted list
            if (customerList.size() > 0) {
                sortedCustomers.addAll(customerList);
            }

            return sortedCustomers;
        } else {
            return customerList;
        }
    }

    private class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }

    /**
     * Bind View As Listing View.
     */
    private void bindAsList() {
        btnListOrGrid.setImageDrawable(new IconDrawable(getActivity(), "fa-th")
                .colorRes(R.color.text_primary)
                .sizeDp(20));
        btnListOrGrid.setVisibility(View.VISIBLE);
        mLayoutManager = new LinearLayoutManager(getActivity());
        activity_grid_view.setLayoutManager(mLayoutManager);
        if (itemDecoration != null) {
            activity_grid_view.removeItemDecoration(itemDecoration);
        }
        itemDecoration = new ItemOffsetDecoration(context, R.dimen.dp_066);
        activity_grid_view.addItemDecoration(itemDecoration);
        mAdapter = new MenuAdapter(getActivity(), activity_grid_view, launcher3Prefs_, prefs, items, false, this, this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter, OldMenuFragment.this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(activity_grid_view);
        activity_grid_view.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        prefs.isMenuGrid().put(false);

    }

    /**
     * Bind View As Grid View.
     */
    private void bindAsGrid() {
        btnListOrGrid.setImageDrawable(new IconDrawable(getActivity(), "fa-list")
                .colorRes(R.color.text_primary)
                .sizeDp(20));
        btnListOrGrid.setVisibility(View.VISIBLE);
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        activity_grid_view.setLayoutManager(mLayoutManager);
        if (itemDecoration != null) {
            activity_grid_view.removeItemDecoration(itemDecoration);
        }
        itemDecoration = new ItemOffsetDecoration(context, R.dimen.menu_grid_margin);
        activity_grid_view.addItemDecoration(itemDecoration);
        mAdapter = new MenuAdapter(getActivity(), activity_grid_view, launcher3Prefs_, prefs, items, true, this, this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter, OldMenuFragment.this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(activity_grid_view);
        activity_grid_view.setAdapter(mAdapter);
        prefs.isMenuGrid().put(true);

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            try {
                if (getActivity() != null)
                    UIUtils.hideSoftKeyboard(getActivity(), getActivity().getCurrentFocus().getWindowToken());
            } catch (Exception e) {
                Tracer.e(e, e.getMessage());
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                if (getActivity() != null)
                    UIUtils.hideSoftKeyboard(getActivity(), getActivity().getCurrentFocus().getWindowToken());
            } catch (Exception e) {
                Tracer.e(e, e.getMessage());
            }
        }
    }
}
