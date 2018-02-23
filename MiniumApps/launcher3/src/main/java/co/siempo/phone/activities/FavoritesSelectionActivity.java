package co.siempo.phone.activities;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.adapters.FavoritesFlagAdapter;
import co.siempo.phone.event.AppInstalledEvent;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.Sorting;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.Subscribe;

public class FavoritesSelectionActivity extends AppCompatActivity {
    Set<String> list = new HashSet<>();
    Set<String> junkFoodList = new HashSet<>();
    private Toolbar toolbar;
    private TextView txtValidateFavoritesApps;
    private ListView listFavoriteApps;
    private TextView txtValidateAllOtherApps;
    private ListView listAllOtherApps;
    private ArrayList<ResolveInfo> favoriteAppList = new ArrayList<>();
    private ArrayList<ResolveInfo> allOtherAppList = new ArrayList<>();
    private FavoritesFlagAdapter favoritesFlagAdapter, favoritesAllAppsAdapter;
    private PopupMenu popup;
    private ScrollView scrollView;
    private boolean isLoadFirstTime = true;
    private int count = 0;

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.getInstalledOrRemoved() == 0) {
            loadApps();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_selection);
        list = PrefSiempo.getInstance(this).read(PrefSiempo.FAVORITE_APPS, new HashSet<String>());

        junkFoodList = PrefSiempo.getInstance(this).read(PrefSiempo.JUNKFOOD_APPS, new HashSet<String>());

        list.removeAll(junkFoodList);

        initView();
        loadApps();
    }

    /**
     * Initialize the view.
     */
    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color
                .colorAccent));

        txtValidateFavoritesApps = findViewById(R.id.txtValidateFavoritesApps);
        txtValidateAllOtherApps = findViewById(R.id.txtValidateAllOtherApps);
        listFavoriteApps = findViewById(R.id.lst_favoritesApps);
        listAllOtherApps = findViewById(R.id.lst_OtherApps);
        scrollView = findViewById(R.id.scrollView);
        count = list.size();
        setToolBarText(count);
    }

    public void setToolBarText(int count) {
        int remainapps = 12 - count;
        toolbar.setTitle("Select up to " + remainapps + " more apps");
    }

    /**
     * load system apps and filter the application for junkfood and normal.
     */
    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> installedPackageList = getPackageManager().queryIntentActivities(mainIntent, 0);
        favoriteAppList = new ArrayList<>();
        allOtherAppList = new ArrayList<>();
        for (ResolveInfo resolveInfo : installedPackageList) {
            if (!resolveInfo.activityInfo.packageName.equalsIgnoreCase(getPackageName())) {
                if (!junkFoodList.contains(resolveInfo.activityInfo.packageName)) {
                    if (list.contains(resolveInfo.activityInfo.packageName)) {
                        favoriteAppList.add(resolveInfo);
                    } else {
                        allOtherAppList.add(resolveInfo);
                    }
                }
            }
        }
        bindListView();
        setToolBarText(favoriteAppList.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_junkfood_flagging, menu);
        MenuItem menuItem = menu.findItem(R.id.item_save);
        setTextColorForMenuItem(menuItem, R.color.colorAccent);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                PrefSiempo.getInstance(FavoritesSelectionActivity.this).write(PrefSiempo.FAVORITE_APPS, list);
                finish();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
            if (allOtherAppList.size() > 0) {
                allOtherAppList = Sorting.sortAppAssignment(this, allOtherAppList);
                txtValidateAllOtherApps.setVisibility(View.GONE);
                listAllOtherApps.setVisibility(View.VISIBLE);
                favoritesAllAppsAdapter = new FavoritesFlagAdapter(this, allOtherAppList);
                listAllOtherApps.setAdapter(favoritesAllAppsAdapter);
                UIUtils.setDynamicHeight(listAllOtherApps);
                listAllOtherApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        showPopUp(view, false, position);
                    }
                });

            } else {
                listAllOtherApps.setVisibility(View.GONE);
                txtValidateAllOtherApps.setVisibility(View.VISIBLE);
            }
            if (favoriteAppList.size() > 0) {
                favoriteAppList = Sorting.sortAppAssignment(this, favoriteAppList);
                txtValidateFavoritesApps.setVisibility(View.GONE);
                listFavoriteApps.setVisibility(View.VISIBLE);
                favoritesFlagAdapter = new FavoritesFlagAdapter(this, favoriteAppList);
                listFavoriteApps.setAdapter(favoritesFlagAdapter);
                favoritesFlagAdapter.notifyDataSetChanged();
                UIUtils.setDynamicHeight(listFavoriteApps);
                listFavoriteApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        showPopUp(view, true, position);
                    }
                });

            } else {
                txtValidateFavoritesApps.setVisibility(View.VISIBLE);
                listFavoriteApps.setVisibility(View.GONE);
            }

            if (isLoadFirstTime || favoriteAppList.size() == 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                }, 100);

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
        popup = new PopupMenu(FavoritesSelectionActivity.this, view, Gravity.END);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item_Unflag) {
                    try {
                        if (isFlagApp) {
                            if (list.contains(favoriteAppList.get(position).activityInfo.packageName)) {
                                list.remove(favoriteAppList.get(position).activityInfo.packageName);

                                //get the JSON array of the ordered of sorted customers
                                String jsonListOfSortedFavorites = PrefSiempo.getInstance(FavoritesSelectionActivity.this).read(PrefSiempo.FAVORITE_SORTED_MENU, "");
                                //convert onNoteListChangedJSON array into a List<Long>
                                Gson gson1 = new Gson();
                                List<String> listOfSortFavoritesApps = gson1.fromJson(jsonListOfSortedFavorites, new TypeToken<List<String>>() {
                                }.getType());

                                for (ListIterator<String> it =
                                     listOfSortFavoritesApps.listIterator(); it.hasNext
                                        (); ) {
                                    String packageName = it.next();
                                    if (favoriteAppList.get(position).activityInfo.packageName.equalsIgnoreCase(packageName)) {
                                        //Used List Iterator to set empty
                                        // value for package name retaining
                                        // the positions of elements
                                        it.set("");
                                    }
                                }


                                Gson gson2 = new Gson();
                                String jsonListOfFavoriteApps = gson2.toJson(listOfSortFavoritesApps);
                                PrefSiempo.getInstance(FavoritesSelectionActivity.this).write(PrefSiempo.FAVORITE_SORTED_MENU, jsonListOfFavoriteApps);


                                isLoadFirstTime = false;
                                allOtherAppList.add(favoriteAppList.get(position));
                                favoriteAppList.remove(favoriteAppList.get(position));
                                bindListView();
                                setToolBarText(favoriteAppList.size());
                            }
                        } else {
                            if (favoriteAppList != null && favoriteAppList.size() < 12) {
                                list.add(allOtherAppList.get(position).activityInfo.packageName);
                                isLoadFirstTime = false;
                                favoriteAppList.add(allOtherAppList.get(position));
                                allOtherAppList.remove(allOtherAppList.get(position));
                                bindListView();
                            } else {
                                Toast.makeText(getApplicationContext(), "Please unselect any of the apps from Frequently used apps section", Toast.LENGTH_LONG).show();
                            }
                            setToolBarText(favoriteAppList.size());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (item.getItemId() == R.id.item_Info) {
                    try {
                        if (isFlagApp) {
                            ResolveInfo resolveInfo = favoriteAppList.get(position);
                            PackageUtil.appSettings(FavoritesSelectionActivity.this, resolveInfo.activityInfo.packageName);
                        } else {
                            ResolveInfo resolveInfo = allOtherAppList.get(position);
                            PackageUtil.appSettings(FavoritesSelectionActivity.this, resolveInfo.activityInfo.packageName);
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
        menuItem.setTitle(isFlagApp ? getString(R.string.favorite_menu_unselect) : getString(R.string.favorite_menu_select));
        popup.show();
        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                popup = null;
            }
        });
    }


}
