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
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import co.siempo.phone.R;


public class PreferenceListAdapter extends ArrayAdapter<ResolveInfo> {

    private Context context;

    private List<ResolveInfo> data = null;
    int pos;
    ListView listView;

    public PreferenceListAdapter(Context context, ListView listView, List<ResolveInfo> items, int pos) {
        super(context, 0);
        this.context = context;
        this.data = items;
        this.pos = pos;
        this.listView = listView;
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
            holder.icon = convertView.findViewById(R.id.icon);
            holder.text = convertView.findViewById(R.id.text);

            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }
        ResolveInfo item = getItem(position);

        if (pos == 6) {
            if (item == null) {
                holder.text.setText(context.getResources().getText(R.string.siempo_note));
                holder.icon.setImageDrawable(context.getDrawable(R.mipmap.ic_launcher));
            } else {
                if (item != null) {
                    holder.text.setText(item.loadLabel(context.getPackageManager()));
                    holder.icon.setImageDrawable(item.loadIcon(context.getPackageManager()));
                }
            }
        } else {
            if (item != null) {
                holder.text.setText(item.loadLabel(context.getPackageManager()));
                holder.icon.setImageDrawable(item.loadIcon(context.getPackageManager()));
            }
        }


        return convertView;
    }

    private static class ItemHolder {
        ImageView icon;
        TextView text;
    }
}
