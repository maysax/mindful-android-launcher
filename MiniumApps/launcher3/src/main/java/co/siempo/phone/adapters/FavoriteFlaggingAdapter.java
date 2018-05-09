package co.siempo.phone.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.activities.FavoritesSelectionActivity;
import co.siempo.phone.app.BitmapWorkerTask;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.models.AppListInfo;
import co.siempo.phone.utils.Sorting;

/**
 * Created by rajeshjadi on 26/2/18.
 */

public class FavoriteFlaggingAdapter extends BaseAdapter implements Filterable {

    private final FavoritesSelectionActivity context;
    public String strSearch = "";
    private ArrayList<AppListInfo> mData = new ArrayList<>();
    private List<AppListInfo> filterList = new ArrayList<>();
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public FavoriteFlaggingAdapter(FavoritesSelectionActivity context, ArrayList<AppListInfo> mData) {
        this.context = context;
        this.mData = mData;
        filterList = mData;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public int getCount() {
        return filterList.size();
    }


    /*
    public void setData(ArrayList<AppListInfo> bind) {
        mData = bind;
        filterList = bind;
        notifyDataSetChanged();
    }*/

    public AppListInfo getItem(int position) {
        return filterList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_junkfoodflag, null);
            holder.txtAppName = convertView.findViewById(R.id.txtAppName);
            holder.imgAppIcon = convertView.findViewById(R.id.imgAppIcon);
            holder.imgChevron = convertView.findViewById(R.id.imgChevron);
            holder.linTop = convertView.findViewById(R.id.linTop);
            holder.txtNoAppsMessage = convertView.findViewById(R.id.txtNoAppsMessage);
            holder.txtHeader = convertView.findViewById(R.id.txtHeader);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.linTop.setTag(filterList.get(position));
        try {
            AppListInfo resolveInfo = (AppListInfo) holder.linTop.getTag();
            if (resolveInfo.isShowHeader && resolveInfo.isShowTitle) {
                holder.txtHeader.setVisibility(View.VISIBLE);
                holder.txtNoAppsMessage.setVisibility(View.VISIBLE);
                holder.linTop.setVisibility(View.GONE);
                if (resolveInfo.isFlagApp) {
                    holder.txtHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.section_title_bg));
                    holder.txtHeader.setText(context.getString(R.string.str_frequently_used_apps));
                } else {
                    holder.txtHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.unflageapp_header));
                    holder.txtHeader.setText(context.getString(R.string.all_other_installed_apps));
                    holder.txtNoAppsMessage.setText(context.getString(R.string.tap_apps_above_to_move_them_into_this_section));
                }
            } else if (resolveInfo.isShowHeader && !resolveInfo.isShowTitle) {
                holder.txtHeader.setVisibility(View.VISIBLE);
                holder.txtNoAppsMessage.setVisibility(View.GONE);
                holder.linTop.setVisibility(View.GONE);
                if (resolveInfo.isFlagApp) {
                    holder.txtHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.section_title_bg));
                    holder.txtHeader.setText(context.getString(R.string.str_frequently_used_apps));
                } else {
                    holder.txtHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.unflageapp_header));
                    holder.txtHeader.setText(context.getString(R.string.all_other_installed_apps));
                }
            } else {
                holder.linTop.setVisibility(View.VISIBLE);
                holder.txtNoAppsMessage.setVisibility(View.GONE);
                holder.txtHeader.setVisibility(View.GONE);
                try {
                    holder.txtAppName.setText(CoreApplication.getInstance().getApplicationNameFromPackageName(resolveInfo.packageName));
                    Bitmap bitmap = CoreApplication.getInstance().getBitmapFromMemCache(resolveInfo.packageName);
                    if (bitmap != null) {
                        holder.imgAppIcon.setImageBitmap(bitmap);
                    } else {
                        BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(context, resolveInfo.packageName);
                        CoreApplication.getInstance().includeTaskPool(bitmapWorkerTask, null);
                        Drawable drawable = CoreApplication.getInstance().getApplicationIconFromPackageName(resolveInfo.packageName);
                        holder.imgAppIcon.setImageDrawable(drawable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            CoreApplication.getInstance().logException(e);
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public static class ViewHolder {
        ImageView imgChevron, imgAppIcon;
        TextView txtNoAppsMessage;
        TextView txtAppName, txtHeader;
        LinearLayout linTop;
    }

    //added for search facilty
    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String searchString = constraint.toString().toLowerCase().trim();
            strSearch = searchString;
            FilterResults ret = new FilterResults();
            int count = mData.size();
            final ArrayList<AppListInfo> nlist = new ArrayList<AppListInfo>();
            String filterableString;
            List<AppListInfo> bindingList = new ArrayList<>();
            if (!searchString.isEmpty()) {
                for (int i = 0; i < count; i++) {
                    filterableString = mData.get(i).applicationName;
                    if (filterableString.toLowerCase().contains(searchString)) {
                        nlist.add(mData.get(i));
                    }
                }

                List<AppListInfo> flagAppList = new ArrayList<>();
                List<AppListInfo> unflageAppList = new ArrayList<>();
                for (AppListInfo resolveInfo : nlist) {
                    if (!resolveInfo.packageName.equalsIgnoreCase(context.getPackageName())) {
                        String applicationname = CoreApplication.getInstance()
                                .getListApplicationName().get(resolveInfo.packageName);
                        if (!TextUtils.isEmpty(applicationname)) {
                            if (context.list.contains(resolveInfo.packageName)) {
                                flagAppList.add(new AppListInfo(resolveInfo.packageName, applicationname, false, false, true));
                            } else {
                                unflageAppList.add(new AppListInfo(resolveInfo.packageName, applicationname, false, false, false));
                            }
                        }
                    }
                }
                //Code for removing the junk app from Favorite Sorted Menu and
                //Favorite List

                if (flagAppList.size() == 0) {
                    flagAppList.add(new AppListInfo("", "", true, true, true));
                } else {
                    flagAppList.add(0, new AppListInfo("", "", true, false, true));
                }

                flagAppList = Sorting.sortApplication(flagAppList);
                bindingList.addAll(flagAppList);
                if (unflageAppList.size() == 0) {
                    unflageAppList.add(new AppListInfo("", "", true, true, false));
                } else {
                    unflageAppList.add(0, new AppListInfo("", "", true, false, false));
                }
                unflageAppList = Sorting.sortApplication(unflageAppList);
                bindingList.addAll(unflageAppList);
            } else {
                bindingList.addAll(mData);
            }
            ret.values = bindingList;
            ret.count = bindingList.size();
            return ret;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                filterList = (List<AppListInfo>) results.values;
                Log.e("filterlist size", String.valueOf(filterList.size()));
            } else {
                filterList = new ArrayList<>(mData);
            }
            notifyDataSetChanged();
        }
    }


}
