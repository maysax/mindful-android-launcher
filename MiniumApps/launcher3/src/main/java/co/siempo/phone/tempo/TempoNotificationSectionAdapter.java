package co.siempo.phone.tempo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.applist.DisableAppList;
import co.siempo.phone.applist.HeaderAppList;
import co.siempo.phone.settings.NoticationFooterViewHolder;
import co.siempo.phone.settings.SectionedRecyclerViewAdapter;

public class TempoNotificationSectionAdapter extends SectionedRecyclerViewAdapter<TempoNotificationHeaderViewHolder,
        TempoNotificationItemViewHolder,
        NoticationFooterViewHolder> {

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
        final List<String> messengerAppList = new ArrayList<>();
        messengerAppList.addAll(Arrays.asList(context.getResources().getStringArray(R.array.messengerAppList)));

        if (headerList.get(section).name.equals("All Other Apps")) {
            holder.render(blockedList.get(position).applicationInfo.name);
            holder.displayImage(blockedList.get(position).applicationInfo, packageManager);

            holder.getToggle().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    final DisableAppList d = blockedList.get(position);
                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.tempo_notification_popup, popup.getMenu());
                    MenuItem menuItem = popup.getMenu().findItem(R.id.block);
                    menuItem.setTitle("Unblock app notifications");

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {


                            if (!showUnblockAlert) {

                                holder.addToBlockList(blockedList.get(position).applicationInfo, true, blockedApps, context);
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
//                                alertDialog.setCanceledOnTouchOutside(false);


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
        } else if (headerList.get(section).name.equals("Human Direct Messaging")) {
            holder.render(messengerList.get(position).applicationInfo.name);

            holder.displayImage(messengerList.get(position).applicationInfo, packageManager);
            holder.getToggle().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.tempo_notification_popup, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            holder.addToBlockList(messengerList.get(position).applicationInfo, false, blockedApps, context);
                            DisableAppList d = messengerList.get(position);
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


        if (headerList.get(section).name.equals("Helpful Robots")) {
            holder.render(appList.get(position).applicationInfo.name);

            holder.displayImage(appList.get(position).applicationInfo, packageManager);

            holder.getToggle().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.tempo_notification_popup, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            holder.addToBlockList(appList.get(position).applicationInfo, false, blockedApps, context);
                            DisableAppList d = appList.get(position);
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


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void changeHeaderNotification(int position, boolean ischecked, ArrayList<String> disableHeaderApps, Context context) {

        HeaderAppList headerAppList = headerList.get(position);

        SharedPreferences launcherPrefs = context.getSharedPreferences("Launcher3Prefs", 0);
        if (ischecked && null != disableHeaderApps && disableHeaderApps
                .contains(headerAppList
                        .name)) {
            disableHeaderApps.remove(headerAppList.name);
        }
        if (!ischecked && null != disableHeaderApps && !disableHeaderApps.contains(headerAppList.name)) {
            disableHeaderApps.add(headerAppList.name);
        }
        String disableList = "";
        if (null != disableHeaderApps) {
            disableList = new Gson().toJson(disableHeaderApps);
        }

        launcherPrefs.edit().putString(Constants.HEADER_APPLIST, disableList).commit();

        HeaderAppList d = headerList.get(position);
        d.ischecked = ischecked;
        headerList.set(position, d);

        Collections.sort(blockedList, new Comparator<DisableAppList>() {
            @Override
            public int compare(DisableAppList o1, DisableAppList o2) {
                return o1.applicationInfo.name.compareToIgnoreCase(o2.applicationInfo.name);
            }
        });

        Collections.sort(messengerList, new Comparator<DisableAppList>() {
            @Override
            public int compare(DisableAppList o1, DisableAppList o2) {
                return o1.applicationInfo.name.compareToIgnoreCase(o2.applicationInfo.name);
            }
        });
        Collections.sort(appList, new Comparator<DisableAppList>() {
            @Override
            public int compare(DisableAppList o1, DisableAppList o2) {
                return o1.applicationInfo.name.compareToIgnoreCase(o2.applicationInfo.name);
            }
        });

        notifyDataSetChanged();
    }


}
