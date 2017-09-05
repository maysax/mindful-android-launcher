package co.siempo.phone.main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.joanzapata.iconify.IconDrawable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import co.siempo.phone.R;
import co.siempo.phone.model.ContactListItem;
import co.siempo.phone.model.MainListItem;
import co.siempo.phone.model.MainListItemType;
import de.greenrobot.event.EventBus;
import minium.co.core.app.CoreApplication;

/**
 * Created by Shahab on 2/16/2017.
 */


public class MainListAdapter extends ArrayAdapter<MainListItem> {

    private Context context;

    private List<MainListItem> originalData = null;
    private List<MainListItem> filteredData = null;
    private ItemFilter filter = new ItemFilter();
    PackageManager packageManager;

    public MainListAdapter(Context context, List<MainListItem> items) {
        super(context, 0);
        this.context = context;
        packageManager = context.getPackageManager();
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
                case NUMBERS:
                    break;
            }
        }

        return null;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        MainListItemType itemViewType = MainListItemType.values()[getItemViewType(position)];

        switch (itemViewType) {
            case CONTACT:
                convertView = getContactItemView(position, convertView, parent);
                break;
            case ACTION:
            case DEFAULT:
            case NUMBERS:
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

    private View getNumberPickerView(int position, View view, ViewGroup parent) {
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

        MainListItem item = getItem(position);

        if (item != null) {
            holder.text.setText(item.getTitle());
            holder.icon.setImageResource(item.getIconRes());
            holder.txtNumber.setText(item.getSubTitle());
        }

        return view;
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
            if (item.getId() == -1) {
                if(!TextUtils.isEmpty(item.getApplicationInfo().packageName)){
                    holder.icon.setImageBitmap(CoreApplication.getInstance().iconList.get(item.getApplicationInfo().packageName));
                }
                holder.text.setText(item.getApplicationInfo().name);
            } else {
                if (item.getIcon() != null) {
                    holder.icon.setImageDrawable(new IconDrawable(context, item.getIcon())
                            .colorRes(R.color.text_primary)
                            .sizeDp(18));
                } else {
                    holder.icon.setImageResource(item.getIconRes());
                }
                holder.text.setText(item.getTitle());
            }

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

            String searchString = constraint.toString();

            FilterResults ret = new FilterResults();

            int count = originalData.size();
            List<MainListItem> buildData = new ArrayList<>();

            if (searchString.isEmpty()) {
                // blank
            } else {
                for (int i = 0; i < count; i++) {
                    String filterableString;
                    String[] splits;
                    if (searchString.startsWith("/")) {
                        if (searchString.length() == 1 && searchString.equalsIgnoreCase("/")) {
                            buildData.clear();
                            for (MainListItem menuMainListItem : originalData) {
                                if (!(menuMainListItem instanceof ContactListItem)) {
                                    buildData.add(menuMainListItem);
                                }
                            }
                        } else {
                            String strSearch = searchString.substring(1).toLowerCase();
                            if (originalData.get(i).getItemType() == MainListItemType.ACTION
                                    && originalData.get(i).getTitle().toLowerCase().startsWith(strSearch)) {
                                if (!checkDuplicate(buildData, strSearch))
                                    buildData.add(originalData.get(i));
                            }
                        }
                    } else {
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
                                    filterableString = item.getContactName();
                                    boolean isAdded = false;
                                    if (filterableString.toString().toLowerCase().contains(searchString2)) {
                                        buildData.add(originalData.get(i));
                                        isAdded = true;
                                    }

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
                                if (originalData.get(i).getApplicationInfo() == null) {
                                    splits = filterableString.split(" ");
                                    for (String str : splits) {
                                        if (str.startsWith(searchString)) {
                                            buildData.add(originalData.get(i));
                                            break;
                                        }
                                    }
                                } else {
                                    if (originalData.get(i).getTitle().toLowerCase().startsWith(searchString.toLowerCase())) {
                                        if (!checkDuplicate(buildData, searchString.toLowerCase().toLowerCase()))
                                            buildData.add(originalData.get(i));
                                    }
                                }
                                break;
                            case DEFAULT:
                                if (!checkDuplicate(buildData, originalData.get(i).getTitle().toLowerCase().toLowerCase()))
                                    buildData.add(originalData.get(i));
                                break;
                            case NUMBERS:
                                buildData.add(originalData.get(i));
                                break;
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
                filteredData = (List<MainListItem>) results.values;
            } else {
                filteredData = new ArrayList<>(originalData);
            }
            EventBus.getDefault().post(new MainListAdapterEvent(filteredData.size()));
            notifyDataSetChanged();
        }
    }

    private boolean checkDuplicate(List<MainListItem> buildData, String str) {
        for (MainListItem mainListItem : buildData) {
            if (mainListItem.getTitle().toLowerCase().toString().equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    private String phoneNumberString(String str) {
        return str.replaceAll("\\+", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll(context.getString(R.string.phone_replace_regex), "");
    }
}