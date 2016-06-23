package minium.co.launcher2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import minium.co.core.util.ThemeUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.model.MainListItem;

/**
 * Created by Shahab on 4/29/2016.
 */
public class MainAdapter extends ArrayAdapter<MainListItem> implements Filterable {

    private Context context;

    private List<MainListItem> originalData = null;
    private List<MainListItem> filteredData = null;
    private ItemFilter filter = new ItemFilter();


    public MainAdapter(Context context, MainListItem... items) {
        super(context, 0);
        this.context = context;
        originalData = Arrays.asList(items);
        filteredData = Arrays.asList(items);
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public MainListItem getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        MainListItem item = filteredData.get(position);

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

    @Override
    public ItemFilter getFilter() {
        return filter;
    }

    public class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String searchString = constraint.toString().toLowerCase();
            FilterResults ret = new FilterResults();

            List<MainListItem> currData = originalData;
            int count = currData.size();
            List<MainListItem> buildData = new ArrayList<>();

            String filterableStr;

            for ( int i = 0; i < count; i++ ) {

                if (searchString.isEmpty()) {
                    buildData.add(currData.get(i));
                    
                } else {
                    filterableStr = currData.get(i).getText();
                    String[] splits = filterableStr.split(" ");

                    for ( String split : splits) {
                        if (split.toLowerCase().startsWith(searchString)) {
                            buildData.add(currData.get(i));
                            break;
                        }
                    }
                }
//                if (searchString.isEmpty() || filterableStr.toLowerCase().contains(searchString)) buildData.add(currData.get(i));
            }

            ret.values = buildData;
            ret.count = buildData.size();

            return ret;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null)
                filteredData = (List<MainListItem>) results.values;
            else
                filteredData = new ArrayList<>(originalData);

            notifyDataSetChanged();
        }
    }
}
