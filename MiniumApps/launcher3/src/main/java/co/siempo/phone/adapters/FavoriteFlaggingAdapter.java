package co.siempo.phone.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.app.BitmapWorkerTask;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.models.AppListInfo;

/**
 * Created by rajeshjadi on 26/2/18.
 */

public class FavoriteFlaggingAdapter extends BaseAdapter {

    private final Context context;
    Set<String> list = new HashSet<>();
    private ArrayList<AppListInfo> mData = new ArrayList<>();
    private LayoutInflater mInflater;

    public FavoriteFlaggingAdapter(Context context, ArrayList<AppListInfo> mData, Set<String> list) {
        this.context = context;
        this.mData = mData;
        this.list = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mData.size();
    }

    public AppListInfo getItem(int position) {
        return mData.get(position);
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

        holder.linTop.setTag(mData.get(position));
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


    public static class ViewHolder {
        ImageView imgChevron, imgAppIcon;
        TextView txtNoAppsMessage;
        TextView txtAppName, txtHeader;
        LinearLayout linTop;
    }


}