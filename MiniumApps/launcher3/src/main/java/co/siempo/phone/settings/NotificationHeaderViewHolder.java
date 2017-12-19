package co.siempo.phone.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.applist.HeaderAppList;

public class NotificationHeaderViewHolder extends RecyclerView.ViewHolder {

    @Bind({R.id.txt_headerName})
    TextView txt_headerName;

    @Bind({R.id.switch_headerNotification})
    Switch switch_headerNotification;

    @Bind({R.id.headerList})
    LinearLayout headerList;

    public NotificationHeaderViewHolder(View itemView) {
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

    public void showHeaderView(){
        headerList.setVisibility(View.VISIBLE);
    }


    public void hideHeaderView(){
        headerList.setVisibility(View.GONE);
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
        launcherPrefs.edit().putString(Constants.HEADER_APPLIST,disableList).commit();
        switch_headerNotification.setChecked(ischecked);


    }


}
