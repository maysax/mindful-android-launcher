package co.siempo.phone.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import minium.co.core.app.CoreApplication;


public class NotificationItemViewHolder extends RecyclerView.ViewHolder {

    @Bind({R.id.txt_app_name})
    TextView txt_app_name;
    @Bind({R.id.switch_appNotification})
    Switch switch_appNotification;


    @Bind({R.id.imv_appicon})
    ImageView imv_appicon;


    public NotificationItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void render(String text) {
        txt_app_name.setText(text);
    }

    public void displayToggle() {
        switch_appNotification.setVisibility(View.VISIBLE);
    }

    public void setData(boolean ischecked) {
        switch_appNotification.setChecked(ischecked);
    }

    public Switch getToggle() {
        return switch_appNotification;
    }


    public void displayImage(ApplicationInfo applicationInfo, PackageManager packageManager) {
        if (CoreApplication.getInstance().iconList.get(applicationInfo.packageName) == null) {
            imv_appicon.setImageDrawable(applicationInfo.loadIcon(packageManager));
        } else {
            imv_appicon.setImageBitmap(CoreApplication.getInstance().iconList.get(applicationInfo.packageName));
        }
    }

    public void changeNotification(ApplicationInfo applicationInfo, boolean ischecked, ArrayList<String> disableNotificationApps, Context context) {
        SharedPreferences launcherPrefs = context.getSharedPreferences("Launcher3Prefs", 0);
        if (ischecked && disableNotificationApps.contains(applicationInfo.packageName)) {
            disableNotificationApps.remove(applicationInfo.packageName);
        }
        if (!ischecked && !disableNotificationApps.contains(applicationInfo.packageName)) {
            disableNotificationApps.add(applicationInfo.packageName);
        }
        String disableList = new Gson().toJson(disableNotificationApps);
        launcherPrefs.edit().putString(Constants.DISABLE_APPLIST, disableList).commit();
        switch_appNotification.setChecked(ischecked);
    }


}
