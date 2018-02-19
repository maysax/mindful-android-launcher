package co.siempo.phone.adapters.viewholder;

import android.content.Context;
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
import co.siempo.phone.models.AppListInfo;
import co.siempo.phone.utils.PrefSiempo;

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

    public void changeNotification(AppListInfo headerAppList, boolean ischecked, ArrayList<String> disableHeaderApps, Context context) {
        if (ischecked && disableHeaderApps.contains(headerAppList.headerName)) {
            disableHeaderApps.remove(headerAppList.headerName);
        }
        if (!ischecked && !disableHeaderApps.contains(headerAppList.headerName)) {
            disableHeaderApps.add(headerAppList.headerName);
        }

        String disableList = new Gson().toJson(disableHeaderApps);
        PrefSiempo.getInstance(context).write(PrefSiempo.HEADER_APPLIST, disableList);
//        launcherPrefs.edit().putString(Constants.HEADER_APPLIST, disableList).apply();
        switch_headerNotification.setChecked(ischecked);


    }


}
