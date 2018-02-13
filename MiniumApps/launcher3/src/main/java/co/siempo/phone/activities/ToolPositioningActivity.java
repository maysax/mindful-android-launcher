package co.siempo.phone.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.interfaces.ItemTouchHelperAdapter;
import co.siempo.phone.interfaces.ItemTouchHelperViewHolder;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.main.OnCustomerListChangedListener;
import co.siempo.phone.main.OnStartDragListener;
import co.siempo.phone.main.SimpleItemTouchHelperCallback;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PrefSiempo;

public class ToolPositioningActivity extends CoreActivity implements OnCustomerListChangedListener,
        OnStartDragListener {
    int id;
    private List<MainListItem> items = new ArrayList<>();
    private MenuAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemOffsetDecoration itemDecoration;
    private ItemTouchHelper mItemTouchHelper;
    private Parcelable mListState;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_positioning);
        id = getIntent().getExtras().getInt("ID");
        initView();
    }

    private void initView() {
        items = new ArrayList<>();
        new MainListItemLoader(this).loadItemsDefaultApp(items);
        recyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(mLayoutManager);
        if (itemDecoration != null) {
            recyclerView.removeItemDecoration(itemDecoration);
        }
        itemDecoration = new ItemOffsetDecoration(this, R.dimen.menu_grid_margin);
        recyclerView.addItemDecoration(itemDecoration);
        boolean is_icon_branding = PrefSiempo.getInstance(this).read(PrefSiempo.IS_ICON_BRANDING, true);
        mAdapter = new MenuAdapter(this, id, items, this, this, is_icon_branding);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter, this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onNoteListChanged(List<MainListItem> customers) {
        List<Long> listOfSortedCustomerId = new ArrayList<>();

        for (MainListItem customer : customers) {
            listOfSortedCustomerId.add((long) customer.getId());
        }

        Gson gson = new Gson();
        String jsonListOfSortedCustomerIds = gson.toJson(listOfSortedCustomerId);
        PrefSiempo.getInstance(this).write(PrefSiempo.SORTED_MENU, jsonListOfSortedCustomerIds);
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
        String jsonListOfSortedCustomerId = PrefSiempo.getInstance(this).read(PrefSiempo.SORTED_MENU, "");


        //check for null
        if (!jsonListOfSortedCustomerId.isEmpty()) {

            //convert onNoteListChangedJSON array into a List<Long>
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

        private final int mItemOffset;

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

    public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {
        private final Activity context;
        private final HashMap<Integer, AppMenu> map;
        boolean isHideIconBranding = true;
        int id;
        private List<MainListItem> arrayList;
        private OnStartDragListener mDragStartListener;
        private OnCustomerListChangedListener mListChangedListener;

        // Provide a suitable constructor (depends on the kind of dataset)
        MenuAdapter(Activity context, int id, List<MainListItem> arrayList, OnStartDragListener dragListener,
                    OnCustomerListChangedListener listChangedListener, boolean isHideIconBranding) {
            this.context = context;
            this.id = id;
            this.arrayList = arrayList;
            mDragStartListener = dragListener;
            mListChangedListener = listChangedListener;
            this.isHideIconBranding = isHideIconBranding;
            map = CoreApplication.getInstance().getToolsSettings();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            try {
                if (arrayList != null && arrayList.size() > 0) {
                    if (fromPosition < toPosition) {
                        for (int i = fromPosition; i < toPosition; i++) {
                            Collections.swap(arrayList, i, i + 1);
                        }
                    } else {
                        for (int i = fromPosition; i > toPosition; i--) {
                            Collections.swap(arrayList, i, i - 1);
                        }
                    }
                    mListChangedListener.onNoteListChanged(arrayList);
                    notifyItemMoved(fromPosition, toPosition);
                }
            } catch (Exception e) {
                CoreApplication.getInstance().logException(e);
                e.printStackTrace();
            }

            return true;
        }

        @Override
        public void onItemDismiss(int position) {

        }

        // Create new views (invoked by the layout manager)
        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
            // create a new view
            LayoutInflater inflater = LayoutInflater.from(
                    parent.getContext());
            View v;
            v = inflater.inflate(R.layout.list_item_grid, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ItemViewHolder(v);
        }


        @Override
        public void onBindViewHolder(final ItemViewHolder holder, int position) {
            final MainListItem item = arrayList.get(position);
            final AppMenu appMenu = map.get(item.getId());
            if (appMenu.isVisible()) {
                holder.linearLayout.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(item.getTitle())) {
                    holder.text.setText(item.getTitle());
                }
                if (isHideIconBranding) {
                    holder.icon.setImageResource(item.getDrawable());
                } else {
                    if (!appMenu.getApplicationName().equalsIgnoreCase("")) {
                        Drawable drawable = CoreApplication.getInstance().getApplicationIconFromPackageName(appMenu.getApplicationName());
                        if (drawable != null) {
                            holder.icon.setImageDrawable(drawable);
                            holder.text.setText(CoreApplication.getInstance().getApplicationNameFromPackageName(appMenu.getApplicationName()));
                        } else {
                            holder.icon.setImageResource(item.getDrawable());
                        }
                    } else {
                        holder.linearLayout.setVisibility(View.INVISIBLE);
                    }
                }
            } else {
                holder.linearLayout.setVisibility(View.INVISIBLE);
            }

            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mDragStartListener.onStartDrag(holder);
                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder implements
                ItemTouchHelperViewHolder {
            public View layout;
            // each data item is just a string in this case
            ImageView icon, imgView;
            TextView text, textDefaultApp;
            RelativeLayout relMenu;
            private LinearLayout linearLayout;

            ItemViewHolder(View v) {
                super(v);
                layout = v;
                linearLayout = v.findViewById(R.id.linearList);
                relMenu = v.findViewById(R.id.relMenu);
                text = v.findViewById(R.id.text);
                textDefaultApp = v.findViewById(R.id.textDefaultApp);
                icon = v.findViewById(R.id.icon);
                imgView = v.findViewById(R.id.imgView);
            }

            @Override
            public void onItemSelected() {
                if (relMenu.getTag().equals("list")) {
                    imgView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.darker_gray));
                } else {
                    imgView.setBackground(layout.getContext().getResources().getDrawable(R.drawable.circle_menu_selected, null));
                }
            }

            @Override
            public void onItemClear() {
                try {
                    if (relMenu.getTag().equals("list")) {
                        if (imgView.getTag() != null && imgView.getTag().equals("1")) {
                            imgView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.rectagle_menu));
                        } else {
                            imgView.setBackground(null);
                        }
                    } else {
                        if (imgView.getTag() != null && imgView.getTag().equals("1")) {
                            imgView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.circle_menu));
                        } else {
                            imgView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.circle_menu_unselected));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CoreApplication.getInstance().logException(e);
                }

            }

        }
    }
}
