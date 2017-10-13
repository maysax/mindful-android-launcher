package co.siempo.phone.old;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;

import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.model.MainListItem;

/**
 * Created by Shahab on 2/23/2017.
 */

public class PreferenceListAdapter extends ArrayAdapter<ResolveInfo> {

    private Context context;

    private List<ResolveInfo> data = null;

    public PreferenceListAdapter(Context context, List<ResolveInfo> items) {
        super(context, 0);
        this.context = context;
        this.data = items;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Nullable
    @Override
    public ResolveInfo getItem(int position) {
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

            convertView = inflater.inflate(R.layout.list_item_apps, parent, false);
            holder.icon =  convertView.findViewById(R.id.icon);
            holder.text =  convertView.findViewById(R.id.text);

            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        ResolveInfo item = getItem(position);

        if (item != null) {
            holder.text.setText(item.loadLabel(context.getPackageManager()));
            holder.icon.setImageDrawable(item.loadIcon(context.getPackageManager()));
        }

        return convertView;
    }

    private static class ItemHolder {
        ImageView icon;
        TextView text;
    }
}
