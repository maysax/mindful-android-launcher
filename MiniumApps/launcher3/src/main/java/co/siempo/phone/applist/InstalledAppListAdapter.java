package co.siempo.phone.applist;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import de.greenrobot.event.EventBus;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.Tracer;


class InstalledAppListAdapter extends RecyclerView.Adapter<InstalledAppListAdapter.ViewHolder> {
    private final Activity context;
    private PackageManager packageManager;
    private List<ApplicationInfo> arrayList;
    private LayoutInflater mInflater;
    private boolean isGrid;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView txt_app_name;
        private ImageView imv_appicon;
        public View layout;
        private View divider;
        private LinearLayout linearLayout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            divider = v.findViewById(R.id.divider);
            linearLayout = v.findViewById(R.id.linearList);
            txt_app_name = v.findViewById(R.id.txt_app_name);
            imv_appicon = v.findViewById(R.id.imv_appicon);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    InstalledAppListAdapter(Activity context, List<ApplicationInfo> arrayList, boolean isGrid) {
        this.context = context;
        packageManager = context.getPackageManager();
        this.arrayList = arrayList;
        this.isGrid = isGrid;
        mInflater = LayoutInflater.from(context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public InstalledAppListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v;
        if (isGrid)
            v = inflater.inflate(R.layout.installed_app_grid_row, parent, false);
        else
            v = inflater.inflate(R.layout.installed_app_list_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ApplicationInfo applicationInfo = arrayList.get(position);
        holder.txt_app_name.setText(applicationInfo.name);
        if (CoreApplication.getInstance().iconList.get(applicationInfo.packageName) == null) {
            holder.imv_appicon.setImageDrawable(applicationInfo.loadIcon(packageManager));
        } else {
            holder.imv_appicon.setImageBitmap(CoreApplication.getInstance().iconList.get(applicationInfo.packageName));
        }
        if (!isGrid) {
            if (position == arrayList.size() - 1) {
                holder.divider.setVisibility(View.GONE);
            } else {
                holder.divider.setVisibility(View.VISIBLE);
            }
        }
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Tracer.i("Opening package: " + applicationInfo.packageName);
                    new ActivityHelper(context).openAppWithPackageName(applicationInfo.packageName);
                    EventBus.getDefault().post(new AppOpenEvent(applicationInfo.packageName));
                    FirebaseHelper.getIntance().logAppUsage(applicationInfo.name,0);
                } catch (Exception e) {
                    Tracer.e(e, e.getMessage());
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}
