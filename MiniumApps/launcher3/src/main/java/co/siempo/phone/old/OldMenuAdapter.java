package co.siempo.phone.old;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.activities.SiempoSettingsDefaultAppActivity;
import co.siempo.phone.app.Constants;
import co.siempo.phone.models.MainListItem;
import minium.co.core.app.CoreApplication;

/**
 * Created by Shahab on 2/23/2017.
 */

public class OldMenuAdapter extends ArrayAdapter<MainListItem> {

    private Context context;

    private List<MainListItem> data = null;
    private List<ApplicationInfo> packagesList;

    public OldMenuAdapter(Context context, List<MainListItem> items) {
        super(context, 0);
        this.context = context;
        loadData(items);
        getInstalledPackges(context);
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
                } else if (menuId == Constants.NOTES_PACKAGE) {
                    packageName = siempoSettingsDefaultAppActivity.prefs.notesPackage().get();
                }
                if (!packageName.equalsIgnoreCase("Notes")) {
                    String strAppName = getApplicationNameFromPackageName(packageName);
                    if (strAppName.equalsIgnoreCase("")) {
                        holder.textDefaultApp.setText("Default: Not Set");
                    } else {
                        holder.textDefaultApp.setText("Default: " + strAppName);
                    }
                } else {
                    holder.textDefaultApp.setText("Default: " + context.getResources().getString(R.string.siempo_note));
                }
            } else {
                holder.textDefaultApp.setVisibility(View.GONE);
            }

        }

        return convertView;
    }

    private void getInstalledPackges(Context context) {
        packagesList = new ArrayList<>();
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        packagesList.addAll(packages);
    }

    private String getApplicationNameFromPackageName(String packagename) {
        try {
            if (packagename != null && !packagename.equalsIgnoreCase("")) {
                for (ApplicationInfo applicationInfo : packagesList) {
                    if (applicationInfo.packageName.equalsIgnoreCase(packagename)) {
                        return "" + applicationInfo.loadLabel(context.getPackageManager());
                    }
                }
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            e.printStackTrace();
        }
        return "";
    }

    private static class ItemHolder {
        ImageView icon;
        TextView text, textDefaultApp;
    }
}
