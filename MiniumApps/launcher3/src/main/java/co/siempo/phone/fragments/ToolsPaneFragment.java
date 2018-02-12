package co.siempo.phone.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.adapters.ToolsMenuAdapter;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PrefSiempo;


public class ToolsPaneFragment extends CoreFragment {

    private View view;
    private RecyclerView recyclerView;
    private List<MainListItem> items = new ArrayList<>();
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

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        items = new ArrayList<>();
        new MainListItemLoader(getActivity()).loadItemsDefaultApp(items);
        recyclerView = view.findViewById(R.id.recyclerView);
        mLayoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(mLayoutManager);
        if (itemDecoration != null) {
            recyclerView.removeItemDecoration(itemDecoration);
        }
        itemDecoration = new ItemOffsetDecoration(context, R.dimen.dp_15);
        recyclerView.addItemDecoration(itemDecoration);
        boolean isHideIconBranding = PrefSiempo.getInstance(context).read(PrefSiempo.IS_ICON_BRANDING, true);
        mAdapter = new ToolsMenuAdapter(getActivity(), isHideIconBranding, items);
        recyclerView.setAdapter(mAdapter);
    }

    private class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private final int mItemOffset;

        ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, mItemOffset, 0, mItemOffset);
        }
    }
}
