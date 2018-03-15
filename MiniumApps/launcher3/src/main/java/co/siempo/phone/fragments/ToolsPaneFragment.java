package co.siempo.phone.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.siempo.phone.R;
import co.siempo.phone.adapters.ToolsMenuAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.customviews.ItemOffsetDecoration;
import co.siempo.phone.event.NotifyToolView;
import co.siempo.phone.models.MainListItem;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;


public class ToolsPaneFragment extends CoreFragment {

    private View view;
    private RecyclerView recyclerView;
    private ArrayList<MainListItem> items = new ArrayList<>();
    private ToolsMenuAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Parcelable mListState;
    private ItemOffsetDecoration itemDecoration;

    public ToolsPaneFragment() {
        // Required empty public constructor
    }

    public static ToolsPaneFragment newInstance() {
        return new ToolsPaneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tools_pane, container, false);
        Log.d("Test", "T1");
        recyclerView = view.findViewById(R.id.recyclerView);
        initView();
        Log.d("Test", "T2");
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MainThread)
    public void onEvent(NotifyToolView notifyToolView) {
        if (notifyToolView != null && notifyToolView.isNotify()) {
            items = CoreApplication.getInstance().getToolItemsList();
//            mAdapter.setMainListItemList(items, false, CoreApplication.getInstance().isHideIconBranding());
            mAdapter = new ToolsMenuAdapter(getActivity(), CoreApplication.getInstance().isHideIconBranding(), false, items);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            EventBus.getDefault().removeStickyEvent(notifyToolView);
        }

    }

    private void initView() {
        if (getActivity() != null && recyclerView != null) {
            items = CoreApplication.getInstance().getToolItemsList();
//            new MainListItemLoader(getActivity()).loadItemsDefaultApp(items);
//            items = PackageUtil.getToolsMenuData(getActivity(), items);
            mLayoutManager = new GridLayoutManager(getActivity(), 4);
            recyclerView.setLayoutManager(mLayoutManager);
            if (itemDecoration != null) {
                recyclerView.removeItemDecoration(itemDecoration);
            }
            itemDecoration = new ItemOffsetDecoration(context, R.dimen.dp_10);
            recyclerView.addItemDecoration(itemDecoration);
            mAdapter = new ToolsMenuAdapter(getActivity(), CoreApplication.getInstance().isHideIconBranding(), false, items);
            recyclerView.setAdapter(mAdapter);
        }
    }
}
