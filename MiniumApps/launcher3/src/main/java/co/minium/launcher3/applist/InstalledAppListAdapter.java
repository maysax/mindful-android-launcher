package co.minium.launcher3.applist;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import co.minium.launcher3.R;
import co.minium.launcher3.db.ActivitiesStorage;

import static android.media.CamcorderProfile.get;

/**
 * Created by tkb on 2017-04-21.
 */

public class InstalledAppListAdapter extends ArrayAdapter<ApplistDataModel> {

    private final Activity context;
    public InstalledAppListAdapter(Activity context) {
        super(context, R.layout.installed_app_list_row);
        this.context = context;
    }

    public void setAppInfo(List<ApplistDataModel> appInfo) {
        if (appInfo!=null){
            addAll(appInfo);
        }else {
            clear();
        }
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.installed_app_list_row, null, false);
        TextView txt_app_name = (TextView) rowView.findViewById(R.id.txt_app_name);
        ImageView imv_appicon = (ImageView)rowView.findViewById(R.id.imv_appicon);
        try {
            txt_app_name.setText(getItem(position).getName());
            //Glide.with(context).load(appInfo.get(position).getIcon()).into(imv_appicon);

            imv_appicon.setImageDrawable(getItem(position).getIcon());
        }catch (Exception e){
            e.printStackTrace();
        }

        return rowView;
    }


}
