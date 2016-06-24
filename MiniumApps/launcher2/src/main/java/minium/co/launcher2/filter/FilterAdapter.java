package minium.co.launcher2.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;

import minium.co.core.util.ThemeUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.model.MainListItem;

/**
 * Created by Shahab on 6/24/2016.
 */
public class FilterAdapter extends ArrayAdapter<MainListItem> {

    private Context context;

    public FilterAdapter(Context context, List<MainListItem> items) {
        super(context, 0);
        this.context = context;
        addAll(items);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return MainListItem.ItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType().ordinal();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MainListItem.ItemType itemViewType = MainListItem.ItemType.values() [getItemViewType(position)];

        switch (itemViewType) {

            case ACTION_LIST_ITEM:
                convertView = getActionItemView(position, convertView, parent);
                break;
            case CONTACT_ITEM:
                convertView = getContactItemView(position, convertView, parent);
                break;
            case OPTION_ITEM:
                convertView = getOptionItemView(position, convertView, parent);
                break;
        }

        return convertView;
    }

    private View getOptionItemView(int position, View view, ViewGroup parent) {
        OptionViewHolder holder;

        if (view == null) {
            holder = new OptionViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.item_action, parent, false);

            holder.icon = (IconTextView) view.findViewById(R.id.icon);
            holder.text = (TextView) view.findViewById(R.id.text);
            view.setTag(holder);
        } else {
            holder = (OptionViewHolder) view.getTag();
        }

        MainListItem item = getItem(position);

        if (item != null) {
            holder.icon.setText(item.getOptionsListItem().getIconName());
            holder.icon.setTextColor(ThemeUtils.getPrimaryColor(getContext()));
            holder.text.setText(item.getOptionsListItem().getText());
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
            holder.displayName = (TextView) view.findViewById(R.id.displayName);
            holder.phoneLabel = (TextView) view.findViewById(R.id.phoneLabel);
            holder.labelSeparator = (TextView) view.findViewById(R.id.labelSeparator);
            holder.phoneNumber = (TextView) view.findViewById(R.id.phoneNumber);
            view.setTag(holder);

        } else {
            holder = (ContactViewHolder) view.getTag();
        }

        MainListItem item = getItem(position);

        if (item != null) {
            holder.displayName.setText(item.getContactListItem().getContactName());
            if (item.getContactListItem().hasMultipleNumber()) {
                holder.phoneLabel.setText(context.getString(R.string.label_multiple_numbers));
                holder.phoneNumber.setVisibility(View.INVISIBLE);
                holder.labelSeparator.setVisibility(View.INVISIBLE);
            } else {
                holder.phoneLabel.setText(item.getContactListItem().getNumber().getLabel());
                holder.phoneNumber.setText(item.getContactListItem().getNumber().getNumber());
                holder.labelSeparator.setVisibility(View.VISIBLE);
                holder.phoneNumber.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    private View getActionItemView(int position, View view, ViewGroup parent) {
        ActionViewHolder holder;

        if (view == null) {
            holder = new ActionViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.item_action, parent, false);

            holder.icon = (IconTextView) view.findViewById(R.id.icon);
            holder.text = (TextView) view.findViewById(R.id.text);
            view.setTag(holder);
        } else {
            holder = (ActionViewHolder) view.getTag();
        }

        MainListItem item = getItem(position);

        if (item != null) {
            holder.icon.setText(item.getActionListItem().getIconName());
            holder.icon.setTextColor(ThemeUtils.getPrimaryColor(getContext()));
            holder.text.setText(item.getActionListItem().getText());
        }

        return view;
    }

    static class ActionViewHolder {
        IconTextView icon;
        TextView text;
    }

    static class ContactViewHolder {
        ImageView icon;
        TextView displayName;
        TextView phoneLabel;
        TextView phoneNumber;
        TextView labelSeparator;
    }

    static class OptionViewHolder {
        IconTextView icon;
        TextView text;
    }
}
