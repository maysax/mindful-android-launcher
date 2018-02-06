package co.siempo.phone.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.siempo.phone.R;

/**
 * Created by roma on 6/2/18.
 */

public class ToolsListAdapter extends RecyclerView.Adapter<ToolsListAdapter
        .ToolsViewHolder> {

    ArrayList<String> planetList;

    public ToolsListAdapter(ArrayList<String> planetList, Context context) {
        this.planetList = planetList;
    }

    @Override
    public ToolsListAdapter.ToolsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .tools_app_list_row, parent, false);
        ToolsViewHolder viewHolder = new ToolsViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ToolsListAdapter.ToolsViewHolder holder, int position) {
//        holder.image.setImageResource(R.drawable.planetimage);
        holder.txt_app_name.setText(planetList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return planetList.size();
    }

    public void updateList(ArrayList<String> list) {
        planetList = list;
        notifyDataSetChanged();
    }

    public static class ToolsViewHolder extends RecyclerView.ViewHolder {

        public final View layout;
        private final TextView txt_app_name;
        private final ImageView img_icon;
        private final View divider;
        private final LinearLayout linearLayout;

        public ToolsViewHolder(View v) {
            super(v);
            layout = v;
            divider = v.findViewById(R.id.divider);
            linearLayout = v.findViewById(R.id.linearList);
            txt_app_name = v.findViewById(R.id.txt_app_name);
            img_icon = v.findViewById(R.id.imv_appicon);
        }
    }


}