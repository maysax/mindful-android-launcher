package co.siempo.phone.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import co.siempo.phone.event.NotifySearchRefresh;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.models.AppListInfo;
import co.siempo.phone.service.LoadFavoritePane;
import co.siempo.phone.service.LoadJunkFoodPane;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.Sorting;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class JunkfoodFlaggingActivity extends CoreActivity implements AdapterView.OnItemClickListener {
    public Set<String> list = new HashSet<>();
    public boolean isLoadFirstTime = true;
    Set<String> favoriteList = new HashSet<>();
    JunkfoodFlaggingAdapter junkfoodFlaggingAdapter;
    List<String> installedPackageList;
    int firstPosition;
    boolean isClickOnView = true;
    private Toolbar toolbar;
    private ListView listAllApps;
    private PopupMenu popup;
    private List<AppListInfo> flagAppList = new ArrayList<>();
    private List<AppListInfo> unflageAppList = new ArrayList<>();
    private ArrayList<AppListInfo> bindingList = new ArrayList<>();
    private long startTime = 0;
    private boolean isFromAppMenu;
    private CardView cardView;
    private ImageView imgClear;
    private EditText edtSearch;
    private int positionPopUP;

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
        Intent intent = getIntent();
        if (intent.getExtras() != null && intent.hasExtra("FromAppMenu")) {
            isFromAppMenu = intent.getBooleanExtra("FromAppMenu", false);
        }

    }


    /**
     * Initialize the view.
     */
    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_flagging_screen);
        setSupportActionBar(toolbar);
        listAllApps = findViewById(R.id.listAllApps);
        cardView = findViewById(R.id.cardView);
        imgClear = findViewById(R.id.imgClear);
        edtSearch = findViewById(R.id.edtSearch);
        try {
            Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/robotoregular.ttf");
            edtSearch.setTypeface(myTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listAllApps.setOnItemClickListener(JunkfoodFlaggingActivity.this);
        edtSearch.clearFocus();
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (junkfoodFlaggingAdapter != null) {
                    junkfoodFlaggingAdapter.getFilter().filter(s.toString());
                }
                if (s.toString().length() > 0) {
                    imgClear.setVisibility(View.VISIBLE);
                } else {
                    imgClear.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
            }
        });
    }


    /**
     * load system apps and filter the application for junkfood and normal.
     */
    private void loadApps() {
        List<String> installedPackageListLocal = CoreApplication.getInstance().getPackagesList();
        Log.d("Junkfood", "" + installedPackageListLocal.size());
        List<String> appList = new ArrayList<>();
        installedPackageList = new ArrayList<>();
        appList.addAll(installedPackageListLocal);

        installedPackageList = appList;
//        new FilterApps(false).execute();
        bindData();
        if (PrefSiempo.getInstance(this).read(PrefSiempo.IS_JUNKFOOD_FIRSTTIME, true)) {
            PrefSiempo.getInstance(this).write(PrefSiempo.IS_JUNKFOOD_FIRSTTIME, false);
            showFirstTimeDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_junkfood_flagging, menu);
        MenuItem menuItem = menu.findItem(R.id.item_save);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(JunkfoodFlaggingActivity.this);
        builder.setTitle(getString(R.string.msg_congratulations));
        builder.setMessage(R.string.msg_flage_save_dialog);
        builder.setPositiveButton(R.string.strcontinue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        JunkfoodFlaggingActivity.this.overridePendingTransition(R
                                .anim.in_from_right_email, R.anim
                                .out_to_left_email);
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
        AlertDialog.Builder builder = new AlertDialog.Builder
                (JunkfoodFlaggingActivity.this);
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

        //Added this code as part of SSA-1333, to save the list on backpress
        super.onBackPressed();

        if (isFromAppMenu) {
            return;

        }
        JunkfoodFlaggingActivity.this.overridePendingTransition(R
                .anim.in_from_right_email, R.anim
                .out_to_left_email);


    }


    void updateFavoriteSortedMenu() {

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    /**
     * bind the list view of flag app and all apps.
     */
    private void bindData() {
        try {
            flagAppList = new ArrayList<>();
            unflageAppList = new ArrayList<>();
            bindingList = new ArrayList<>();
            junkfoodFlaggingAdapter = new JunkfoodFlaggingAdapter(this, bindingList);
            listAllApps.setAdapter(junkfoodFlaggingAdapter);
            for (String resolveInfo : installedPackageList) {
                if (!resolveInfo.equalsIgnoreCase(getPackageName())) {
                    String applicationname = CoreApplication.getInstance()
                            .getListApplicationName().get(resolveInfo);
                    if (!TextUtils.isEmpty(applicationname)) {
                        if (list.contains(resolveInfo)) {
                            flagAppList.add(new AppListInfo(resolveInfo, applicationname, false, false, true));
                        } else {
                            unflageAppList.add(new AppListInfo(resolveInfo, applicationname, false, false, false));
                        }
                    }
                }
            }
            //Code for removing the junk app from Favorite Sorted Menu and
            //Favorite List
            removeJunkAppsFromFavorites();

            if (flagAppList.size() == 0) {
                flagAppList.add(new AppListInfo("", "", true, true, true));
            } else {
                flagAppList.add(0, new AppListInfo("", "", true, false, true));
            }
            flagAppList = Sorting.sortApplication(flagAppList);
            bindingList.addAll(flagAppList);

            if (unflageAppList.size() == 0) {
                unflageAppList.add(new AppListInfo("", "", true, true, false));
            } else {
                unflageAppList.add(0, new AppListInfo("", "", true, false, false));
            }
            unflageAppList = Sorting.sortApplication(unflageAppList);
            bindingList.addAll(unflageAppList);

            if (junkfoodFlaggingAdapter != null) {
                junkfoodFlaggingAdapter.setData(bindingList);
                listAllApps.setSelection(firstPosition);
            }


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
     * @param packagename
     * @param isFlagApp
     */
    public void showPopUp(final View view, String packagename, final boolean isFlagApp) {
        positionPopUP = 0;
        for (int i = 0; i < bindingList.size(); i++) {
            if (bindingList.get(i).packageName.equalsIgnoreCase(packagename)) {
                positionPopUP = i;
                break;
            }
        }

        if (popup != null) {
            popup.dismiss();
        }
        popup = new PopupMenu(JunkfoodFlaggingActivity.this, view, Gravity.END);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item_Unflag) {
                    try {
                        if (isLoadFirstTime && isFlagApp) {
                            showAlertForFirstTime(positionPopUP, view);
                        } else {
                            popup.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showEmptyRowBeforeDelete(view);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (list.contains(bindingList.get(positionPopUP).packageName)) {
                                                list.remove(bindingList.get(positionPopUP).packageName);
                                            } else {
                                                list.add(bindingList.get(positionPopUP).packageName);
                                            }
                                            firstPosition = listAllApps.getFirstVisiblePosition();
//                                    bindData(true);
                                            new FilterApps().execute();
                                            listAllApps.setEnabled(true);
                                            listAllApps.setClickable(true);
                                        }
                                    }, 300);


                                }
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (item.getItemId() == R.id.item_Info) {
                    try {
                        PackageUtil.appSettings(JunkfoodFlaggingActivity.this, bindingList.get(positionPopUP).packageName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        popup.getMenuInflater().inflate(R.menu.junkfood_popup, popup.getMenu());
        MenuItem menuItem = popup.getMenu().findItem(R.id.item_Unflag);
        menuItem.setTitle(list.contains(bindingList.get(positionPopUP).packageName) ? getString(R.string.unflagapp) : getString(R.string.flag_app));
        popup.show();
        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                popup = null;
            }
        });
    }

    private void showEmptyRowBeforeDelete(View view) {
        listAllApps.setEnabled(false);
        listAllApps.setClickable(false);
        TextView textView = view.findViewById(R.id
                .txtAppName);
        ImageView imageView = view.findViewById(R
                .id.imgAppIcon);
        ImageView imageViewChevron = view
                .findViewById(R
                        .id.imgChevron);
        if (null != textView) {
            textView.setText("");
        }
        if (null != imageView) {
            imageView.setImageDrawable(null);
        }
        if (null != imageViewChevron) {
            imageViewChevron.setImageDrawable(null);
        }

    }

    /**
     * This dialog shows when user comes in this screen and user flag first application
     *
     * @param position
     */
    private void showAlertForFirstTime(final int position, final View itemView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(JunkfoodFlaggingActivity.this);
        builder.setTitle(getString(R.string.are_you_sure));
        builder.setMessage(R.string.msg_flag_first_time);
        builder.setPositiveButton(getString(R.string.yes_unhide), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isLoadFirstTime = false;

                        if (null != itemView) {
                            showEmptyRowBeforeDelete(itemView);
                        }
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (list.contains(bindingList.get(position).packageName)) {
                                    list.remove(bindingList.get(position).packageName);
                                } else {
                                    list.add(bindingList.get(position).packageName);
                                }
                                firstPosition = listAllApps.getFirstVisiblePosition();
//                                    bindData(true);
                                new FilterApps().execute();
                                listAllApps.setEnabled(true);
                                listAllApps.setClickable(true);
                            }
                        }, 200);

                    }
                });

            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isLoadFirstTime = false;
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
        list = PrefSiempo.getInstance(this).read(PrefSiempo.JUNKFOOD_APPS, new HashSet<String>());
        favoriteList = PrefSiempo.getInstance(this).read(PrefSiempo.FAVORITE_APPS, new HashSet<String>());
        favoriteList.removeAll(list);
        PrefSiempo.getInstance(JunkfoodFlaggingActivity.this).write(PrefSiempo.FAVORITE_APPS, favoriteList);
        loadApps();
        if (junkfoodFlaggingAdapter != null) {
            junkfoodFlaggingAdapter.getFilter().filter(edtSearch.getText().toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        favoriteList.removeAll(list);
        PrefSiempo.getInstance(JunkfoodFlaggingActivity.this).write(PrefSiempo.FAVORITE_APPS, favoriteList);
        PrefSiempo.getInstance(JunkfoodFlaggingActivity.this).write(PrefSiempo.JUNKFOOD_APPS, list);
        if (list.size() == 0 && !DashboardActivity.isJunkFoodOpen) {
            DashboardActivity.isJunkFoodOpen = true;
        }
        new LoadFavoritePane(JunkfoodFlaggingActivity.this).execute();
        new LoadJunkFoodPane(JunkfoodFlaggingActivity.this).execute();
        EventBus.getDefault().postSticky(new NotifySearchRefresh(true));
        FirebaseHelper.getInstance().logScreenUsageTime(this.getClass().getSimpleName(), startTime);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (popup != null) {
                popup.dismiss();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    class FilterApps extends AsyncTask<String, String, ArrayList<AppListInfo>> {

        FilterApps() {
            listAllApps.setOnItemClickListener(null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isClickOnView = false;
            flagAppList = new ArrayList<>();
            unflageAppList = new ArrayList<>();
            bindingList = new ArrayList<>();
        }

        @Override
        protected ArrayList<AppListInfo> doInBackground(String... strings) {
            for (String resolveInfo : installedPackageList) {
                if (!resolveInfo.equalsIgnoreCase(getPackageName())) {
                    String applicationname = CoreApplication.getInstance()
                            .getListApplicationName().get(resolveInfo);
                    if (!TextUtils.isEmpty(applicationname)) {
                        if (list.contains(resolveInfo)) {
                            flagAppList.add(new AppListInfo(resolveInfo, applicationname, false, false, true));
                        } else {
                            unflageAppList.add(new AppListInfo(resolveInfo, applicationname, false, false, false));
                        }
                    }
                }
            }
            //Code for removing the junk app from Favorite Sorted Menu and
            //Favorite List
            removeJunkAppsFromFavorites();

            if (flagAppList.size() == 0) {
                flagAppList.add(new AppListInfo("", "", true, true, true));
            } else {
                flagAppList.add(0, new AppListInfo("", "", true, false, true));
            }
            flagAppList = Sorting.sortApplication(flagAppList);
            bindingList.addAll(flagAppList);

            if (unflageAppList.size() == 0) {
                unflageAppList.add(new AppListInfo("", "", true, true, false));
            } else {
                unflageAppList.add(0, new AppListInfo("", "", true, false, false));
            }
            unflageAppList = Sorting.sortApplication(unflageAppList);
            bindingList.addAll(unflageAppList);
            return bindingList;
        }

        @Override
        protected void onPostExecute(ArrayList<AppListInfo> s) {
            super.onPostExecute(s);
            try {
                if (listAllApps != null) {
                    bindingList = s;
                    listAllApps.setOnItemClickListener(JunkfoodFlaggingActivity.this);
                    if (junkfoodFlaggingAdapter != null) {
                        junkfoodFlaggingAdapter.setData(bindingList);
                        edtSearch.setText("");
                        listAllApps.setSelection(firstPosition);
                    }
                    isClickOnView = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
