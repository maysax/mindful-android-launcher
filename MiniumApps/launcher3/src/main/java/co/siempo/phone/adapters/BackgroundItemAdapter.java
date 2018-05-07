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
        // return mItems.get(i).drawableId;
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView mImage;
        TextView imageText;
        if (v == null) {
            v = mInflater.inflate(R.layout.choose_background_grid_item, viewGroup, false);
            v.setTag(R.id.gridImage, v.findViewById(R.id.gridImage));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }
        ImageItem item = getItem(i);
        mImage = (ImageView) v.getTag(R.id.gridImage);
        imageText = (TextView) v.getTag(R.id.text);
        if (item.getName().equalsIgnoreCase("Current background")) {
            imageText.setCompoundDrawables(null, null, null, null);
        }
        if (item.getName().equalsIgnoreCase("")) {
            imageText.setVisibility(View.GONE);
        } else {
            imageText.setVisibility(View.VISIBLE);
            imageText.setText(item.name);
        }
        Glide.with(context)
                .load(Uri.fromFile(new File(item.getDrawableId().get(0).toString())))
                .into(mImage);
        return v;
    }


}
