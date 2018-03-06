package co.siempo.phone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import co.siempo.phone.R;
import co.siempo.phone.adapters.FavoritePositioningAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.customviews.ItemOffsetDecoration;
import co.siempo.phone.event.HomePressEvent;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.interfaces.OnFavoriteItemListChangedListener;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.main.OnStartDragListener;
import co.siempo.phone.main.SimpleItemTouchHelperCallback;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.Subscribe;

public class FavoriteAppsPositionActivity extends CoreActivity implements OnFavoriteItemListChangedListener,
        OnStartDragListener {
    private ArrayList<MainListItem> items = new ArrayList<>();
    private ArrayList<MainListItem> sortedList = new ArrayList<>();
    private FavoritePositioningAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemOffsetDecoration itemDecoration;
    private ItemTouchHelper mItemTouchHelper;
    private Parcelable mListState;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TextView txtSelectTools;
    private RelativeLayout relTop;
    private RelativeLayout relPane;
    private long startTime = 0;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_apps_positioning);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getInstance().logScreenUsageTime(FavoriteAppsPositionActivity.this.getClass().getSimpleName(), startTime);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        relTop = findViewById(R.id.relTop);
        relPane = findViewById(R.id.relPane);
        toolbar.setTitle(R.string.editing_frequently_apps);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color
                .colorAccent));
        items = new ArrayList<>();
        items = PackageUtil.getFavoriteList(this);

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
        boolean isHideIconBranding = PrefSiempo.getInstance(FavoriteAppsPositionActivity.this).read(PrefSiempo.IS_ICON_BRANDING, true);


        mAdapter = new FavoritePositioningAdapter(this, isHideIconBranding, items, this, this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter, this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
        txtSelectTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoriteAppsPositionActivity.this, FavoritesSelectionActivity.class);
                startActivity(intent);
                FavoriteAppsPositionActivity.this
                        .overridePendingTransition(R.anim
                                        .fade_in,
                                R.anim.fade_out);
            }
        });


        relTop.setOnClickListener(onClickListener);

        toolbar.setOnClickListener(onClickListener);

        relPane.setOnClickListener(onClickListener);

    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onFavoriteItemListChanged(ArrayList<MainListItem> customers) {
        ArrayList<String> listOfSortedCustomerId = new ArrayList<>();

        for (MainListItem customer : customers) {
            listOfSortedCustomerId.add(customer.getPackageName());
        }
        sortedList = customers;
        Gson gson = new Gson();
        String jsonListOfSortedCustomerIds = gson.toJson(listOfSortedCustomerId);
        PrefSiempo.getInstance(this).write(PrefSiempo.FAVORITE_SORTED_MENU, jsonListOfSortedCustomerIds);
    }

    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        try {
            if (event.isVisible() && UIUtils.isMyLauncherDefault(this)) {
                finish();
            }

        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }
    }

}
