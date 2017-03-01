package co.minium.launcher3.main;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

import co.minium.launcher3.R;
import co.minium.launcher3.model.ContactListItem;
import co.minium.launcher3.model.MainListItem;
import co.minium.launcher3.model.MainListItemType;
import de.greenrobot.event.EventBus;

/**
 * Created by Shahab on 2/16/2017.
 */

public class MainListAdapter extends ArrayAdapter<MainListItem> {

    private Context context;

    private List<MainListItem> originalData = null;
    private List<MainListItem> filteredData = null;
    private ItemFilter filter = new ItemFilter();

    public MainListAdapter(Context context, List<MainListItem> items) {
        super(context, 0);
        this.context = context;
        loadData(items);
    }

    public void loadData(List<MainListItem> items) {
        originalData = items;
        filteredData = items;
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
    public int getViewTypeCount() {
        return MainListItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getItemType().ordinal();
    }

    public MainListItem getItemById(MainListItemType type, int id) {

        for (MainListItem item : filteredData) {
            switch (type) {
                case CONTACT:
                    break;
                case ACTION:
                    if (item.getId() == id) return item;
                    break;
                case DEFAULT:
                    if (item.getId() == id) return item;
                    break;
            }
        }

        return null;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        MainListItemType itemViewType = MainListItemType.values() [getItemViewType(position)];

        switch (itemViewType) {
            case CONTACT:
                convertView = getContactItemView(position, convertView, parent);
                break;
            case ACTION:
            case DEFAULT:
                convertView = getActionItemView(position, convertView, parent);
        }

        return convertView;
    }

    private static class ActionViewHolder {
        ImageView icon;
        TextView text;
    }

    private static class ContactViewHolder {
        ImageView icon;
        TextView text;
        TextView txtNumber;
    }

    private View getContactItemView(int position, View view, ViewGroup parent) {
        ContactViewHolder holder;

        if (view == null) {
            holder = new ContactViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.list_item_contacts, parent, false);

            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.txtNumber = (TextView) view.findViewById(R.id.txtNumber);
            holder.text = (TextView) view.findViewById(R.id.text);
            view.setTag(holder);

        } else {
            holder = (ContactViewHolder) view.getTag();
        }

        ContactListItem item = (ContactListItem) getItem(position);

        if (item != null) {
            holder.text.setText(item.getContactName());

            if (item.getImageUri() != null) {
                Glide.with(context)
                        .load(Uri.parse(item.getImageUri()))
                        .placeholder(R.drawable.placeholder_blank_contact)
                        .into(holder.icon);
            }

            if (item.hasMultipleNumber()) {
                holder.txtNumber.setText(context.getString(R.string.label_multiple_numbers));
            } else {
                holder.txtNumber.setText(item.getNumber().getNumber());
            }
        }

        return view;
    }

    private View getActionItemView(int position, View view, ViewGroup parent) {
        ActionViewHolder holder;

        if (view == null) {
            holder = new ActionViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.list_item, parent, false);

            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.text = (TextView) view.findViewById(R.id.text);
            view.setTag(holder);
        } else {
            holder = (ActionViewHolder) view.getTag();
        }

        MainListItem item = getItem(position);

        if (item != null) {
            holder.icon.setImageResource(item.getIconRes());
            holder.text.setText(item.getTitle());
        }

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String searchString = constraint.toString().toLowerCase();

            FilterResults ret = new FilterResults();

            int count = originalData.size();
            List<MainListItem> buildData = new ArrayList<>();

            if (searchString.isEmpty()) {
                // blank
            } else {
                for (int i = 0; i < count; i++) {

                    String filterableString;
                    String [] splits;

                    switch (originalData.get(i).getItemType()) {
                        case CONTACT:
                            if (searchString.equals("@")) {
                                buildData.add(originalData.get(i));
                            } else {
                                /**
                                 * A blank space was added with searchString2. After using trim the search problem is resolved
                                 */
                                String searchString2 = searchString.replaceAll("@", "").trim();
                                ContactListItem item = (ContactListItem) originalData.get(i);
                                filterableString =  item.getContactName();
                                //splits = filterableString.split(" ");
                                boolean isAdded = false;
                                if (filterableString.toString().toLowerCase().contains(searchString2)){
                                    buildData.add(originalData.get(i));
                                    isAdded = true;
                                }
                                /*for (String str: splits) {
                                    if (str.toLowerCase().startsWith(searchString2)) {
                                        buildData.add(originalData.get(i));
                                        isAdded = true;
                                        break;
                                    }
                                }*/

                                if (!isAdded) {
                                    searchString2 = phoneNumberString(searchString);
                                    List<ContactListItem.ContactNumber> numbers = item.getNumbers();
                                    for (ContactListItem.ContactNumber number : numbers) {
                                        String phoneNum = phoneNumberString(number.getNumber());
                                        if (phoneNum.contains(searchString2)) {
                                            buildData.add(originalData.get(i));
                                            break;
                                        }
                                    }
                                }
                            }

                            break;
                        case ACTION:
                            filterableString = originalData.get(i).getTitle();
                            splits = filterableString.split(" ");

                            for (String str: splits) {
                                if (str.toLowerCase().startsWith(searchString)) {
                                    buildData.add(originalData.get(i));
                                    break;
                                }
                            }
                            break;
                        case DEFAULT:
                            buildData.add(originalData.get(i));
                            break;
                    }

                }
            }

            ret.values = buildData;
            ret.count = buildData.size();

            return ret;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                filteredData = (List<MainListItem>) results.values;
            } else {
                filteredData = new ArrayList<>(originalData);
            }

            EventBus.getDefault().post(new MainListAdapterEvent(filteredData.size()));

            notifyDataSetChanged();
        }
    }

    private String phoneNumberString(String str) {
        return str.replaceAll("\\+", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\-", "");
    }
}