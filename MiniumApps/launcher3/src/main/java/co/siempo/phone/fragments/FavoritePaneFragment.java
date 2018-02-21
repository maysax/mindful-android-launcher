package co.siempo.phone.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import co.siempo.phone.R;
import co.siempo.phone.adapters.FavoritesPaneAdapter;
import co.siempo.phone.customviews.ItemOffsetDecoration;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;


public class FavoritePaneFragment extends CoreFragment {

    private View view;
    private RecyclerView recyclerView;
    private ArrayList<MainListItem> items = new ArrayList<>();
    private FavoritesPaneAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Parcelable mListState;
    private ItemOffsetDecoration itemDecoration;
    private LinearLayout linSelectFavouriteFood;
    private Button btnSelect;

    public FavoritePaneFragment() {
        // Required empty public constructor
    }

    public static FavoritePaneFragment newInstance() {
        return new FavoritePaneFragment();
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
        items = PackageUtil.getFavoriteList(getActivity());
        recyclerView = view.findViewById(R.id.recyclerView);
        btnSelect = view.findViewById(R.id.btnSelect);
        linSelectFavouriteFood = view.findViewById(R.id.linSelectFavouriteFood);
        mLayoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(mLayoutManager);
        if (itemDecoration != null) {
            recyclerView.removeItemDecoration(itemDecoration);
        }
        itemDecoration = new ItemOffsetDecoration(context, R.dimen.dp_10);
        recyclerView.addItemDecoration(itemDecoration);
        boolean isHideIconBranding = PrefSiempo.getInstance(context).read(PrefSiempo.IS_ICON_BRANDING, true);

        mAdapter = new FavoritesPaneAdapter(getActivity(), isHideIconBranding, false, items);
        recyclerView.setAdapter(mAdapter);


        if (items.size() > 0) {
            linSelectFavouriteFood.setVisibility(View.GONE);
        } else if (items.size() == 0) {
            linSelectFavouriteFood.setVisibility(View.VISIBLE);
        }

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initView();
        }
    }


}
