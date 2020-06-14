package co.siempo.phone.adapters.viewholder;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import co.siempo.phone.R;
import co.siempo.phone.models.AppListInfo;
import co.siempo.phone.utils.PrefSiempo;

public class TempoNotificationHeaderViewHolder extends RecyclerView.ViewHolder {
    final TextView txt_headerName;
    final TextView switch_headerNotification;
    final LinearLayout headerList;

    public TempoNotificationHeaderViewHolder(View itemView) {
        super(itemView);
        txt_headerName = itemView.findViewById(R.id.txt_headerName);
        switch_headerNotification = itemView.findViewById(R.id.txtHeaderlabel);
        headerList = itemView.findViewById(R.id.headerList);
    }

    public void render(String text) {
        txt_headerName.setText("" + text);
    }

    public TextView getHeaderToggle() {
        return switch_headerNotification;
    }

    public void showHeaderView() {
        headerList.setVisibility(View.VISIBLE);
    }

    public void hideHeaderView() {
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
//        switch_headerNotification.setChecked(ischecked);
    }
}
