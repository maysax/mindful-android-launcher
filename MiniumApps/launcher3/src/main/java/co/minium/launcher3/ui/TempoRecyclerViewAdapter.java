package co.minium.launcher3.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import co.minium.launcher3.R;

/**
 * Created by tkb on 2017-02-24.
 */

public class TempoRecyclerViewAdapter extends RecyclerView.Adapter<TempoRecyclerViewAdapter.ViewHolder>{

    String[] SubjectValues;
    Context context;
    View view1;
    ViewHolder viewHolder1;

    public TempoRecyclerViewAdapter(Context context1, String[] SubjectValues1){

        SubjectValues = SubjectValues1;
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
    public TempoRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        view1 = LayoutInflater.from(context).inflate(R.layout.pause_pref_row,parent,false);

        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){

        holder.textView.setText(SubjectValues[position]);
        holder.option_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(context,""+SubjectValues[position]+" Checked: "+b,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount(){

        return SubjectValues.length;
    }
}