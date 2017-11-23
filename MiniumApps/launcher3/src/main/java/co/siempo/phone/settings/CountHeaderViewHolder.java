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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.siempo.phone.R;
import co.siempo.phone.applist.HeaderAppList;
import minium.co.core.app.CoreApplication;

/**
 * Created by tomas on 15/07/15.
 */
public class CountHeaderViewHolder extends RecyclerView.ViewHolder {

    @Bind({R.id.txt_headerName})
    TextView txt_headerName;

    @Bind({R.id.switch_headerNotification})
    Switch switch_headerNotification;

    public CountHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void render(String text){
        txt_headerName.setText(text);
    }

    public void setHeaderData(boolean ischecked){
        switch_headerNotification.setChecked(ischecked);
    }

    public Switch getHeaderToggle(){
        return switch_headerNotification;
    }

    public void setData(boolean ischecked){
        switch_headerNotification.setChecked(ischecked);
    }

    public void changeNotification(HeaderAppList headerAppList, boolean ischecked, ArrayList<String> disableHeaderApps, Context context){
        SharedPreferences launcherPrefs = context.getSharedPreferences("Launcher3Prefs", 0);
        if(ischecked && disableHeaderApps.contains(headerAppList.name)){
            disableHeaderApps.remove(headerAppList.name);
        }
        if(!ischecked && !disableHeaderApps.contains(headerAppList.name)){
            disableHeaderApps.add(headerAppList.name);
        }

        String disableList = new Gson().toJson(disableHeaderApps);
        launcherPrefs.edit().putString(CoreApplication.getInstance().HEADER_APPLIST,disableList).commit();
        switch_headerNotification.setChecked(ischecked);
    }


}
