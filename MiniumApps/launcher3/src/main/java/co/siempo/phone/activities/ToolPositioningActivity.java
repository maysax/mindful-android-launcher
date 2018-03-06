package co.siempo.phone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import co.siempo.phone.R;
import co.siempo.phone.adapters.ToolPositioningAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.customviews.ItemOffsetDecoration;
import co.siempo.phone.event.HomePressEvent;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.interfaces.OnToolItemListChangedListener;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.main.OnStartDragListener;
import co.siempo.phone.main.SimpleItemTouchHelperCallback;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.Subscribe;

public class ToolPositioningActivity extends CoreActivity implements OnToolItemListChangedListener,
        OnStartDragListener {
    HashMap<Integer, AppMenu> map = new HashMap<>();
    private ArrayList<MainListItem> items = new ArrayList<>();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_positioning);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        map = CoreApplication.getInstance().getToolsSettings();
        initView();
    }


    @Override
    protected void onPause() {
        super.onPause();
        for (int i = 0; i < sortedList.size(); i++) {
            if (i >= 12) {
                map.get(sortedList.get(i).getId()).setBottomDoc(true);
            } else {
                map.get(sortedList.get(i).getId()).setBottomDoc(false);
            }
        }
        String hashMapToolSettings = new Gson().toJson(map);
        PrefSiempo.getInstance(this).write(PrefSiempo.TOOLS_SETTING, hashMapToolSettings);
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
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color
                .colorAccent));
        items = new ArrayList<>();
        new MainListItemLoader(this).loadItemsDefaultApp(items);
        items = PackageUtil.getToolsMenuData(this, items);

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
        boolean is_icon_branding = PrefSiempo.getInstance(this).read(PrefSiempo.IS_ICON_BRANDING, true);
        mAdapter = new ToolPositioningAdapter(this, items, this, this, is_icon_branding);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter, this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        txtSelectTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToolPositioningActivity.this, ToolSelectionActivity.class);
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
        Log.d("kamothi", "onToolItemListChange");
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

    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        try {
            if (event.isVisible() && UIUtils.isMyLauncherDefault(this)) {
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
