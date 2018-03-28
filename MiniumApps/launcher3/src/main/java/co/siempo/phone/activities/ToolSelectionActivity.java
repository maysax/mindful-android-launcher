package co.siempo.phone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import co.siempo.phone.R;
import co.siempo.phone.adapters.ToolsListAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.NotifyBottomView;
import co.siempo.phone.event.NotifySearchRefresh;
import co.siempo.phone.event.NotifyToolView;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.service.LoadToolPane;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.Sorting;
import de.greenrobot.event.EventBus;

public class ToolSelectionActivity extends CoreActivity {

    public static final int TOOL_SELECTION = 100;
    private HashMap<Integer, AppMenu> map;
    private Toolbar toolbar;
    private ArrayList<MainListItem> items = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private ToolsListAdapter mAdapter;
    private long startTime = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_junkfood_flagging, menu);
        MenuItem menuItem = menu.findItem(R.id.item_save);
        setTextColorForMenuItem(menuItem, R.color.colorAccent);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mAdapter != null) {
                    PrefSiempo.getInstance(ToolSelectionActivity.this).write(PrefSiempo.TOOLS_SETTING, new Gson().toJson(mAdapter.getMap()));
                    EventBus.getDefault().postSticky(new NotifyBottomView(true));
                    EventBus.getDefault().postSticky(new NotifyToolView(true));
                    finish();
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * change text color of Menuitem
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_selection);
        map = CoreApplication.getInstance().getToolsSettings();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            PrefSiempo.getInstance(ToolSelectionActivity.this).write(PrefSiempo.TOOLS_SETTING, new Gson().toJson(mAdapter.getMap()));
            EventBus.getDefault().postSticky(new NotifyBottomView(true));
            EventBus.getDefault().postSticky(new NotifyToolView(true));
        }
        new LoadToolPane(this).execute();
        EventBus.getDefault().postSticky(new NotifySearchRefresh(true));
        FirebaseHelper.getInstance().logScreenUsageTime(this.getClass().getSimpleName(), startTime);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.select_tools);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color
                .colorAccent));
        recyclerView = findViewById(R.id.recyclerView);
        filterListData();
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ToolsListAdapter(this, items);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void filterListData() {
        items = new ArrayList<>();
        new MainListItemLoader(this).loadItemsDefaultApp(items);
        items.remove(11);
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setVisable(map.get(items.get(i).getId()).isVisible());
        }
        items = Sorting.sortToolAppAssignment(this, items);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TOOL_SELECTION) {
            if (resultCode == RESULT_OK) {
                mAdapter.refreshEvents(items);
            }
        }
    }

}
