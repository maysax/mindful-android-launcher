package co.siempo.phone.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private final int dividerColor;
    private ArrayList<MainListItem> listItems;
    private HashMap<Integer, AppMenu> map;
    private Context context;
    private int defaultTextColor;

    public ToolsListAdapter(Context context, ArrayList<MainListItem>
            listItems, HashMap<Integer, AppMenu> mapList) {
        this.context = context;
        this.listItems = listItems;
        map = mapList;
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.icon_color, typedValue, true);
        defaultTextColor = typedValue.resourceId;
        theme.resolveAttribute(R.attr.divider_tools, typedValue, true);
        dividerColor = typedValue.resourceId;

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
                    intent.putExtra("class_name", ToolSelectionActivity.class.getSimpleName
                            ().toString());
                    ((ToolSelectionActivity) context).startActivityForResult(intent, ToolSelectionActivity.TOOL_SELECTION);
                }
            });

            if (holder.checkbox.isChecked()) {
                holder.imgAppIcon.setColorFilter(ContextCompat.getColor(context, R.color
                        .white), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {

                holder.imgAppIcon.setColorFilter(ContextCompat.getColor
                        (context, defaultTextColor), android.graphics.PorterDuff.Mode
                        .SRC_IN);
            }

            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        holder.imgAppIcon.setColorFilter(ContextCompat.getColor(context, R.color
                                .white), android.graphics.PorterDuff.Mode.SRC_IN);
                    } else {

                        holder.imgAppIcon.setColorFilter(ContextCompat.getColor
                                (context, defaultTextColor), android.graphics.PorterDuff.Mode
                                .SRC_IN);
                    }
                }
            });


            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.checkbox.isChecked()) {
                        if (getCountOfCheckTools() > 1) {
                            mainListItem.setVisable(false);
//                            map.get(mainListItem.getId()).setVisible(false);
                            bindView(mainListItem, holder, false);
                        }
                    } else {
//                        if (getCountOfCheckTools() < 16) {
//
//                            int id = ((ToolSelectionActivity) context).check();
//                            if (id != 0) {
//                                ((ToolSelectionActivity) context).replace(id, mainListItem.getId());
//                            }
                        mainListItem.setVisable(true);
//                        map.get(mainListItem.getId()).setVisible(true);
                        bindView(mainListItem, holder, true);
                        if (map.get(mainListItem.getId()).getApplicationName().equalsIgnoreCase("")) {
                            String hashMapToolSettings = new Gson().toJson(map);
                            PrefSiempo.getInstance(context).write(PrefSiempo.TOOLS_SETTING, hashMapToolSettings);
                            Intent intent = new Intent(context, AppAssignmentActivity.class);
                            intent.putExtra(Constants.INTENT_MAINLISTITEM, mainListItem);
                            intent.putExtra("class_name", ToolSelectionActivity.class.getSimpleName
                                    ().toString());
                            ((ToolSelectionActivity) context).startActivityForResult(intent, ToolSelectionActivity.TOOL_SELECTION);
                        }
//                        } else {
//                            UIUtils.toastShort(context, "You cannot select " +
//                                    "more than 16 tools");
//                        }
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
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_menu_map_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat
                            .getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_map));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor
                            (context, defaultTextColor));
                }
                break;
            case 2:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_tranport_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_transport));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 3:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_calender_white));

                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_calendar));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 4:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_weather_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_cloud));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 5:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_notes_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_note));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 6:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_recorder_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_recorder));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 7:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_camera_white));

                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_camera));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 8:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_photos_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_photo));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 9:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_payment_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_payment));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 10:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_wellness_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));

                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_wellness));

                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor
                            (context, defaultTextColor));
                }
                break;
            case 12:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_white_todo));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_todo));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;


            case 11:


                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_browser_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_browser));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 13:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_call_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_call));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 14:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_clock_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_clock));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 15:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_msg_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_messages));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 16:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_menu_mail_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_email));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;

            case 17:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_music_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_music));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 18:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_podcast_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_podcast));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 19:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_food_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_food));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
                }
                break;
            case 20:
                if (isVisible) {
                    holder.txtAssignApp.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    holder.checkbox.setChecked(true);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_fitness_white));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, R.color.white));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    holder.txtAssignApp.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    holder.checkbox.setChecked(false);
                    holder.imgAppIcon.setImageDrawable(ContextCompat.getDrawable
                            (context, R.drawable.ic_vector_fitness));
                    holder.viewDivider.setBackgroundColor(ContextCompat.getColor
                            (context, dividerColor));
                    holder.txtAppName.setTextColor(ContextCompat.getColor(context, defaultTextColor));
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
        private final View viewDivider;
        private final ImageView imgAppIcon;
        private final TextView txtAssignApp;
        private final LinearLayout linearLayout;

        public ToolsViewHolder(View v) {
            super(v);
            linearLayout = v.findViewById(R.id.linearList);
            viewDivider = v.findViewById(R.id.viewDivider);
            txtAppName = v.findViewById(R.id.txtAppName);
            txtAssignApp = v.findViewById(R.id.txtAssignApp);
            imgAppIcon = v.findViewById(R.id.imgAppIcon);
            checkbox = v.findViewById(R.id.checkbox);
        }
    }


}
