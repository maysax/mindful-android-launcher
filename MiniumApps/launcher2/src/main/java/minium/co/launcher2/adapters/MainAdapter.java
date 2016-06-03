package minium.co.launcher2.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;

import minium.co.core.util.ThemeUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.model.MainListItem;

/**
 * Created by Shahab on 4/29/2016.
 */
public class MainAdapter extends ArrayAdapter<MainListItem> {

    private Context context;

    public MainAdapter(Context context, MainListItem... items) {
        super(context, 0);
        this.context = context;
        addAll(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_action, parent, false);
            holder = new ViewHolder();
            holder.icon = (IconTextView) convertView.findViewById(R.id.icon);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MainListItem item = getItem(position);

        if (item != null) {
            holder.icon.setText(item.getIconName());
            holder.icon.setTextColor(ThemeUtils.getPrimaryColor(getContext()));
            holder.text.setText(item.getText());
        }

        return convertView;
    }

    static class ViewHolder {
        IconTextView icon;
        TextView text;
    }
}
