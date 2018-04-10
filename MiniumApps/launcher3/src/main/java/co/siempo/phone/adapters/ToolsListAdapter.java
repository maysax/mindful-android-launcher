package co.siempo.phone.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.siempo.phone.R;
import co.siempo.phone.activities.AppAssignmentActivity;
import co.siempo.phone.activities.ToolSelectionActivity;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PrefSiempo;

/**
 * Created by RajeshJadi on 14/2/18.
 */

public class ToolsListAdapter extends RecyclerView.Adapter<ToolsListAdapter
        .ToolsViewHolder> {
    private ArrayList<MainListItem> listItems;
    private HashMap<Integer, AppMenu> map;
    private Context context;

    public ToolsListAdapter(Context context, ArrayList<MainListItem> listItems) {
        this.context = context;
        this.listItems = listItems;
        map = CoreApplication.getInstance().getToolsSettings();
    }

    public HashMap<Integer, AppMenu> getMap() {
        return map;
    }

    public void refreshEvents(List<MainListItem> listItems1) {
        map = CoreApplication.getInstance().getToolsSettings();
        listItems = new ArrayList<>();
        listItems.addAll(listItems1);
        notifyDataSetChanged();
    }


    @Override
    public ToolsListAdapter.ToolsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .tools_app_list_row, parent, false);
        return new ToolsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ToolsListAdapter.ToolsViewHolder holder, final int position) {
        final MainListItem mainListItem = listItems.get(position);
        if (mainListItem != null) {
            final boolean isVisible = mainListItem.isVisable();
            holder.txtAppName.setText(mainListItem.getTitle());
            bindView(mainListItem, holder, isVisible);
            holder.txtAssignApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AppAssignmentActivity.class);
                    intent.putExtra(Constants.INTENT_MAINLISTITEM, mainListItem);
                    ((ToolSelectionActivity) context).startActivityForResult(intent, ToolSelectionActivity.TOOL_SELECTION);
                }
            });

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.checkbox.isChecked()) {
                        if (getCountOfCheckTools() > 1) {
                            mainListItem.setVisable(false);
                            map.get(mainListItem.getId()).setVisible(false);
                            bindView(mainListItem, holder, false);
                        }
                    } else {
                        mainListItem.setVisable(true);
                        map.get(mainListItem.getId()).setVisible(true);
                        bindView(mainListItem, holder, true);

                        if (map.get(mainListItem.getId()).getApplicationName().equalsIgnoreCase("")) {
                            String hashMapToolSettings = new Gson().toJson(map);
                            PrefSiempo.getInstance(context).write(PrefSiempo.TOOLS_SETTING, hashMapToolSettings);
                            Intent intent = new Intent(context, AppAssignmentActivity.class);
                            intent.putExtra(Constants.INTENT_MAINLISTITEM, mainListItem);
                            ((ToolSelectionActivity) context).startActivityForResult(intent, ToolSelectionActivity.TOOL_SELECTION);
                        }
                    }

                }
            });
        }
    }

    private void bindView(MainListItem mainListItem, ToolsViewHolder holder, boolean isVisible) {

        if (map.get(mainListItem.getId()).getApplicationName().equalsIgnoreCase("")) {
            holder.txtAssignApp.setText(context.getString(R.string.assign_app));
        } else {
            if (map.get(mainListItem.getId()).getApplicationName().equalsIgnoreCase("Notes")) {
                holder.txtAssignApp.setText("Note");
            } else {
                holder.txtAssignApp.setText(CoreApplication.getInstance().getApplicationNameFromPackageName(map.get(mainListItem.getId()).getApplicationName()));
            }
        }

        switch (mainListItem.getId()) {
            case 1:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_menu_map_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_vector_map));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            case 2:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_tranport_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_transport));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            case 3:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_calender_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_calendar));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            case 4:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_weather_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_cloud));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            case 5:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_notes_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_vector_note));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            case 6:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_recorder_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_vector_recorder));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            case 7:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_camera_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_vector_camera));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            case 8:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_photos_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_vector_photo));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            case 9:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_payment_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_vector_payment));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            case 10:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_wellness_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_vector_wellness));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            case 11:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_browser_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_vector_browser));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            case 13:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_call_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_vector_call));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            case 14:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_clock_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_vector_clock));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            case 15:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_msg_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_messages));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            case 16:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_mail_white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setBackground(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_email));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.dialog_title));
                }
                break;
            default:
                break;
        }
    }

    private int getCountOfCheckTools() {
        int count = 0;
        for (MainListItem mainListItem : listItems) {
            if (mainListItem.isVisable())
                count++;
        }
        return count;
    }

    private int getCountOfAssignTools() {
        int count = 0;
        for (Map.Entry<Integer, AppMenu> entry : map.entrySet()) {
            if (entry.getValue().isVisible() && !entry.getValue().getApplicationName().equalsIgnoreCase(""))
                count++;
        }
        return count;
    }

    private int getAssignToolsId() {
        int count = -1;
        for (Map.Entry<Integer, AppMenu> entry : map.entrySet()) {
            if (entry.getValue().isVisible() && !entry.getValue().getApplicationName().equalsIgnoreCase(""))
                return entry.getKey();
        }
        return count;
    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ToolsViewHolder extends RecyclerView.ViewHolder {

        public final CheckBox checkbox;
        private final TextView txtAppName;
        private final ImageView imgAppIcon;
        private final TextView txtAssignApp;
        private final LinearLayout linearLayout;

        public ToolsViewHolder(View v) {
            super(v);
            linearLayout = v.findViewById(R.id.linearList);
            txtAppName = v.findViewById(R.id.txtAppName);
            txtAssignApp = v.findViewById(R.id.txtAssignApp);
            imgAppIcon = v.findViewById(R.id.imgAppIcon);
            checkbox = v.findViewById(R.id.checkbox);
        }
    }


}