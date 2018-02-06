package co.siempo.phone.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.activities.InstalledAppsActivity;
import co.siempo.phone.db.DBClient;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.utils.PackageUtil;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.Tracer;


public class InstalledAppListAdapter extends RecyclerView.Adapter<InstalledAppListAdapter.ViewHolder> {
    private final Activity context;
    private final PackageManager packageManager;
    private final List<ApplicationInfo> arrayList;
    private final LayoutInflater mInflater;
    private final boolean isGrid;

    public InstalledAppListAdapter(Activity context, List<ApplicationInfo> arrayList, boolean isGrid) {
        this.context = context;
        packageManager = context.getPackageManager();
        this.arrayList = arrayList;
        this.isGrid = isGrid;
        mInflater = LayoutInflater.from(context);
    }

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
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ApplicationInfo applicationInfo = arrayList.get(position);
        holder.txt_app_name.setText(applicationInfo.name);
        if (CoreApplication.getInstance().iconList.get(applicationInfo.packageName) == null) {
            holder.img_icon.setImageDrawable(applicationInfo.loadIcon(packageManager));
        } else {
            holder.img_icon.setImageBitmap(CoreApplication.getInstance().iconList.get(applicationInfo.packageName));
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
                    new DBClient().deleteMsgByPackageName(applicationInfo.packageName);
                    FirebaseHelper.getIntance().logAppUsage(applicationInfo.name);
                } catch (Exception e) {
                    Tracer.e(e, e.getMessage());
                    CoreApplication.getInstance().logException(e);
                }
            }
        });


        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                View view = context.getLayoutInflater().inflate(R.layout
                        .installed_app_bottom_sheet, null);
                TextView txtDetail = view.findViewById(R.id.txt_detail);
                final TextView txtUninstall = view.findViewById(R.id.txt_uninstall);

                final Dialog mBottomSheetDialog = new Dialog(context, R.style
                        .MaterialDialogSheet);
                mBottomSheetDialog.setContentView(view);
                mBottomSheetDialog.setCancelable(true);
                mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
                mBottomSheetDialog.show();
                if (PackageUtil.isSystemApp(applicationInfo.packageName, context)) {
                    txtUninstall.setVisibility(View.GONE);
                } else {
                    txtUninstall.setVisibility(View.VISIBLE);
                }


                txtDetail.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            //Open the specific App Info page:
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + applicationInfo.packageName));
                            context.startActivity(intent);

                        } catch (ActivityNotFoundException e) {
                            Tracer.e(e, e.getMessage());
                            CoreApplication.getInstance().logException(e);
                            //Open the generic Apps page:
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                            context.startActivity(intent);

                        }
                        mBottomSheetDialog.dismiss();
                    }
                });


                txtUninstall.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {


                            Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                            intent.setData(Uri.parse("package:" + applicationInfo.packageName));
                            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                            context.startActivityForResult(intent,
                                    InstalledAppsActivity.UNINSTALL_APP_REQUEST_CODE);
                        } catch (Exception e) {
                            Tracer.e(e, e.getMessage());
                            CoreApplication.getInstance().logException(e);
                        }

                        mBottomSheetDialog.dismiss();

                    }
                });
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        if (arrayList != null)
            return arrayList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View layout;
        private final TextView txt_app_name;
        private final ImageView img_icon;
        private final View divider;
        private final LinearLayout linearLayout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            divider = v.findViewById(R.id.divider);
            linearLayout = v.findViewById(R.id.linearList);
            txt_app_name = v.findViewById(R.id.txt_app_name);
            img_icon = v.findViewById(R.id.imv_appicon);
        }
    }

}
