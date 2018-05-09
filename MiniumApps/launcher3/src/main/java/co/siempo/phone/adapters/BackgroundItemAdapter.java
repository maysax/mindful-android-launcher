package co.siempo.phone.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.models.ImageItem;

/**
 * Created by parth on 3/5/18.
 */

public final class BackgroundItemAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private List<ImageItem> mItems = new ArrayList<ImageItem>();
    private Context context;

    public BackgroundItemAdapter(Context context, List<ImageItem> mItems) {
        this.context = context;
        this.mItems = mItems;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public ImageItem getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.choose_background_grid_item, viewGroup, false);
            viewHolder.mImage = (ImageView) convertView.findViewById(R.id.gridImage);
            viewHolder.imageText = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ImageItem item = getItem(i);
        if (item.getName().equalsIgnoreCase(context.getString(R.string.current_background))) {
            viewHolder.imageText.setCompoundDrawables(null, null, null, null);
        }
        if (item.getName().equalsIgnoreCase("")) {
            viewHolder.imageText.setVisibility(View.GONE);
        } else {
            viewHolder.imageText.setVisibility(View.VISIBLE);
            viewHolder.imageText.setText(item.name);
        }
        Glide.with(context)
                .load(Uri.fromFile(new File(item.getDrawableId().get(0))))
                .into(viewHolder.mImage);
        return convertView;
    }

    public static class ViewHolder {
        ImageView mImage;
        TextView imageText;
    }


}
