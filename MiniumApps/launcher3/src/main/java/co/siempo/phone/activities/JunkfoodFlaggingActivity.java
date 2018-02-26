package co.siempo.phone.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.adapters.JunkFoodFlagAdapter;
import co.siempo.phone.event.AppInstalledEvent;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.Sorting;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.Subscribe;

public class JunkfoodFlaggingActivity extends AppCompatActivity {
    Set<String> list = new HashSet<>();
    Set<String> favoriteList = new HashSet<>();
    private Toolbar toolbar;
    private ListView listFlaggedApps;
    private ListView listAllApps;
    private ArrayList<ResolveInfo> flagAppList = new ArrayList<>();
    private ArrayList<ResolveInfo> allAppList = new ArrayList<>();
    private JunkFoodFlagAdapter junkFoodFlagAdapter, junkFoodAllAppsAdapter;
    private PopupMenu popup;
    private ScrollView scrollView;
    private boolean isLoadFirstTime = true;

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.getInstalledOrRemoved() == 0) {
            loadApps();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_junkfood_flagging);
        initView();
        list = PrefSiempo.getInstance(this).read(PrefSiempo.JUNKFOOD_APPS, new HashSet<String>());
        favoriteList = PrefSiempo.getInstance(this).read(PrefSiempo.FAVORITE_APPS, new HashSet<String>());
        favoriteList.removeAll(list);
        PrefSiempo.getInstance(JunkfoodFlaggingActivity.this).write(PrefSiempo.FAVORITE_APPS, favoriteList);
        loadApps();
    }

    /**
     * Initialize the view.
     */
    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_flagging_screen);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color
                .colorAccent));
        listFlaggedApps = findViewById(R.id.listFlaggedApps);
        listAllApps = findViewById(R.id.listAllApps);
        scrollView = findViewById(R.id.scrollView);
    }

    /**
     * load system apps and filter the application for junkfood and normal.
     */
    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> installedPackageList = getPackageManager().queryIntentActivities(mainIntent, 0);
        flagAppList = new ArrayList<>();
        allAppList = new ArrayList<>();
        for (ResolveInfo resolveInfo : installedPackageList) {
            if (!resolveInfo.activityInfo.packageName.equalsIgnoreCase(getPackageName())) {
                if (list.contains(resolveInfo.activityInfo.packageName)) {
                    flagAppList.add(resolveInfo);
                } else {
                    allAppList.add(resolveInfo);
                }
            }
        }
        bindListView();
        if (PrefSiempo.getInstance(this).read(PrefSiempo.IS_APP_INSTALLED_FIRSTTIME, true)) {
            showFirstTimeDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_junkfood_flagging, menu);
        MenuItem menuItem = menu.findItem(R.id.item_save);
        setTextColorForMenuItem(menuItem, R.color.colorAccent);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showSaveDialog();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Show save dialog for saving the user filter data.
     */
    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(JunkfoodFlaggingActivity.this, R.style.AlertDialogTheme);
        builder.setTitle(getString(R.string.msg_congratulations));
        builder.setMessage(R.string.msg_flage_save_dialog);
        builder.setPositiveButton(R.string.strcontinue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                favoriteList.removeAll(list);
                flagAppList.remove(null);
                allAppList.remove(null);
                PrefSiempo.getInstance(JunkfoodFlaggingActivity.this).write(PrefSiempo.FAVORITE_APPS, favoriteList);
                PrefSiempo.getInstance(JunkfoodFlaggingActivity.this).write(PrefSiempo.JUNKFOOD_APPS, list);
                if (list.size() == 0 && !DashboardActivity.isJunkFoodOpen) {
                    DashboardActivity.isJunkFoodOpen = true;
                }
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.dialog_blue));
    }


    /**
     * Show save dialog for saving the user filter data.
     */
    private void showFirstTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(JunkfoodFlaggingActivity.this, R.style.AlertDialogTheme);
        builder.setTitle(getString(R.string.flag_app));
        builder.setMessage(R.string.flag_first_time_install);
        builder.setPositiveButton(R.string.gotit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PrefSiempo.getInstance(JunkfoodFlaggingActivity.this).write(PrefSiempo.IS_APP_INSTALLED_FIRSTTIME, false);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.dialog_blue));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (list.size() == 0 && !DashboardActivity.isJunkFoodOpen) {
            DashboardActivity.isJunkFoodOpen = true;
        }
    }

    /**
     * change text color of menuitem
     *
     * @param menuItem
     * @param color
     */
    private void setTextColorForMenuItem(MenuItem menuItem, @ColorRes int color) {
        SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, color)), 0, spanString.length(), 0);
        menuItem.setTitle(spanString);
    }

    /**
     * bind the list view of flag app and all apps.
     */
    private void bindListView() {
        try {
            if (allAppList.size() > 0) {
                allAppList = Sorting.sortAppAssignment(this, allAppList);
                listAllApps.setVisibility(View.VISIBLE);
                junkFoodAllAppsAdapter = new JunkFoodFlagAdapter(this, allAppList, false);
                listAllApps.setAdapter(junkFoodAllAppsAdapter);
                UIUtils.setDynamicHeight(this, listAllApps);
                listAllApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        showPopUp(view, false, position);
                    }
                });

            } else {
                allAppList.add(null);
                junkFoodAllAppsAdapter = new JunkFoodFlagAdapter(this, allAppList, false);
                listAllApps.setAdapter(junkFoodAllAppsAdapter);
                junkFoodAllAppsAdapter.notifyDataSetChanged();
                UIUtils.setDynamicHeight(this, listAllApps);
            }
            if (flagAppList.size() > 0) {
                flagAppList = Sorting.sortAppAssignment(this, flagAppList);
                listFlaggedApps.setVisibility(View.VISIBLE);
                junkFoodFlagAdapter = new JunkFoodFlagAdapter(this, flagAppList, true);
                listFlaggedApps.setAdapter(junkFoodFlagAdapter);
                junkFoodFlagAdapter.notifyDataSetChanged();
                UIUtils.setDynamicHeight(this, listFlaggedApps);
                listFlaggedApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        showPopUp(view, true, position);
                    }
                });

            } else {
                flagAppList.add(null);
                junkFoodFlagAdapter = new JunkFoodFlagAdapter(this, flagAppList, true);
                listFlaggedApps.setAdapter(junkFoodFlagAdapter);
                junkFoodFlagAdapter.notifyDataSetChanged();
                UIUtils.setDynamicHeight(this, listFlaggedApps);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * show pop dialog on List item click for flag/un-flag and appinfo.
     *
     * @param view
     * @param isFlagApp
     * @param position
     */
    private void showPopUp(View view, final boolean isFlagApp, final int position) {
        if (popup != null) {
            popup.dismiss();
        }
        popup = new PopupMenu(JunkfoodFlaggingActivity.this, view, Gravity.END);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item_Unflag) {
                    try {
                        if (isFlagApp) {
                            if (list.contains(flagAppList.get(position).activityInfo.packageName)) {
                                popup.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        list.remove(flagAppList.get(position).activityInfo.packageName);
                                        isLoadFirstTime = false;
                                        allAppList.add(flagAppList.get(position));
                                        flagAppList.remove(flagAppList.get(position));
                                        bindListView();
                                    }
                                });

                            }
                        } else {
                            if (isLoadFirstTime) {
                                showAlertForFirstTime(position);
                            } else {
                                popup.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        list.add(allAppList.get(position).activityInfo.packageName);
                                        isLoadFirstTime = false;
                                        flagAppList.add(allAppList.get(position));
                                        allAppList.remove(allAppList.get(position));
                                        bindListView();
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (item.getItemId() == R.id.item_Info) {
                    try {
                        if (isFlagApp) {
                            ResolveInfo resolveInfo = flagAppList.get(position);
                            PackageUtil.appSettings(JunkfoodFlaggingActivity.this, resolveInfo.activityInfo.packageName);
                        } else {
                            ResolveInfo resolveInfo = allAppList.get(position);
                            PackageUtil.appSettings(JunkfoodFlaggingActivity.this, resolveInfo.activityInfo.packageName);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        popup.getMenuInflater().inflate(R.menu.junkfood_popup, popup.getMenu());
        MenuItem menuItem = popup.getMenu().findItem(R.id.item_Unflag);
        menuItem.setTitle(isFlagApp ? getString(R.string.unflagapp) : getString(R.string.flag_app));
        popup.show();
        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                popup = null;
            }
        });
    }

    /**
     * This dialog shows when user comes in this screen and user flag first application
     *
     * @param position
     */
    private void showAlertForFirstTime(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(JunkfoodFlaggingActivity.this, R.style.AlertDialogTheme);
        builder.setTitle(getString(R.string.are_you_sure));
        builder.setMessage(R.string.msg_flag_first_time);
        builder.setPositiveButton(getString(R.string.yes_unhide), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        list.add(allAppList.get(position).activityInfo.packageName);
                        isLoadFirstTime = false;
                        flagAppList.add(allAppList.get(position));
                        allAppList.remove(allAppList.get(position));
                        bindListView();
                    }
                });

            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.dialog_blue));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.dialog_red));
    }
}
