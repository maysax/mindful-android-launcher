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
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import co.siempo.phone.R;
import co.siempo.phone.applist.DisableAppList;
import co.siempo.phone.applist.HeaderAppList;
import minium.co.core.app.CoreApplication;

public class CountSectionAdapter extends SectionedRecyclerViewAdapter<CountHeaderViewHolder,
        CountItemViewHolder,
        CountFooterViewHolder> {

    protected Context context = null;
    private PackageManager packageManager;
    SharedPreferences  launcherPrefs ;
    ArrayList<String> disableNotificationApps=new ArrayList<>();
    ArrayList<String> disableSections=new ArrayList<>();


    private List<DisableAppList> appList,socialList;
    private List<HeaderAppList> headerList;

    public CountSectionAdapter(Context context, List<DisableAppList> appList, List<DisableAppList> socialList, List<HeaderAppList> headerList) {
        this.context = context;
        this.appList = appList;
        this.socialList= socialList;
        this.headerList = headerList;
        this.packageManager=context.getPackageManager();
        launcherPrefs = context.getSharedPreferences("Launcher3Prefs", 0);

        String disable_AppList=launcherPrefs.getString(CoreApplication.getInstance().DISABLE_APPLIST,"");
        if(!TextUtils.isEmpty(disable_AppList)){
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            disableNotificationApps = new Gson().fromJson(disable_AppList, type);
        }

        String disable_HeaderAppList=launcherPrefs.getString(CoreApplication.getInstance().HEADER_APPLIST,"");
        if(!TextUtils.isEmpty(disable_HeaderAppList)){
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            disableSections = new Gson().fromJson(disable_HeaderAppList, type);
        }
    }

    @Override
    protected int getItemCountForSection(int section) {
        int size=0;
        if(section == 0 ){
            size = socialList.size();
        }
        if(section == 1){
            size = appList.size();
        }
        return  size;
    }

    @Override
    protected int getSectionCount() {
        return headerList.size();
    }

    @Override
    protected boolean hasFooterInSection(int section) {
        return true;
    }

    protected LayoutInflater getLayoutInflater(){
        return LayoutInflater.from(context);
    }

    @Override
    protected CountHeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.section_header_layout, parent, false);
        return new CountHeaderViewHolder(view);
    }

    @Override
    protected CountFooterViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.view_count_footer, parent, false);
        return new CountFooterViewHolder(view);
    }

    @Override
    protected CountItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.installed_app_list_row, parent, false);
        return new CountItemViewHolder(view);
    }

    @Override
    protected void onBindSectionHeaderViewHolder(final CountHeaderViewHolder holder, final int section) {
        holder.render(headerList.get(section).name);

        holder.setData(headerList.get(section).ischecked);

        holder.getHeaderToggle().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(holder.getHeaderToggle().isPressed()){
                    holder.changeNotification(headerList.get(section),isChecked,disableSections,context);


                    HeaderAppList d=headerList.get(section);
                    d.ischecked=isChecked;
                    headerList.set(section,d);
                    holder.setData(isChecked);
                }
            }
        });


    }

    @Override
    protected void onBindSectionFooterViewHolder(CountFooterViewHolder holder, int section) {
    }

    @Override
    protected void onBindItemViewHolder(final CountItemViewHolder holder, int section, final int position) {

        holder.displayToggle();

        if(section == 0){
            holder.render(socialList.get(position).applicationInfo.name);
            holder.displayImage(socialList.get(position).applicationInfo,packageManager);
            holder.setData(socialList.get(position).ischecked);
            holder.getToggle().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(holder.getToggle().isPressed()){
                        holder.changeNotification(socialList.get(position).applicationInfo,isChecked,disableNotificationApps,context);


                        DisableAppList d=socialList.get(position);
                        d.ischecked=isChecked;
                        socialList.set(position,d);
                        holder.setData(isChecked);


                    }
                }
            });
        }

        if(section == 1){
            holder.render(appList.get(position).applicationInfo.name);

            holder.displayImage(appList.get(position).applicationInfo,packageManager);
            holder.setData(appList.get(position).ischecked);
            holder.getToggle().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(holder.getToggle().isPressed()){
                        holder.changeNotification(appList.get(position).applicationInfo,isChecked,disableNotificationApps,context);

                        DisableAppList d=appList.get(position);
                        d.ischecked=isChecked;
                        appList.set(position,d);
                        holder.setData(isChecked);

                    }
                }
            });
        }
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
