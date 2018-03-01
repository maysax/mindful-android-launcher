package co.siempo.phone.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import co.siempo.phone.R;
import co.siempo.phone.app.BitmapWorkerTask;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.interfaces.ItemTouchHelperAdapter;
import co.siempo.phone.interfaces.ItemTouchHelperViewHolder;
import co.siempo.phone.interfaces.OnToolItemListChangedListener;
import co.siempo.phone.main.OnStartDragListener;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;

/**
 * Created by rajeshjadi on 14/2/18.
 */

public class ToolPositioningAdapter extends RecyclerView.Adapter<ToolPositioningAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {
    private final Activity context;
    private final HashMap<Integer, AppMenu> map;
    private boolean isHideIconBranding = true;
    private ArrayList<MainListItem> arrayList;
    private OnStartDragListener mDragStartListener;
    private OnToolItemListChangedListener mListChangedListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ToolPositioningAdapter(Activity context, ArrayList<MainListItem> arrayList, OnStartDragListener dragListener,
                                  OnToolItemListChangedListener listChangedListener, boolean isHideIconBranding) {
        this.context = context;
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
                mListChangedListener.onToolItemListChanged(arrayList);
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
//             set the view's size, margins, paddings and layout parameters
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
                holder.text.setText(item.getTitle());
            } else {
                holder.text.setText(CoreApplication.getInstance().getApplicationNameFromPackageName(appMenu.getApplicationName()));
                Bitmap bitmap = CoreApplication.getInstance().getBitmapFromMemCache(appMenu.getApplicationName());
                if (bitmap != null) {
                    holder.icon.setImageBitmap(bitmap);
                } else {
                    BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(context, appMenu.getApplicationName());
                    CoreApplication.getInstance().includeTaskPool(bitmapWorkerTask, null);
                    holder.icon.setImageResource(item.getDrawable());
                    holder.text.setText(item.getTitle());
                }
            }
        } else {
            holder.linearLayout.setVisibility(View.INVISIBLE);
        }


        holder.linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

//            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    mDragStartListener.onStartDrag(holder);
//                    return true;
//                }
//            });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        public View layout;
        // each data item is just a string in this case
        ImageView icon, imgView, temp;
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
            temp = v.findViewById(R.id.temp);
        }

        @Override
        public void onItemSelected() {
        }

        @Override
        public void onItemClear() {
            try {
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                CoreApplication.getInstance().logException(e);
            }

        }
    }

}
