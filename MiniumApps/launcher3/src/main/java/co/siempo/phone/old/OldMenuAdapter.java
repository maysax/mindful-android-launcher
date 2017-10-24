package co.siempo.phone.old;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;

import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.model.MainListItem;
import co.siempo.phone.settings.SiempoSettingsDefaultAppActivity;
import minium.co.core.app.CoreApplication;

/**
 * Created by Shahab on 2/23/2017.
 */

public class OldMenuAdapter extends ArrayAdapter<MainListItem> {

    private Context context;

    private List<MainListItem> data = null;

    public OldMenuAdapter(Context context, List<MainListItem> items) {
        super(context, 0);
        this.context = context;
        loadData(items);
    }

    private void loadData(List<MainListItem> items) {
        this.data = items;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Nullable
    @Override
    public MainListItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ItemHolder holder;

        if (convertView == null) {
            holder = new ItemHolder();
            LayoutInflater inflater = LayoutInflater.from(context);

            convertView = inflater.inflate(R.layout.list_item, parent, false);
            holder.icon = convertView.findViewById(R.id.icon);
            holder.text = convertView.findViewById(R.id.text);
            holder.textDefaultApp = convertView.findViewById(R.id.textDefaultApp);

            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        MainListItem item = getItem(position);

        if (item != null) {
            holder.text.setText(item.getTitle());
            holder.icon.setImageDrawable(new IconDrawable(context, item.getIcon())
                    .colorRes(R.color.text_primary)
                    .sizeDp(18));
            int menuId = item.getId();
            if (context instanceof SiempoSettingsDefaultAppActivity) {
                holder.textDefaultApp.setVisibility(View.VISIBLE);
                SiempoSettingsDefaultAppActivity siempoSettingsDefaultAppActivity = (SiempoSettingsDefaultAppActivity) context;
                String packageName = "";
                if (menuId == Constants.CALL_PACKAGE) {
                    packageName = siempoSettingsDefaultAppActivity.prefs.callPackage().get();
                } else if (menuId == Constants.MESSAGE_PACKAGE) {
                    packageName = siempoSettingsDefaultAppActivity.prefs.messagePackage().get();
                } else if (menuId == Constants.CALENDER_PACKAGE) {
                    packageName = siempoSettingsDefaultAppActivity.prefs.calenderPackage().get();
                } else if (menuId == Constants.CONTACT_PACKAGE) {
                    packageName = siempoSettingsDefaultAppActivity.prefs.contactPackage().get();
                } else if (menuId == Constants.MAP_PACKAGE) {
                    packageName = siempoSettingsDefaultAppActivity.prefs.mapPackage().get();
                } else if (menuId == Constants.PHOTOS_PACKAGE) {
                    packageName = siempoSettingsDefaultAppActivity.prefs.photosPackage().get();
                } else if (menuId == Constants.CAMERA_PACKAGE) {
                    packageName = siempoSettingsDefaultAppActivity.prefs.cameraPackage().get();
                } else if (menuId == Constants.BROWSER_PACKAGE) {
                    packageName = siempoSettingsDefaultAppActivity.prefs.browserPackage().get();
                } else if (menuId == Constants.CLOCK_PACKAGE) {
                    packageName = siempoSettingsDefaultAppActivity.prefs.clockPackage().get();
                } else if (menuId == Constants.EMAIL_PACKAGE) {
                    packageName = siempoSettingsDefaultAppActivity.prefs.emailPackage().get();
                }
                String strAppName = CoreApplication.getInstance().getApplicationNameFromPackageName(packageName);
                Log.d("App Name : ", strAppName);
                if (strAppName.equalsIgnoreCase("")) {
                    holder.textDefaultApp.setText("Default: Not Set");
                } else {
                    holder.textDefaultApp.setText("Default: "+strAppName);
                }

            } else {
                holder.textDefaultApp.setVisibility(View.GONE);
            }

        }

        return convertView;
    }

    private static class ItemHolder {
        ImageView icon;
        TextView text, textDefaultApp;
    }
}
