/*
 * Copyright (C) 2015 Tomás Ruiz-López.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import minium.co.core.app.CoreApplication;

/**
 * Created by tomas on 15/07/15.
 */
public class CountItemViewHolder extends RecyclerView.ViewHolder {

    @Bind({R.id.txt_app_name})
    TextView txt_app_name;
    @Bind({R.id.switch_appNotification})
    Switch switch_appNotification;


    @Bind({R.id.imv_appicon})
    ImageView imv_appicon;


    public CountItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void render(String text){
        txt_app_name.setText(text);
    }

    public void displayToggle(){
        switch_appNotification.setVisibility(View.VISIBLE);
    }

    public void setData(boolean ischecked){
        switch_appNotification.setChecked(ischecked);
    }

    public Switch getToggle(){
        return switch_appNotification;
    }


    public void displayImage(ApplicationInfo applicationInfo, PackageManager packageManager){
        if (CoreApplication.getInstance().iconList.get(applicationInfo.packageName) == null) {
            imv_appicon.setImageDrawable(applicationInfo.loadIcon(packageManager));
        } else {
            imv_appicon.setImageBitmap(CoreApplication.getInstance().iconList.get(applicationInfo.packageName));
        }
    }

    public void changeNotification(ApplicationInfo applicationInfo, boolean ischecked, ArrayList<String> disableNotificationApps,Context context){
        SharedPreferences launcherPrefs = context.getSharedPreferences("Launcher3Prefs", 0);
        if(ischecked && disableNotificationApps.contains(applicationInfo.packageName)){
                disableNotificationApps.remove(applicationInfo.packageName);
            }
            if(!ischecked && !disableNotificationApps.contains(applicationInfo.packageName)){
                disableNotificationApps.add(applicationInfo.packageName);
            }
            String disableList = new Gson().toJson(disableNotificationApps);
          launcherPrefs.edit().putString(CoreApplication.getInstance().DISABLE_APPLIST,disableList).commit();
        switch_appNotification.setChecked(ischecked);
    }

}
