package co.siempo.phone.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
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
import co.siempo.phone.activities.JunkfoodFlaggingActivity;
import co.siempo.phone.app.BitmapWorkerTask;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.models.AppListInfo;
import co.siempo.phone.utils.Sorting;
import co.siempo.phone.utils.UIUtils;

/**
 * Created by rajeshjadi on 26/2/18.
 */

public class JunkfoodFlaggingAdapter extends BaseAdapter implements Filterable {

    public final JunkfoodFlaggingActivity context;
    String searchReference = "";
    private ArrayList<AppListInfo> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private List<AppListInfo> filterList = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();
    private PopupMenu popup;

    public JunkfoodFlaggingAdapter(JunkfoodFlaggingActivity context, ArrayList<AppListInfo> mData) {
        this.context = context;
        this.mData = mData;
        filterList = mData;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return filterList.size();
    }

    public void setData(ArrayList<AppListInfo> bind) {
        mData = bind;
        filterList = bind;
        notifyDataSetChanged();
    }

    public AppListInfo getItem(int position) {
        return filterList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
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
            holder.linearList = convertView.findViewById(R.id.linearList);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        holder.linTop.setTag(filterList.get(position));
        try {
            final AppListInfo resolveInfo = filterList.get(position);
            Log.d("Tetsing ", resolveInfo.toString());
            if (resolveInfo.isShowHeader && resolveInfo.isShowTitle) {
                holder.txtHeader.setVisibility(View.VISIBLE);
                holder.txtNoAppsMessage.setVisibility(View.VISIBLE);
                holder.linTop.setVisibility(View.GONE);
                if (resolveInfo.isFlagApp) {
                    holder.txtHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.flageapp_header));
                    holder.txtHeader.setText(context.getString(R.string.flag_app));
                    int color = R.color.forground;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.txtNoAppsMessage.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                        holder.txtNoAppsMessage.setText(searchReference.equalsIgnoreCase("") ? context.getString(R.string.tap_apps_below_to_move_them_into_this_section) : context.getString(R
                                .string.no_apps));
                    }
                } else {
                    holder.txtHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.unflageapp_header));
                    holder.txtHeader.setText(context.getString(R.string.all_other_installed_apps));
                    int color = R.color.transparent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.txtNoAppsMessage.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                    }
                    holder.txtNoAppsMessage.setText(searchReference.equalsIgnoreCase("") ? context.getString(R.string.tap_apps_above_to_move_them_into_this_section) : "No apps match that input text");
                }
            } else if (resolveInfo.isShowHeader && !resolveInfo.isShowTitle) {
                holder.txtHeader.setVisibility(View.VISIBLE);
                holder.linTop.setVisibility(View.GONE);
                if (resolveInfo.isFlagApp) {
                    holder.txtHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.flageapp_header));
                    holder.txtHeader.setText(context.getString(R.string.flag_app));
                } else {
                    holder.txtHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.unflageapp_header));
                    holder.txtHeader.setText(context.getString(R.string.all_other_installed_apps));
                }
                holder.txtNoAppsMessage.setVisibility(View.GONE);

            } else {
                holder.linTop.setVisibility(View.VISIBLE);
                holder.txtNoAppsMessage.setVisibility(View.GONE);
                holder.txtHeader.setVisibility(View.GONE);
                try {
                    //Done as a part of SSA-1454, in order to change the app name
                    // based on user selected language
                    holder.txtAppName.setText(CoreApplication.getInstance()
                            .getApplicationNameFromPackageName(resolveInfo.packageName));
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
                if (resolveInfo.isFlagApp) {
                    holder.imgChevron.setImageResource(R.drawable.ic_down_arrow_red);
                    int color = R.color.forground;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.linTop.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                    }
                } else {
                    holder.imgChevron.setImageResource(R.drawable.ic_down_arrow);
                    int color = R.color.transparent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.linTop.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                    }
                }
            }
            holder.linearList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (resolveInfo != null) {
                        if (!resolveInfo.packageName.equalsIgnoreCase("")) {
                            UIUtils.hideSoftKeyboard(context, context.getWindow().getDecorView().getWindowToken());
                            context.showPopUp(v, resolveInfo.packageName, resolveInfo.isFlagApp);
                        }
                    } else {
                        UIUtils.hideSoftKeyboard(context, context.getWindow().getDecorView().getWindowToken());
                    }
                }
            });

            holder.txtHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIUtils.hideSoftKeyboard(context, context.getWindow().getDecorView().getWindowToken());
                }
            });
            holder.txtNoAppsMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIUtils.hideSoftKeyboard(context, context.getWindow().getDecorView().getWindowToken());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            CoreApplication.getInstance().logException(e);
        }
        return convertView;
    }


    public Filter getFilter() {
        return mFilter;
    }


    public static class ViewHolder {
        ImageView imgChevron, imgAppIcon;
        TextView txtNoAppsMessage;
        TextView txtAppName, txtHeader;
        LinearLayout linTop, linearList;
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String searchString = constraint.toString().toLowerCase().trim();
            searchReference = searchString;
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
            } else {
                filterList = new ArrayList<>(mData);
            }
            notifyDataSetChanged();
        }
    }


}
