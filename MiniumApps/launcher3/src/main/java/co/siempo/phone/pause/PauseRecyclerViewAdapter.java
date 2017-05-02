package co.siempo.phone.pause;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import co.siempo.phone.R;
import de.greenrobot.event.EventBus;

/**
 * Created by tkb on 2017-02-24.
 */

public class PauseRecyclerViewAdapter extends RecyclerView.Adapter<PauseRecyclerViewAdapter.ViewHolder>{

    ArrayList<PauseDataModel> SubjectValues;
    Context context;
    View view1;
    ViewHolder viewHolder1;

    public PauseRecyclerViewAdapter(Context context1, ArrayList<PauseDataModel> SubjectValues){

        this.SubjectValues = SubjectValues;
        context = context1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;
        public CheckBox option_checkbox;
        public ViewHolder(View v){

            super(v);

            textView = (TextView)v.findViewById(R.id.name_textview);
            option_checkbox = (CheckBox)v.findViewById(R.id.option_checkbox);
        }
    }

    @Override
    public PauseRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        view1 = LayoutInflater.from(context).inflate(R.layout.pause_pref_row,parent,false);

        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){

        holder.textView.setText(SubjectValues.get(position).getName());
        holder.option_checkbox.setChecked(SubjectValues.get(position).getStatus());
        holder.option_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SubjectValues.get(position).setStatus(b);
                EventBus.getDefault().post(new PausePreferenceEvent(SubjectValues.get(position)));
            }
        });
    }

    @Override
    public int getItemCount(){

        return SubjectValues.size();
    }
}