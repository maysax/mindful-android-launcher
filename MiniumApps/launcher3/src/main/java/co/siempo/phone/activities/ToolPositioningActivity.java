package co.siempo.phone.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import co.siempo.phone.R;
import co.siempo.phone.adapters.ToolPositioningAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.customviews.ItemOffsetDecoration;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.interfaces.OnToolItemListChangedListener;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.main.OnStartDragListener;
import co.siempo.phone.main.SimpleItemTouchHelperCallback;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.service.LoadToolPane;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;

public class ToolPositioningActivity extends CoreActivity implements OnToolItemListChangedListener,
        OnStartDragListener {
    public static ArrayList<MainListItem> sortedList = new ArrayList<>();
    HashMap<Integer, AppMenu> map = new HashMap<>();
    LinearLayout linMain;
    private ArrayList<MainListItem> items = new ArrayList<>();
    private ArrayList<MainListItem> topItems = new ArrayList<>();
    private ArrayList<MainListItem> bottomItems = new ArrayList<>();
    private ToolPositioningAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemOffsetDecoration itemDecoration;
    private ItemTouchHelper mItemTouchHelper;
    private Parcelable mListState;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TextView txtSelectTools;
    private RelativeLayout relTop;
    private LinearLayout linearTop;
    private long startTime = 0;
    private RelativeLayout relMain;
    private ImageView imgBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_positioning);
        relMain = findViewById(R.id.relMain);
        linMain = findViewById(R.id.linMain);
        imgBackground = findViewById(R.id.imgBackground);
        String filePath = PrefSiempo.getInstance(this).read(PrefSiempo
                .DEFAULT_BAG, "");

        try {
            if (!TextUtils.isEmpty(filePath)) {
                //Code for Applying background
                Glide.with(this)
                        .load(Uri.fromFile(new File(filePath))) // Uri of the
                        // picture
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgBackground);
                linMain.setBackgroundColor(ContextCompat.getColor(this, R.color
                        .trans_black_bg));
            } else {

                imgBackground.setImageBitmap(null);
                imgBackground.setBackground(null);
                linMain.setBackgroundColor(ContextCompat.getColor(this, R.color
                        .transparent));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        StatusBarUtil.setTransparent(this);
        boolean read = PrefSiempo.getInstance(this).read(PrefSiempo.IS_DARK_THEME, false);
        if (read) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.white));
                getWindow().getDecorView().setSystemUiVisibility(View
                        .SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
//        String jsonListOfSortedToolsId = PrefSiempo.getInstance(this).read
//                (PrefSiempo.SORTED_MENU, "");
//        Log.d("MenuItem", jsonListOfSortedToolsId);
//        //check for null
//        if (!jsonListOfSortedToolsId.isEmpty()) {
//            //convert onNoteListChangedJSON array into a List<Long>
//            Gson gson = new GsonBuilder()
//                    .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
//            List<Long> listOfSortedCustomersId = gson.fromJson(jsonListOfSortedToolsId, new TypeToken<List<Long>>() {
//            }.getType());
//
//            if (listOfSortedCustomersId.size() > 16) {
//                listOfSortedCustomersId.remove(12);
//                listOfSortedCustomersId.remove(13);
//                listOfSortedCustomersId.remove(14);
//                listOfSortedCustomersId.remove(15);
//
//                String jsonListOfSortedCustomerIds = gson.toJson
//                        (listOfSortedCustomersId);
//                PrefSiempo.getInstance(this).write(PrefSiempo.SORTED_MENU, jsonListOfSortedCustomerIds);
//            }
//
//
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        map = CoreApplication.getInstance().getToolsSettings();


        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Log.d("HashMap", pair.getKey() + " = " + ((AppMenu)
                    pair
                            .getValue()).isVisible() );
        }

        Log.d("HashMap","End");
        initView();
    }

    private void setTextColorForMenuItem(MenuItem menuItem, @ColorRes int color) {
        SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, color)), 0, spanString.length(), 0);
        menuItem.setTitle(spanString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_junkfood_flagging, menu);
        MenuItem menuItem = menu.findItem(R.id.item_save);
//        setTextColorForMenuItem(menuItem, R.color.colorAccent);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                finish();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onPause() {
        super.onPause();

        for (int i = 0; i < sortedList.size(); i++) {
//            if (i >= 16) {
            if (i >= 12) {
                map.get(sortedList.get(i).getId()).setBottomDoc(true);
            } else {
                map.get(sortedList.get(i).getId()).setBottomDoc(false);
            }
        }

        String hashMapToolSettings = new Gson().toJson(map);
        PrefSiempo.getInstance(this).write(PrefSiempo.TOOLS_SETTING, hashMapToolSettings);
        new LoadToolPane(this).execute();
        FirebaseHelper.getInstance().logScreenUsageTime(this.getClass().getSimpleName(), startTime);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.editing_tools);
        setSupportActionBar(toolbar);
        items = new ArrayList<>();
        new MainListItemLoader(this).loadItemsDefaultApp(items);
        items = PackageUtil.getToolsMenuData(this, items);
        sortedList = new ArrayList<>(items);
        ListIterator listIterator = sortedList.listIterator();
        while (listIterator.hasNext()) {
            MainListItem next = (MainListItem) listIterator.next();
            next.setVisable(map.get(next.getId()).isVisible());
        }


        recyclerView = findViewById(R.id.recyclerView);
        txtSelectTools = findViewById(R.id.txtSelectTools);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(mLayoutManager);
        if (itemDecoration != null) {
            recyclerView.removeItemDecoration(itemDecoration);
        }
        itemDecoration = new ItemOffsetDecoration(this, R.dimen.dp_10);
        recyclerView.addItemDecoration(itemDecoration);
        mAdapter = new ToolPositioningAdapter(this, items, this, this, CoreApplication.getInstance().isHideIconBranding());
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter, this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        txtSelectTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToolPositioningActivity.this, ToolSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                intent.putExtra("TopList", topItems);
//                intent.putExtra("BottomList", bottomItems);
                startActivity(intent);
            }
        });
        linearTop = findViewById(R.id.linearTop);
        linearTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        relTop = findViewById(R.id.relTop);
        relTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    public void onToolItemListChanged(ArrayList<MainListItem> customers) {
        ArrayList<Long> listOfSortedCustomerId = new ArrayList<>();
        for (MainListItem customer : customers) {
            listOfSortedCustomerId.add((long) customer.getId());
        }
        sortedList = customers;
        Gson gson = new Gson();
        String jsonListOfSortedCustomerIds = gson.toJson(listOfSortedCustomerId);
        PrefSiempo.getInstance(this).write(PrefSiempo.SORTED_MENU, jsonListOfSortedCustomerIds);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

}
