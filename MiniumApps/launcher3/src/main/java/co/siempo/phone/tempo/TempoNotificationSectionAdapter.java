package co.siempo.phone.tempo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.applist.DisableAppList;
import co.siempo.phone.applist.HeaderAppList;
import co.siempo.phone.settings.NoticationFooterViewHolder;
import co.siempo.phone.settings.SectionedRecyclerViewAdapter;
import minium.co.core.log.Tracer;
/**
 *
 * Below adapter is use to Display the section wise below apps
 *  1. Humand Direct Messaging
 *  2. Helpful Robots
 *  3. All Other Apps
 */
public class TempoNotificationSectionAdapter extends SectionedRecyclerViewAdapter<TempoNotificationHeaderViewHolder,
        TempoNotificationItemViewHolder,
        NoticationFooterViewHolder> {

    private List<String> messengerAppList = new ArrayList<>();;
    protected Context context = null;
    SharedPreferences launcherPrefs;
    ArrayList<String> disableNotificationApps = new ArrayList<>();
    ArrayList<String> blockedApps = new ArrayList<>();
    ArrayList<String> disableSections = new ArrayList<>();
    AlertDialog alertDialog;
    private PackageManager packageManager;
    private List<DisableAppList> appList, blockedList, messengerList;
    private List<HeaderAppList> headerList;
    private boolean showUnblockAlert = true;
    PopupMenu popup;


    public TempoNotificationSectionAdapter(Context context, List<DisableAppList> appList, List<DisableAppList> messengerList, List<DisableAppList> blockedList, List<HeaderAppList> headerList) {
        this.context = context;
        this.appList = appList;
        this.blockedList = blockedList;
        this.headerList = headerList;
        this.messengerList = messengerList;
        this.packageManager = context.getPackageManager();
        launcherPrefs = context.getSharedPreferences("Launcher3Prefs", 0);

        String disable_AppList = launcherPrefs.getString(Constants.DISABLE_APPLIST, "");
        if (!TextUtils.isEmpty(disable_AppList)) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            disableNotificationApps = new Gson().fromJson(disable_AppList, type);
        }


        String block_AppList = launcherPrefs.getString(Constants.BLOCKED_APPLIST, "");
        if (!TextUtils.isEmpty(block_AppList)) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            blockedApps = new Gson().fromJson(block_AppList, type);
        }

        String disable_HeaderAppList = launcherPrefs.getString(Constants.HEADER_APPLIST, "");
        if (!TextUtils.isEmpty(disable_HeaderAppList)) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            disableSections = new Gson().fromJson(disable_HeaderAppList, type);
        }

        messengerAppList = new ArrayList<>();
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        List<ResolveInfo> messagingResolveList = context.getPackageManager()
                .queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : messagingResolveList) {
            messengerAppList.add(resolveInfo.activityInfo.packageName);
        }
    }

    @Override
    protected int getItemCountForSection(int section) {
        int size = 0;
        if (headerList.get(section).name.equals("All Other Apps")) {
            size = blockedList.size();
        }
        if (headerList.get(section).name.equals("Human Direct Messaging")) {
            size = messengerList.size();
        }
        if (headerList.get(section).name.equals("Helpful Robots")) {
            size = appList.size();
        }
        return size;
    }

    @Override
    protected int getSectionCount() {
        return headerList.size();
    }

    @Override
    protected boolean hasFooterInSection(int section) {
        return false;
    }

    protected LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(context);
    }

    @Override
    protected TempoNotificationHeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.tempo_section_header_layout, parent, false);
        return new TempoNotificationHeaderViewHolder(view);
    }

    @Override
    protected NoticationFooterViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.view_count_footer, parent, false);
        return new NoticationFooterViewHolder(view);
    }

    @Override
    protected TempoNotificationItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.tempo_installed_app_list_row, parent, false);
        return new TempoNotificationItemViewHolder(view);
    }

    @Override
    protected void onBindSectionHeaderViewHolder(final TempoNotificationHeaderViewHolder holder, final int section) {

        holder.render(headerList.get(section).name);

        if (headerList.get(section).name.equalsIgnoreCase("All Other Apps")) {
            holder.getHeaderToggle().setText("(BLOCKED)");
        } else {
            holder.getHeaderToggle().setText("(OK)");
        }


    }

    @Override
    protected void onBindSectionFooterViewHolder(NoticationFooterViewHolder holder, int section) {
    }


    @Override
    protected void onBindItemViewHolder(final TempoNotificationItemViewHolder holder, final int section, final int position) {

        holder.displayToggle();

        if (headerList.get(section).name.equals("All Other Apps")) {

            final DisableAppList otherAppsItems=blockedList.get(position);
            if(!TextUtils.isEmpty(otherAppsItems.errorMessage)){
                holder.render(otherAppsItems.errorMessage);
                holder.disableViews();
            }
            else{
                holder.enableViews();
                holder.render(otherAppsItems.applicationInfo.name);
                holder.displayImage(otherAppsItems.applicationInfo, packageManager,otherAppsItems.errorMessage);

                holder.getToggle().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        final DisableAppList d = otherAppsItems;
                        if(popup!=null){
                            popup.dismiss();
                        }
                        popup = new PopupMenu(context, v);
                        popup.getMenuInflater().inflate(R.menu.tempo_notification_popup, popup.getMenu());
                        MenuItem menuItem = popup.getMenu().findItem(R.id.block);
                        menuItem.setTitle("Unblock app notifications");

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {


                                if (!showUnblockAlert) {

                                    holder.addToBlockList(d.applicationInfo, true, blockedApps, context);
                                    blockedList.remove(d);
                                    if (messengerAppList.contains(d.applicationInfo.packageName)) {
                                        messengerList.add(d);
                                        int disableCount = launcherPrefs.getInt(Constants.MESSENGER_DISABLE_COUNT, 0);
                                        launcherPrefs.edit().putInt(Constants.MESSENGER_DISABLE_COUNT, disableCount - 1).commit();
                                    } else {
                                        appList.add(d);
                                        int disableCount = launcherPrefs.getInt(Constants.APP_DISABLE_COUNT, 0);
                                        launcherPrefs.edit().putInt(Constants.APP_DISABLE_COUNT, disableCount - 1).commit();
                                    }


                                    changeHeaderNotification(section, true, disableSections, context);
                                } else {

                                    showUnblockAlert = false;
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                                            .setMessage("Siempo won't block this app's notifications, but they might be blocked by your Android system settings.")
                                            .setCancelable(false)

                                            .setPositiveButton("OK", null)
                                            .setNegativeButton("OPEN SYSTEM " +
                                                    "SETTINGS", null);

                                    alertDialog = alertDialogBuilder.create();
                                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                                                      @Override
                                                                      public void onShow(DialogInterface dialog) {
                                                                          alertDialog.getButton(AlertDialog
                                                                                  .BUTTON_NEGATIVE)
                                                                                  .setOnClickListener(new View.OnClickListener() {
                                                                                      @Override
                                                                                      public void onClick(View v) {
                                                                                          alertDialog.dismiss();
                                                                                          Intent intent = new Intent();
                                                                                          intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

                                                                                          //for Android 5-7
                                                                                          if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT <= 25) {
                                                                                              intent.putExtra("app_package", d.applicationInfo.packageName);
                                                                                              intent.putExtra("app_uid", d.applicationInfo.uid);
                                                                                          } else if (Build.VERSION.SDK_INT >= 26) {
                                                                                          // for Android O
                                                                                              intent.putExtra("android.provider.extra.APP_PACKAGE", d.applicationInfo.packageName);
                                                                                          }
                                                                                          context.startActivity(intent);
                                                                                      }
                                                                                  });

                                                                          alertDialog.getButton(AlertDialog
                                                                                  .BUTTON_POSITIVE)
                                                                                  .setOnClickListener(new View.OnClickListener() {
                                                                                      @Override
                                                                                      public void onClick(View v) {
                                                                                          alertDialog.dismiss();
                                                                                          holder.addToBlockList(blockedList.get(position).applicationInfo, true, blockedApps, context);
                                                                                          DisableAppList d = blockedList.get(position);
                                                                                          blockedList.remove(d);
                                                                                          if (messengerAppList.contains(d.applicationInfo.packageName)) {
                                                                                              messengerList.add(d);
                                                                                              int disableCount = launcherPrefs.getInt(Constants.MESSENGER_DISABLE_COUNT, 0);
                                                                                              launcherPrefs.edit().putInt(Constants.MESSENGER_DISABLE_COUNT, disableCount - 1).commit();
                                                                                          } else {
                                                                                              appList.add(d);
                                                                                              int disableCount = launcherPrefs.getInt(Constants.APP_DISABLE_COUNT, 0);
                                                                                              launcherPrefs.edit().putInt(Constants.APP_DISABLE_COUNT, disableCount - 1).commit();
                                                                                          }
                                                                                          changeHeaderNotification(section, true, disableSections, context);
                                                                                      }
                                                                                  });
                                                                      }
                                                                  }


                                    );

                                    alertDialog.show();
                                }
                                return true;

                            }
                        });

                        popup.show();
                    }
                });
            }

        } else if (headerList.get(section).name.equals("Human Direct Messaging")) {

            final DisableAppList messengerAppsItem=messengerList.get(position);
            if(!TextUtils.isEmpty(messengerAppsItem.errorMessage)){
                holder.render(messengerAppsItem.errorMessage);
                holder.disableViews();
            }
            else{
                holder.render(messengerAppsItem.applicationInfo.name);
                holder.enableViews();
                holder.displayImage(messengerAppsItem.applicationInfo, packageManager,messengerAppsItem.errorMessage);
                holder.getToggle().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popup!=null){
                            popup.dismiss();
                        }
                        popup = new PopupMenu(context, v);
                        popup.getMenuInflater().inflate(R.menu.tempo_notification_popup, popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                holder.addToBlockList(messengerAppsItem.applicationInfo, false, blockedApps, context);
                                DisableAppList d = messengerAppsItem;
                                messengerList.remove(d);
                                blockedList.add(d);
                                int disableCount = launcherPrefs.getInt(Constants.MESSENGER_DISABLE_COUNT, 0);
                                launcherPrefs.edit().putInt(Constants.MESSENGER_DISABLE_COUNT, disableCount + 1).commit();
                                changeHeaderNotification(section, true, disableSections, context);
                                return true;
                            }
                        });

                        popup.show();


                    }
                });

            }


        }


        if (headerList.get(section).name.equals("Helpful Robots")) {
            final DisableAppList appListItem=appList.get(position);
            if(!TextUtils.isEmpty(appListItem.errorMessage)){
                holder.render(appListItem.errorMessage);
                holder.disableViews();
            }
            else{
                holder.enableViews();
                holder.render(appListItem.applicationInfo.name);

                holder.displayImage(appListItem.applicationInfo, packageManager,appListItem.errorMessage);

                holder.getToggle().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popup!=null){
                            popup.dismiss();
                        }
                        popup = new PopupMenu(context, v);
                        popup.getMenuInflater().inflate(R.menu.tempo_notification_popup, popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                holder.addToBlockList(appListItem.applicationInfo, false, blockedApps, context);
                                DisableAppList d = appListItem;
                                appList.remove(d);
                                blockedList.add(d);
                                int disableCount = launcherPrefs.getInt(Constants.APP_DISABLE_COUNT, 0);
                                launcherPrefs.edit().putInt(Constants.APP_DISABLE_COUNT, disableCount + 1).commit();
                                changeHeaderNotification(section, true, disableSections, context);
                                return true;
                            }
                        });

                        popup.show();


                    }
                });
            }

        }


    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * This method is used to remove apps from Open Category to Blocked List - true
     * Or to move Blocked app to normal open app - false
     *
     *
     * @param position
     * @param ischecked
     * @param disableHeaderApps
     * @param context
     */
    public void changeHeaderNotification(int position, boolean ischecked, ArrayList<String> disableHeaderApps, Context context) {

        HeaderAppList headerAppList = headerList.get(position);

        SharedPreferences launcherPrefs = context.getSharedPreferences("Launcher3Prefs", 0);


        try{
            if(messengerList.size()>1) {
                for (int i=0;i<messengerList.size();i++)
                    if(!TextUtils.isEmpty(messengerList.get(i).errorMessage)) {
                        messengerList.remove(messengerList.get(i));
                    }
            }

            if(appList.size()>1) {
                for (int i=0;i<appList.size();i++)
                    if(!TextUtils.isEmpty(appList.get(i).errorMessage)) {
                        appList.remove(appList.get(i));
                    }
            }

            if(blockedList.size()>1) {
                for (int i=0;i<blockedList.size();i++)
                    if(!TextUtils.isEmpty(blockedList.get(i).errorMessage)) {
                        blockedList.remove(blockedList.get(i));
                    }
            }
        }
        catch (Exception e){
            Tracer.d("Exception in remove error message");
        }


        if(messengerList.size()>0 ) {
            Collections.sort(messengerList, new Comparator<DisableAppList>() {
                @Override
                public int compare(DisableAppList o1, DisableAppList o2) {
                    return o1.applicationInfo.name.compareToIgnoreCase(o2.applicationInfo.name);
                }
            });
        }

        if(appList.size()>0) {
            Collections.sort(appList, new Comparator<DisableAppList>() {
                @Override
                public int compare(DisableAppList o1, DisableAppList o2) {
                    return o1.applicationInfo.name.compareToIgnoreCase(o2.applicationInfo.name);
                }
            });
        }

        if(blockedList.size()>0) {

            Collections.sort(blockedList, new Comparator<DisableAppList>() {
                @Override
                public int compare(DisableAppList o1, DisableAppList o2) {
                    return o1.applicationInfo.name.compareToIgnoreCase(o2.applicationInfo.name);
                }
            });
        }


        if(messengerList.size() == 0){
            DisableAppList d= new DisableAppList();
            d.errorMessage=context.getResources().getString(R.string.msg_no_apps);
            messengerList.add(d);
        }

        if(appList.size() == 0){
            DisableAppList d= new DisableAppList();
            d.errorMessage=context.getResources().getString(R.string.msg_no_apps);
            appList.add(d);
        }

        if(blockedList.size() == 0){
            DisableAppList d= new DisableAppList();
            d.errorMessage=context.getResources().getString(R.string.msg_no_apps);
            blockedList.add(d);
        }


        notifyDataSetChanged();
    }


    public void validationMessage(){

    }
}
