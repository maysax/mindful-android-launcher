package co.siempo.phone.applist;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App_;
import minium.co.core.log.Tracer;

/**
 * Created by tkb on 2017-04-21.
 */

public class InstalledAppListAdapter extends ArrayAdapter<ApplistDataModel> {

    private final Activity context;

    InstalledAppListAdapter(Activity context) {
        super(context, R.layout.installed_app_list_row);
        this.context = context;
    }

    void setAppInfo(List<ApplistDataModel> appInfo) {
        if (appInfo != null) {
            addAll(appInfo);
        } else {
            clear();
        }
    }


    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.installed_app_list_row, null, false);
        TextView txt_app_name = (TextView) rowView.findViewById(R.id.txt_app_name);
        ImageView imv_appicon = (ImageView) rowView.findViewById(R.id.imv_appicon);
        try {
            txt_app_name.setText(getItem(position).getName());
            //Glide.with(context).load(appInfo.get(position).getIcon()).into(imv_appicon);

            imv_appicon.setImageDrawable(getDrawable(getItem(position)));
        } catch (Exception e) {
            Tracer.e(e);
        }

        return rowView;
    }

    private Drawable getDrawable(ApplistDataModel model) {
        if (model.getIcon() == null) {
            model.setIcon(Launcher3App_.getIconsHandler(context).getDrawableIconForPackage(model.getPackageName()));
        }
        return model.getIcon();
    }
}
