package co.siempo.phone.settings;

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

import co.siempo.phone.R;
import co.siempo.phone.model.SettingsData;

/**
 * Created by hardik on 16/8/17.
 */

@SuppressWarnings("ALL")
public class SettingsAdapter extends ArrayAdapter<SettingsData> {


    private Context context;
    private List<SettingsData> lst_settings = null;

    public SettingsAdapter(Context context, List<SettingsData> lst_settings) {
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
    public SettingsData getItem(int position) {
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

        String name = lst_settings.get(position).getSettingType();

        if (!TextUtils.isEmpty(name)) {
            holder.txt_settingsName.setText(name);
        }

        return convertView;
    }


    private static class ViewHolder {
        TextView txt_settingsName;
    }

}