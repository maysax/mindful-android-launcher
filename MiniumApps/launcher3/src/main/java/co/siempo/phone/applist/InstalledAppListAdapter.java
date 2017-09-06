package co.siempo.phone.applist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import co.siempo.phone.R;
import minium.co.core.app.CoreApplication;




public class InstalledAppListAdapter extends BaseAdapter {

    private final Activity context;
    PackageManager packageManager;
    List<ApplicationInfo> arrayList;
    LayoutInflater mInflater;

    InstalledAppListAdapter(Activity context, List<ApplicationInfo> arrayList) {
        this.context = context;
        packageManager = context.getPackageManager();
        this.arrayList = arrayList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.installed_app_list_row, parent,false);
            holder = new ViewHolder();
            holder.txt_app_name = (TextView) convertView.findViewById(R.id.txt_app_name);
            holder.imv_appicon = (ImageView) convertView.findViewById(R.id.imv_appicon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ApplicationInfo applicationInfo = (ApplicationInfo) getItem(position);

        holder.txt_app_name.setText(applicationInfo.name);
        if(CoreApplication.getInstance().iconList.get(applicationInfo.name)==null){
            holder.imv_appicon.setImageDrawable(applicationInfo.loadIcon(packageManager));
        }else {
            holder.imv_appicon.setImageBitmap(CoreApplication.getInstance().iconList.get(applicationInfo.name));
        }

        return convertView;
    }

    static class ViewHolder {
        private TextView txt_app_name;
        private ImageView imv_appicon;
    }
}
