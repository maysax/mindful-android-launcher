package co.siempo.phone.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.joanzapata.iconify.IconDrawable;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.main.MainListAdapterEvent;
import co.siempo.phone.models.ContactListItem;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.models.MainListItemType;
import co.siempo.phone.token.TokenManager;
import co.siempo.phone.utils.ColorGenerator;
import co.siempo.phone.utils.DrawableProvider;
import co.siempo.phone.utils.TextDrawable;
import de.greenrobot.event.EventBus;

/**
 * Created by Shahab on 2/16/2017.
 */


public class MainListAdapter extends ArrayAdapter<MainListItem> {

    private static final int HIGHLIGHT_COLOR = 0x999be6ff;
    private final Context context;
    private List<MainListItem> originalData = null;
    private List<MainListItem> filteredData = null;
    private ItemFilter filter = new ItemFilter();
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private DrawableProvider mProvider;
    private TextDrawable.IBuilder mDrawableBuilder;

    public MainListAdapter(Context context, List<MainListItem> items) {
        super(context, 0);
        this.context = context;
        mDrawableBuilder = TextDrawable.builder()
                .round();
        mProvider = new DrawableProvider(context);
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
        int pos = 0;
        try {
            pos = getItem(position).getItemType().ordinal();
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
        }
        return pos;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        MainListItemType itemViewType = MainListItemType.values()[getItemViewType(position)];
        if (itemViewType != null) {
            switch (itemViewType) {
                case CONTACT:
                    convertView = getContactItemView(position, convertView, parent);
                    break;
                case ACTION:
                case DEFAULT:
                case NUMBERS:
                    convertView = getActionItemView(position, convertView, parent);
                default:
                    break;
            }
        }
        return convertView;
    }

    private View getContactItemView(int position, View view, ViewGroup parent) {
        final ContactViewHolder holder;
        if (view == null) {
            holder = new ContactViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (inflater != null) {
                view = inflater.inflate(R.layout.list_item_contacts, parent, false);
                holder.icon = view.findViewById(R.id.icon);
                holder.txtNumber = view.findViewById(R.id.txtNumber);
                holder.text = view.findViewById(R.id.text);
                view.setTag(holder);
            }


        } else {
            holder = (ContactViewHolder) view.getTag();
        }

        ContactListItem item = (ContactListItem) getItem(position);

        if (item != null) {
            holder.text.setText(item.getContactName());
            if (item.getImageUri() != null && !TextUtils.isEmpty(item.getImageUri())) {

                Glide.with(context).load(Uri.parse(item.getImageUri())).asBitmap().centerCrop().placeholder(R.drawable.placeholder_blank_contact).into(new BitmapImageViewTarget(holder.icon) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.icon.setImageDrawable(circularBitmapDrawable);
                    }
                });
            } else {
                if (!TextUtils.isEmpty(item.getContactName())) {
                    Drawable drawable = mProvider.getRound("" + item.getContactName().charAt(0), Color.BLACK);
                    holder.icon.setImageDrawable(drawable);
                }
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

            if (inflater != null) {
                view = inflater.inflate(R.layout.list_item, parent, false);
                holder.icon = view.findViewById(R.id.icon);
                holder.text = view.findViewById(R.id.text);

                view.setTag(holder);
            }

        } else {
            holder = (ActionViewHolder) view.getTag();
        }

        MainListItem item = getItem(position);

        if (item != null) {
            Log.d("hardikkamothi","Item not null");
            if (item.getId() == -1) {
                if (!TextUtils.isEmpty(item.getApplicationInfo().packageName)) {

                    Log.d("hardikkamothi","Icon set 1");
                    holder.icon.setImageBitmap(CoreApplication.getInstance().iconList.get(item.getApplicationInfo().packageName));
                }
                holder.text.setText(item.getApplicationInfo().name);
            } else {
                if (item.getDrawable() != 0) {

                    Log.d("hardikkamothi","Icon set 2");
                    holder.icon.setImageResource(item.getDrawable());
                } else {

                    Log.d("hardikkamothi","Icon set 3");
//                    holder.icon.setImageResource(item.getIconRes());
                }
                holder.text.setText(item.getTitle());
            }

            if(item.getItemType() == MainListItemType.DEFAULT){
                holder.text.setTextColor(context.getResources().getColor(R
                        .color.appland_blue_bright));
            }
            else
            {
                holder.text.setTextColor(context.getResources().getColor(R
                        .color.black));
            }

        }

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    private boolean checkDuplicate(List<MainListItem> buildData, String str) {
        if (buildData != null) {
            for (MainListItem mainListItem : buildData) {
                if (mainListItem.getTitle().toLowerCase().equalsIgnoreCase(str)) {
                    return false;
                }
            }
        }
        return true;
    }

