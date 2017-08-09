package minium.co.launcher2.contactspicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import minium.co.launcher2.R;
import minium.co.launcher2.model.ContactListItem;

/**
 * Created by Shahab on 6/27/2016.
 */
public class ContactsPickerAdapter extends ArrayAdapter<ContactListItem> {

    private Context context;

    private List<ContactListItem> originalData = null;
    private List<ContactListItem> filteredData = null;
    private ItemFilter filter = new ItemFilter();

    public ContactsPickerAdapter(Context context, List<ContactListItem> items) {
        super(context, 0);
        this.context = context;
        originalData = items;
        filteredData = items;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public ContactListItem getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactViewHolder holder;

        if (convertView == null) {
            holder = new ContactViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_item_contacts, parent, false);

            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.displayName = (TextView) convertView.findViewById(R.id.displayName);
            holder.phoneLabel = (TextView) convertView.findViewById(R.id.phoneLabel);
            holder.labelSeparator = (TextView) convertView.findViewById(R.id.labelSeparator);
            holder.phoneNumber = (TextView) convertView.findViewById(R.id.phoneNumber);
            convertView.setTag(holder);

        } else {
            holder = (ContactViewHolder) convertView.getTag();
        }

        ContactListItem item = getItem(position);

        if (item != null) {
            holder.displayName.setText(item.getContactName());
            if (item.hasMultipleNumber()) {
                holder.phoneLabel.setText(context.getString(R.string.label_multiple_numbers));
                holder.phoneNumber.setVisibility(View.INVISIBLE);
                holder.labelSeparator.setVisibility(View.INVISIBLE);
            } else {
                holder.phoneLabel.setText(item.getNumber().getLabel());
                holder.phoneNumber.setText(item.getNumber().getNumber());
                holder.labelSeparator.setVisibility(View.VISIBLE);
                holder.phoneNumber.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    static class ContactViewHolder {
        ImageView icon;
        TextView displayName;
        TextView phoneLabel;
        TextView phoneNumber;
        TextView labelSeparator;
    }

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
            List<ContactListItem> buildData = new ArrayList<>();

            for (int i = 0; i < count; i++) {

                if (searchString.isEmpty()) {
                    buildData.add(originalData.get(i));
                } else {
                    String filterableString;
                    String[] splits;


                    filterableString = originalData.get(i).getContactName();
                    splits = filterableString.split(" ");
                    boolean isAdded = false;

                    for (String str : splits) {
                        if (str.toLowerCase().startsWith(searchString)) {
                            buildData.add(originalData.get(i));
                            isAdded = true;
                            break;
                        }
                    }

                    if (!isAdded) {
                        searchString = phoneNumberString(searchString);
                        List<ContactListItem.ContactNumber> numbers = originalData.get(i).getNumbers();
                        for (ContactListItem.ContactNumber number : numbers) {
                            String phoneNum = phoneNumberString(number.getNumber());
                            if (phoneNum.contains(searchString)) {
                                buildData.add(originalData.get(i));
                                isAdded = true;
                                break;
                            }
                        }
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
                filteredData = (List<ContactListItem>) results.values;
            } else {
                filteredData = new ArrayList<>(originalData);
            }

            notifyDataSetChanged();
        }
    }

    private String phoneNumberString(String str) {
        return str.replaceAll("\\+", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\-", "");
    }
}
