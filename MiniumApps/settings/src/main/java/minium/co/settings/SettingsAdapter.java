package minium.co.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * Created by hardik on 16/8/17.
 */

public class SettingsAdapter extends ArrayAdapter<String> {


    private Context context;
    private List<String> lst_settings = null;

    public SettingsAdapter(Context context, List<String> lst_settings) {
        super(context, 0);
        this.context = context;
        this.lst_settings = lst_settings;
    }

    @Override
    public int getCount() {
        return lst_settings.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return lst_settings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);

            convertView = inflater.inflate(R.layout.list_main_settings, parent, false);
            holder.txt_settingsName = (TextView) convertView.findViewById(R.id.txt_settingsName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String name = lst_settings.get(position);

        if (!TextUtils.isEmpty(name)) {
            holder.txt_settingsName.setText(name);
        }

        return convertView;
    }


    private static class ViewHolder {
        TextView txt_settingsName;
    }

}