    private String phoneNumberString(String str) {
        return str.replaceAll("\\+", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll(context.getString(R.string.phone_replace_regex), "");
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

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String searchString = constraint.toString().toLowerCase().trim();

            FilterResults ret = new FilterResults();

            int count = originalData.size();
            List<MainListItem> buildData = new ArrayList<>();
            boolean isValidNumber = false;
            if (!searchString.isEmpty()) {

                for (int i = 0; i < count; i++) {
                    String filterableString;
                    String[] splits;
                    if (searchString.startsWith("/")) {
                        if (searchString.length() == 1 && searchString.equalsIgnoreCase("/")) {
                            buildData.clear();
                            for (MainListItem menuMainListItem : originalData) {
                                if (!(menuMainListItem instanceof ContactListItem)) {
                                    isValidNumber = true;
                                    buildData.add(menuMainListItem);
                                }
                            }
                        } else {
                            String strSearch = searchString.substring(1).toLowerCase();
                            if (originalData.get(i).getItemType() == MainListItemType.ACTION
                                    && originalData.get(i).getTitle().toLowerCase().contains(strSearch)) {
                                if (checkDuplicate(buildData, strSearch)) {
                                    isValidNumber = true;
                                    buildData.add(originalData.get(i));
                                }
                            }
                        }
                    } else {
                        switch (originalData.get(i).getItemType()) {
                            case CONTACT:
                                if (searchString.startsWith("@")) {
                                    if (searchString.equals("@")) {
                                        isValidNumber = true;
                                        buildData.add(originalData.get(i));
                                    } else {
                                    /*
                                      A blank space was added with searchString2. After using trim the search problem is resolved
                                     */
                                        String searchString2 = searchString.replaceAll("@", "").trim();
                                        ContactListItem item = (ContactListItem) originalData.get(i);
                                        filterableString = item.getContactName();
                                        boolean isAdded = false;
                                        if (filterableString.toLowerCase().contains(searchString2)) {
                                            isValidNumber = true;
                                            buildData.add(originalData.get(i));
                                            isAdded = true;
                                        }

                                        if (!isAdded) {
                                            searchString2 = phoneNumberString(searchString);
                                            List<ContactListItem.ContactNumber> numbers = item.getNumbers();
                                            for (ContactListItem.ContactNumber number : numbers) {
                                                String phoneNum = phoneNumberString(number.getNumber());
                                                if (phoneNum.contains(searchString2)) {
                                                    isValidNumber = true;
                                                    buildData.add(originalData.get(i));
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }

                                break;
                            case ACTION:
                                filterableString = originalData.get(i).getTitle();
                                if (originalData.get(i).getApplicationInfo() == null) {
                                    if (filterableString.contains(searchString.toLowerCase().trim())) {
                                        buildData.add(originalData.get(i));
                                        break;
                                    }
                                } else {
                                    if (originalData.get(i).getTitle().toLowerCase().contains(searchString.toLowerCase())) {
                                        if (checkDuplicate(buildData, searchString.toLowerCase().toLowerCase())) {
                                            buildData.add(originalData.get(i));
                                        }
                                    }
                                }
                                break;
                            case NUMBERS:
                                if (PhoneNumberUtils.isGlobalPhoneNumber(searchString)) {
                                    isValidNumber = true;
                                    TokenManager.getInstance().getCurrent().setExtra2(searchString);
                                    buildData.add(originalData.get(i));
                                }
                                break;
                            case DEFAULT:
                                if (checkDuplicate(buildData, originalData.get(i).getTitle().toLowerCase().toLowerCase())) {
                                    if (originalData.get(i).getTitle().toLowerCase().equalsIgnoreCase("Send as SMS") && !isValidNumber) {

                                    } else {
                                        buildData.add(originalData.get(i));
                                    }
                                }
                                break;

                        }
                    }
                }
            }
            else{
                buildData=originalData;
            }

            Log.d("List Size , Hardk", String.valueOf(buildData.size()));
            Log.d("List Size , Hardk", String.valueOf(originalData.size()));
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
}