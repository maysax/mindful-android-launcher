package co.siempo.phone.adapters.viewholder;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import minium.co.core.app.CoreApplication;


public class TempoNotificationItemViewHolder extends RecyclerView.ViewHolder {

    @Bind({R.id.txt_app_name})
    TextView txt_app_name;
    @Bind({R.id.img_block_unblock})
    ImageView img_block_unblock;


    @Bind({R.id.imv_appicon})
    ImageView imv_appicon;

    @Bind({R.id.linearList})
    LinearLayout linearList;


    public TempoNotificationItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void render(String text) {
        txt_app_name.setText(text);
    }

    public void displayToggle() {
        img_block_unblock.setVisibility(View.VISIBLE);
    }


    public ImageView getToggle() {
        return img_block_unblock;
    }

    public LinearLayout getLinearList(){
        return  linearList;
    }


    public void displayImage(ApplicationInfo applicationInfo, PackageManager packageManager,String errormessage) {
        if(TextUtils.isEmpty(errormessage)) {
            if (CoreApplication.getInstance().iconList.get(applicationInfo.packageName) == null) {
                imv_appicon.setImageDrawable(applicationInfo.loadIcon(packageManager));
            } else {
                imv_appicon.setImageBitmap(CoreApplication.getInstance().iconList.get(applicationInfo.packageName));
            }
        }else{
            imv_appicon.setImageBitmap(null);
        }
    }

    public void disableViews(){
        imv_appicon.setVisibility(View.INVISIBLE);
        img_block_unblock.setVisibility(View.INVISIBLE);
        txt_app_name.setTextColor(Color.parseColor("#777777"));
        txt_app_name.setTextSize(12);

    }

    public void enableViews(){
        txt_app_name.setTextSize(16);
        txt_app_name.setTextColor(Color.parseColor("#000000"));
        img_block_unblock.setVisibility(View.VISIBLE);
        imv_appicon.setVisibility(View.VISIBLE);
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
        launcherPrefs.edit().putString(Constants.HELPFUL_ROBOTS, disableList).commit();
    }


    public void addToBlockList(ApplicationInfo applicationInfo, boolean ischecked, ArrayList<String> blockedApps, Context context) {

        SharedPreferences launcherPrefs = context.getSharedPreferences("Launcher3Prefs", 0);
        if (ischecked && blockedApps.contains(applicationInfo.packageName)) {
            blockedApps.remove(applicationInfo.packageName);
        }
        if (!ischecked && !blockedApps.contains(applicationInfo.packageName)) {
            blockedApps.add(applicationInfo.packageName);
        }
        String blockedList = new Gson().toJson(blockedApps);
        launcherPrefs.edit().putString(Constants.BLOCKED_APPLIST, blockedList).commit();


    }


}
