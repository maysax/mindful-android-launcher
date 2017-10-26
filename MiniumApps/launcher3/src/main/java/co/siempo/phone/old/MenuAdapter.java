package co.siempo.phone.old;

import android.app.Activity;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;

import java.util.Collections;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.main.ItemTouchHelperAdapter;
import co.siempo.phone.main.ItemTouchHelperViewHolder;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.main.OnCustomerListChangedListener;
import co.siempo.phone.main.OnStartDragListener;
import co.siempo.phone.model.MainListItem;
import minium.co.core.app.DroidPrefs_;


class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {
    private final Activity context;
    private List<MainListItem> arrayList;
    private boolean isGrid;
    private OnStartDragListener mDragStartListener;
    private OnCustomerListChangedListener mListChangedListener;
    private Launcher3Prefs_ prefs;
    private DroidPrefs_ droidPrefs_;
    private static RecyclerView activity_grid_view;

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
        }catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }


    // Provide a suitable constructor (depends on the kind of dataset)
    MenuAdapter(Activity context, RecyclerView activity_grid_view, Launcher3Prefs_ prefs, DroidPrefs_ droidPrefs_, List<MainListItem> arrayList, boolean isGrid, OnStartDragListener dragListener,
                OnCustomerListChangedListener listChangedListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.isGrid = isGrid;
        this.prefs = prefs;
        this.activity_grid_view = activity_grid_view;
        this.droidPrefs_ = droidPrefs_;
        mDragStartListener = dragListener;
        mListChangedListener = listChangedListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v;
        if (isGrid)
            v = inflater.inflate(R.layout.list_item_grid, parent, false);
        else
            v = inflater.inflate(R.layout.list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final MainListItem item = arrayList.get(position);
        if (item != null) {
            holder.text.setText(item.getTitle());
            holder.icon.setImageDrawable(new IconDrawable(context, item.getIcon())
                    .colorRes(R.color.text_primary)
                    .sizeDp(isGrid ? 25 : 18));
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id;
                if (item != null) {
                    id = item.getId();
                    new MainListItemLoader(context).listItemClicked(id);
                }

            }
        });

        holder.icon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mDragStartListener.onStartDrag(holder);
                return false;
            }
        });

        if (isGrid) {
            if (item != null) {
                if (item.getId() == 2 && !droidPrefs_.isCallClickedFirstTime().get()) {
                    holder.imgView.setBackground(context.getResources().getDrawable(R.drawable.circle_menu, null));
                } else if (item.getId() == 1 && !droidPrefs_.isMessageClickedFirstTime().get()) {
                    holder.imgView.setBackground(context.getResources().getDrawable(R.drawable.circle_menu, null));
                } else if (item.getId() == 16 && !droidPrefs_.isEmailClickedFirstTime().get()) {
                    holder.imgView.setBackground(context.getResources().getDrawable(R.drawable.circle_menu, null));
                } else {
                    holder.imgView.setBackground(null);
                }
            }
        } else {
            if (item != null) {
                if (item.getId() == 2 && !droidPrefs_.isCallClickedFirstTime().get()) {
                    holder.relMenu.setBackground(context.getResources().getDrawable(R.drawable.rectagle_menu, null));
                } else if (item.getId() == 1 && !droidPrefs_.isMessageClickedFirstTime().get()) {
                    holder.relMenu.setBackground(context.getResources().getDrawable(R.drawable.rectagle_menu, null));
                } else if (item.getId() == 16 && !droidPrefs_.isEmailClickedFirstTime().get()) {
                    holder.relMenu.setBackground(context.getResources().getDrawable(R.drawable.rectagle_menu, null));
                } else {
                    holder.relMenu.setBackground(null);
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        // each data item is just a string in this case
        ImageView icon,imgView;
        public View layout;
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
//            layout.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.white_100opacity));
        }

        @Override
        public void onItemClear() {
//            itemView.setBackgroundColor(0);
            try {
                activity_grid_view.getAdapter().notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
