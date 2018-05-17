package co.siempo.phone.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;

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
    HashMap<Integer, AppMenu> map = new HashMap<>();
    LinearLayout linMain;
    private ArrayList<MainListItem> items = new ArrayList<>();
    private ArrayList<MainListItem> topItems = new ArrayList<>();
    private ArrayList<MainListItem> bottomItems = new ArrayList<>();
    private ArrayList<MainListItem> sortedList = new ArrayList<>();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_positioning);
        relMain = findViewById(R.id.relMain);
        linMain = findViewById(R.id.linMain);
        String filePath = PrefSiempo.getInstance(this).read(PrefSiempo
                .DEFAULT_BAG, "");
        if (!TextUtils.isEmpty(filePath)) {


            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
            //Code for Applying background
            relMain.setBackground(ob);
            linMain.setBackgroundColor(ContextCompat.getColor(this, R.color
                    .trans_black_bg));
        } else {
            linMain.setBackgroundColor(ContextCompat.getColor(this, R.color
                    .transparent));
        }
        StatusBarUtil.setTransparent(this);
        boolean read = PrefSiempo.getInstance(this).read(PrefSiempo.IS_DARK_THEME, false);
        if (read) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View
                    .SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);}
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        map = CoreApplication.getInstance().getToolsSettings();
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


//        ArrayList<MainListItem> top = new ArrayList<>();
//        ArrayList<MainListItem> bottom = new ArrayList<>();
//
//        for (int i = 0; i < sortedList.size(); i++) {
//            if (i >= 12) {
////                map.get(sortedList.get(i).getId()).setBottomDoc(true);
//                bottom.add(sortedList.get(i));
//            } else {
//                top.add(sortedList.get(i));
////                map.get(sortedList.get(i).getId()).setBottomDoc(false);
//            }
//        }
//
//        for (Map.Entry<Integer, AppMenu> entry : map.entrySet()) {
//            for (int i = 0; i < top.size(); i++) {
//                if (entry.getKey() == top.get(i).getId()) {
//                    map.get(top.get(i).getId()).setBottomDoc(false);
//                }
//            }
//
//            for (int i = 0; i < bottom.size(); i++) {
//                if (entry.getKey() == bottom.get(i).getId()) {
//                    map.get(bottom.get(i).getId()).setBottomDoc(true);
//                }
//            }
//        }

        for (int i = 0; i < sortedList.size(); i++) {
            if (i >= 16) {
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
//        items = CoreApplication.getInstance().getToolItemsList();
        items = PackageUtil.getToolsMenuData(this, items);
//        ArrayList<MainListItem> visableItems = new ArrayList<>();
//        ArrayList<MainListItem> inVisableItems = new ArrayList<>();
//        ArrayList<MainListItem> tempData = new ArrayList<>();
//
//        for (MainListItem mainListItem : CoreApplication.getInstance().getToolItemsList()) {
//            if (map.get(mainListItem.getId()).isVisible()) {
//                visableItems.add(mainListItem);
//            } else {
//                inVisableItems.add(mainListItem);
//            }
//        }
//        tempData.addAll(visableItems);
//        tempData.addAll(inVisableItems);

//        for (int i = 0; i < 12; i++) {
//            topItems.add(CoreApplication.getInstance().getToolItemsList().get(i));
//        }
//
//        topItems = new ArrayList<>();
//        topItems.addAll(CoreApplication.getInstance().getToolItemsList());
//        bottomItems = new ArrayList<>();
//        bottomItems.addAll(CoreApplication.getInstance().getToolBottomItemsList());
//
//        items.addAll(topItems);
//        items.addAll(bottomItems);
//        items = CoreApplication.getInstance().getToolItemsList();
//

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
                intent.putExtra("TopList", topItems);
                intent.putExtra("BottomList", bottomItems);
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
