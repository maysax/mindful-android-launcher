package co.minium.launcher3.old;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;

import co.minium.launcher3.R;
import co.minium.launcher3.model.MainListItem;

/**
 * Created by Shahab on 2/23/2017.
 */

public class OldMenuAdapter extends ArrayAdapter<MainListItem> {

    private Context context;

    private List<MainListItem> data = null;

    public OldMenuAdapter(Context context, List<MainListItem> items) {
        super(context, 0);
        this.context = context;
        loadData(items);
    }

    private void loadData(List<MainListItem> items) {
        this.data = items;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Nullable
    @Override
    public MainListItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ItemHolder holder;

        if (convertView == null) {
            holder = new ItemHolder();
            LayoutInflater inflater = LayoutInflater.from(context);

            convertView = inflater.inflate(R.layout.list_item, parent, false);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.text = (TextView) convertView.findViewById(R.id.text);

            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        MainListItem item = getItem(position);

        if (item != null) {
            holder.text.setText(item.getTitle());
            holder.icon.setImageDrawable(new IconDrawable(context, item.getIcon())
                    .colorRes(R.color.text_primary)
                    .sizeDp(18));

        }

        return convertView;
    }

    private static class ItemHolder {
        ImageView icon;
        TextView text;
    }
}
