package co.siempo.phone.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.adapters.JunkfoodFlaggingAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.AppInstalledEvent;
import co.siempo.phone.event.HomePressEvent;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.models.AppListInfo;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.Sorting;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.Subscribe;

public class JunkfoodFlaggingActivity extends CoreActivity {
    Set<String> list = new HashSet<>();
    Set<String> favoriteList = new HashSet<>();
    JunkfoodFlaggingAdapter junkfoodFlaggingAdapter;
    int firstPosition;
    List<ResolveInfo> installedPackageList;
    private Toolbar toolbar;
    private ListView listAllApps;
    private PopupMenu popup;
    private boolean isLoadFirstTime = true;
    private List<AppListInfo> flagAppList = new ArrayList<>();
    private List<AppListInfo> unflageAppList = new ArrayList<>();
    private ArrayList<AppListInfo> bindingList = new ArrayList<>();
    private long startTime = 0;
    private Window mWindow;
    private int defaultStatusBarColor;

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isAppInstalledSuccessfully()) {
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
        listAllApps = findViewById(R.id.listAllApps);
    }


    /**
     * load system apps and filter the application for junkfood and normal.
     */
    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        installedPackageList = getPackageManager().queryIntentActivities(mainIntent, 0);

        bindData(false);
        if (PrefSiempo.getInstance(this).read(PrefSiempo.IS_JUNKFOOD_FIRSTTIME, true)) {
            PrefSiempo.getInstance(this).write(PrefSiempo.IS_JUNKFOOD_FIRSTTIME, false);
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
                dialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        favoriteList.removeAll(list);
                        PrefSiempo.getInstance(JunkfoodFlaggingActivity.this).write(PrefSiempo.FAVORITE_APPS, favoriteList);
                        PrefSiempo.getInstance(JunkfoodFlaggingActivity.this).write(PrefSiempo.JUNKFOOD_APPS, list);
                        if (list.size() == 0 && !DashboardActivity.isJunkFoodOpen) {
                            DashboardActivity.isJunkFoodOpen = true;
                        }
                        finish();
                    }
                });

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
        builder.setTitle(getString(R.string.flag_app_first_time));
        builder.setMessage(R.string.flag_first_time_install);
        builder.setPositiveButton(R.string.gotit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
    private void bindData(boolean isNotify) {
        try {
            flagAppList = new ArrayList<>();
            unflageAppList = new ArrayList<>();
            bindingList = new ArrayList<>();
            for (ResolveInfo resolveInfo : installedPackageList) {
                if (!resolveInfo.activityInfo.packageName.equalsIgnoreCase(getPackageName())) {
                    if (list.contains(resolveInfo.activityInfo.packageName)) {
                        flagAppList.add(new AppListInfo(resolveInfo.activityInfo.packageName, false, false, true));
                    } else {
                        unflageAppList.add(new AppListInfo(resolveInfo.activityInfo.packageName, false, false, false));
                    }
                }
            }


            //Code for removing the junk app from Favorite Sorted Menu and
            //Favorite List
            removeJunkAppsFromFavorites();


            if (flagAppList.size() == 0) {
                flagAppList.add(new AppListInfo("", true, true, true));
            } else {
                flagAppList.add(0, new AppListInfo("", true, false, true));
            }
            flagAppList = Sorting.sortApplication(flagAppList);
            bindingList.addAll(flagAppList);

            if (unflageAppList.size() == 0) {
                unflageAppList.add(new AppListInfo("", true, true, false));
            } else {
                unflageAppList.add(0, new AppListInfo("", true, false, false));
            }
            unflageAppList = Sorting.sortApplication(unflageAppList);
            bindingList.addAll(unflageAppList);
            junkfoodFlaggingAdapter = new JunkfoodFlaggingAdapter(this, bindingList, list);
            listAllApps.setAdapter(junkfoodFlaggingAdapter);
            if (isNotify) {
                junkfoodFlaggingAdapter.notifyDataSetChanged();
                listAllApps.setSelection(firstPosition);
            }

            listAllApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!bindingList.get(position).packageName.equalsIgnoreCase("")) {
                        showPopUp(view, position);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove the junk apps from Favorite Sorted menu and Favorite list
     */
    private void removeJunkAppsFromFavorites() {
        String jsonListOfSortedFavorites = PrefSiempo.getInstance(JunkfoodFlaggingActivity.this)
                .read(PrefSiempo.FAVORITE_SORTED_MENU, "");
        Set<String> favlist = PrefSiempo.getInstance(this).read(PrefSiempo
                .FAVORITE_APPS, new HashSet<String>());
        //convert onNoteListChangedJSON array into a List<Long>
        Gson gson1 = new Gson();
        List<String> listOfSortFavoritesApps = gson1.fromJson(jsonListOfSortedFavorites, new TypeToken<List<String>>() {
        }.getType());

        for (String junkString : list) {
            if (favlist != null && favlist.contains(junkString)) {

                for (ListIterator<String> it =
                     listOfSortFavoritesApps.listIterator(); it.hasNext
                        (); ) {
                    String packageName = it.next();
                    if (junkString.equalsIgnoreCase(packageName)) {
                        //Used List Iterator to set empty
                        // value for package name retaining
                        // the positions of elements
                        it.set("");
                    }
                }

            }
        }

        Gson gson2 = new Gson();
        String jsonListOfFavoriteApps = gson2.toJson(listOfSortFavoritesApps);
        PrefSiempo.getInstance(JunkfoodFlaggingActivity.this).write(PrefSiempo
                .FAVORITE_SORTED_MENU, jsonListOfFavoriteApps);
        PrefSiempo.getInstance(JunkfoodFlaggingActivity.this).write(PrefSiempo.FAVORITE_APPS,
                favlist);
    }

    /**
     * show pop dialog on List item click for flag/un-flag and application information.
     *
     * @param view
     * @param position
     */
    private void showPopUp(View view, final int position) {
        if (popup != null) {
            popup.dismiss();
        }
        popup = new PopupMenu(JunkfoodFlaggingActivity.this, view, Gravity.END);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item_Unflag) {
                    try {
                        if (isLoadFirstTime) {
                            showAlertForFirstTime(position);
                        } else {
                            popup.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (list.contains(bindingList.get(position).packageName)) {
                                        list.remove(bindingList.get(position).packageName);
                                    } else {
                                        list.add(bindingList.get(position).packageName);
                                    }
                                    firstPosition = listAllApps.getFirstVisiblePosition();
                                    bindData(true);
                                }
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (item.getItemId() == R.id.item_Info) {
                    try {
                        PackageUtil.appSettings(JunkfoodFlaggingActivity.this, bindingList.get(position).packageName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        popup.getMenuInflater().inflate(R.menu.junkfood_popup, popup.getMenu());
        MenuItem menuItem = popup.getMenu().findItem(R.id.item_Unflag);
        menuItem.setTitle(list.contains(bindingList.get(position).packageName) ? getString(R.string.unflagapp) : getString(R.string.flag_app));
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
                        if (list.contains(bindingList.get(position).packageName)) {
                            list.remove(bindingList.get(position).packageName);
                        } else {
                            list.add(bindingList.get(position).packageName);
                        }
                        isLoadFirstTime = false;
                        firstPosition = listAllApps.getFirstVisiblePosition();
                        bindData(true);
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

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        //Loading apps here to refresh data in case user goes to app info and
        // disables app
        loadApps();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getInstance().logScreenUsageTime(this.getClass().getSimpleName(), startTime);
    }

    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        try {
            if (event.isVisible() && UIUtils.isMyLauncherDefault(this)) {
                onBackPressed();
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startActivity(startMain);

            }

        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }
    }


}
