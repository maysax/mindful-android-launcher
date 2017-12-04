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
import android.text.TextUtils;
import android.util.Log;
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
    private List<DisableAppList> appList,socialList,messengerList;
    private List<HeaderAppList> headerList;



    public CountSectionAdapter(Context context, List<DisableAppList> appList, List<DisableAppList> socialList,List<DisableAppList> messengerList, List<HeaderAppList> headerList) {
        this.context = context;
        this.appList = appList;
        this.socialList= socialList;
        this.headerList = headerList;
        this.messengerList = messengerList;
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
        if(headerList.get(section).name.equals("Social Media")){
            size = socialList.size();
        }
        if(headerList.get(section).name.equals("Messengers")){
            size = messengerList.size();
        }
        if(headerList.get(section).name.equals("Other Apps")){
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

                    // Below logic is use to add disable section names to disable toggle.
                    holder.changeNotification(headerList.get(section),isChecked,disableSections,context);

                    // Below logic is use to disable section name for update existing (Current display) list
                    HeaderAppList d=headerList.get(section);
                    d.ischecked=isChecked;
                    headerList.set(section,d);
                    holder.setData(isChecked);


                    if(headerList.get(section).name.equals("Social Media")){
                        if(isChecked){
                            launcherPrefs.edit().putInt(CoreApplication.getInstance().SOCIAL_DISABLE_COUNT,0).commit();
                        }
                        else{
                            launcherPrefs.edit().putInt(CoreApplication.getInstance().SOCIAL_DISABLE_COUNT,socialList.size()).commit();
                        }
                        android.os.Handler handler = new android.os.Handler();
                        for(int i=0;i<socialList.size();i++){
                            changeAppNotification(socialList.get(i).applicationInfo,isChecked,disableNotificationApps,context);
                            DisableAppList d1=socialList.get(i);
                            d1.ischecked=isChecked;
                            socialList.set(i,d1);
                            notifyDataSetChanged();
                        }
                    }


                    if(headerList.get(section).name.equals("Messengers")){
                        if(isChecked){
                            launcherPrefs.edit().putInt(CoreApplication.getInstance().MESSENGER_DISABLE_COUNT,0).commit();
                        }
                        else{
                            launcherPrefs.edit().putInt(CoreApplication.getInstance().MESSENGER_DISABLE_COUNT,messengerList.size()).commit();
                        }
                        for(int i=0;i<messengerList.size();i++){
                            changeAppNotification(messengerList.get(i).applicationInfo,isChecked,disableNotificationApps,context);
                            DisableAppList d1=messengerList.get(i);
                            d1.ischecked=isChecked;
                            messengerList.set(i,d1);
                            notifyDataSetChanged();
                        }
                    }

                    if(headerList.get(section).name.equals("Other Apps")){
                        if(isChecked){
                            launcherPrefs.edit().putInt(CoreApplication.getInstance().APP_DISABLE_COUNT,0).commit();
                        }
                        else{
                            launcherPrefs.edit().putInt(CoreApplication.getInstance().APP_DISABLE_COUNT,appList.size()).commit();
                        }
                        for(int i=0;i<appList.size();i++){
                            changeAppNotification(appList.get(i).applicationInfo,isChecked,disableNotificationApps,context);
                            DisableAppList d1=appList.get(i);
                            d1.ischecked=isChecked;
                            appList.set(i,d1);
                            notifyDataSetChanged();
                        }
                    }



                }
            }
        });


    }

    @Override
    protected void onBindSectionFooterViewHolder(CountFooterViewHolder holder, int section) {
    }

    @Override
    protected void onBindItemViewHolder(final CountItemViewHolder holder, final int section, final int position) {

        holder.displayToggle();

        if(headerList.get(section).name.equals("Social Media")){
            holder.render(socialList.get(position).applicationInfo.name);
            holder.displayImage(socialList.get(position).applicationInfo,packageManager);
            holder.setData(socialList.get(position).ischecked);
            holder.getToggle().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(holder.getToggle().isPressed()){

                        // Below logic is use to add disable social app names to disable toggle.
                        holder.changeNotification(socialList.get(position).applicationInfo,isChecked,disableNotificationApps,context);

                        // Below logic is use to disable social app names for update existing (Current display) list
                        DisableAppList d=socialList.get(position);
                        d.ischecked=isChecked;
                        socialList.set(position,d);
                        holder.setData(isChecked);


                        // Below logic is use to maintain social section and cor-related items
                        if(!isChecked) {
                            int disableCount=launcherPrefs.getInt(CoreApplication.getInstance().SOCIAL_DISABLE_COUNT,0);
                            launcherPrefs.edit().putInt(CoreApplication.getInstance().SOCIAL_DISABLE_COUNT,disableCount+1).commit();
                        }
                        else{
                            int disableCount=launcherPrefs.getInt(CoreApplication.getInstance().SOCIAL_DISABLE_COUNT,0);
                            launcherPrefs.edit().putInt(CoreApplication.getInstance().SOCIAL_DISABLE_COUNT,disableCount-1).commit();
                        }

                        if(socialList.size() == launcherPrefs.getInt(CoreApplication.getInstance().SOCIAL_DISABLE_COUNT,0)){
                            changeHeaderNotification(section,false,disableSections,context);
                        }
                        else{
                            changeHeaderNotification(section,true,disableSections,context);
                        }


                    }
                }
            });
        }
        if(headerList.get(section).name.equals("Messengers")){
            holder.render(messengerList.get(position).applicationInfo.name);

            holder.displayImage(messengerList.get(position).applicationInfo,packageManager);
            holder.setData(messengerList.get(position).ischecked);
            holder.getToggle().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(holder.getToggle().isPressed()){
                        // Below logic is use to add disable app names to disable toggle.
                        holder.changeNotification(messengerList.get(position).applicationInfo,isChecked,disableNotificationApps,context);

                        // Below logic is use to disable app names for update existing (Current display) list
                        DisableAppList d=messengerList.get(position);
                        d.ischecked=isChecked;
                        messengerList.set(position,d);
                        holder.setData(isChecked);

                        // Below logic is use to maintain app section and cor-related items
                        if(!isChecked) {
                            int disableCount=launcherPrefs.getInt(CoreApplication.getInstance().MESSENGER_DISABLE_COUNT,0);
                            launcherPrefs.edit().putInt(CoreApplication.getInstance().MESSENGER_DISABLE_COUNT,disableCount+1).commit();
                        }
                        else{
                            int disableCount=launcherPrefs.getInt(CoreApplication.getInstance().MESSENGER_DISABLE_COUNT,0);
                            launcherPrefs.edit().putInt(CoreApplication.getInstance().MESSENGER_DISABLE_COUNT,disableCount-1).commit();

                        }

                        if(messengerList.size() == launcherPrefs.getInt(CoreApplication.getInstance().MESSENGER_DISABLE_COUNT,0)){
                            changeHeaderNotification(section,false,disableSections,context);
                        }
                        else{
                            changeHeaderNotification(section,true,disableSections,context);
                        }
                    }
                }
            });
        }
        if(headerList.get(section).name.equals("Other Apps")){
            holder.render(appList.get(position).applicationInfo.name);

            holder.displayImage(appList.get(position).applicationInfo,packageManager);
            holder.setData(appList.get(position).ischecked);
            holder.getToggle().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(holder.getToggle().isPressed()){
                        // Below logic is use to add disable app names to disable toggle.
                        holder.changeNotification(appList.get(position).applicationInfo,isChecked,disableNotificationApps,context);

                        // Below logic is use to disable app names for update existing (Current display) list
                        DisableAppList d=appList.get(position);
                        d.ischecked=isChecked;
                        appList.set(position,d);
                        holder.setData(isChecked);

                        // Below logic is use to maintain app section and cor-related items
                        if(!isChecked) {
                            int disableCount=launcherPrefs.getInt(CoreApplication.getInstance().APP_DISABLE_COUNT,0);
                            launcherPrefs.edit().putInt(CoreApplication.getInstance().APP_DISABLE_COUNT,disableCount+1).commit();
                        }
                        else{
                            int disableCount=launcherPrefs.getInt(CoreApplication.getInstance().APP_DISABLE_COUNT,0);
                            launcherPrefs.edit().putInt(CoreApplication.getInstance().APP_DISABLE_COUNT,disableCount-1).commit();
                        }

                        if(appList.size() == launcherPrefs.getInt(CoreApplication.getInstance().APP_DISABLE_COUNT,0)){
                            changeHeaderNotification(section,false,disableSections,context);
                        }
                        else{
                            changeHeaderNotification(section,true,disableSections,context);
                        }
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


    public void changeHeaderNotification(int position, boolean ischecked, ArrayList<String> disableHeaderApps, Context context){

        HeaderAppList headerAppList=headerList.get(position);

        SharedPreferences launcherPrefs = context.getSharedPreferences("Launcher3Prefs", 0);
        if(ischecked && disableHeaderApps.contains(headerAppList.name)){
            disableHeaderApps.remove(headerAppList.name);
        }
        if(!ischecked && !disableHeaderApps.contains(headerAppList.name)){
            disableHeaderApps.add(headerAppList.name);
        }
        String disableList = new Gson().toJson(disableHeaderApps);
        launcherPrefs.edit().putString(CoreApplication.getInstance().HEADER_APPLIST,disableList).commit();

        HeaderAppList d=headerList.get(position);
        d.ischecked=ischecked;
        headerList.set(position,d);

        notifyDataSetChanged();
    }


    public void changeAppNotification(ApplicationInfo applicationInfo, boolean ischecked, ArrayList<String> disableNotificationApps, Context context){

        SharedPreferences launcherPrefs = context.getSharedPreferences("Launcher3Prefs", 0);

        if(ischecked && disableNotificationApps.contains(applicationInfo.packageName)){
            disableNotificationApps.remove(applicationInfo.packageName);
        }

        if(!ischecked && !disableNotificationApps.contains(applicationInfo.packageName)){
            disableNotificationApps.add(applicationInfo.packageName);
        }

        String disableList = new Gson().toJson(disableNotificationApps);
        launcherPrefs.edit().putString(CoreApplication.getInstance().DISABLE_APPLIST,disableList).commit();
    }

}
