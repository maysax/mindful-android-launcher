package co.siempo.phone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import co.siempo.phone.R;
import co.siempo.phone.activities.FavoritesSelectionActivity;
import co.siempo.phone.adapters.FavoritesPaneAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.customviews.ItemOffsetDecoration;
import co.siempo.phone.event.NotifyFavortieView;
import co.siempo.phone.models.MainListItem;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;


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
        view = inflater.inflate(R.layout.fragment_favorite_pane, container, false);
        Log.d("Test", "F1");
        initView();
        Log.d("Test", "F2");
        return view;

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MainThread)
    public void onEvent(NotifyFavortieView notifyFavortieView) {
        if (notifyFavortieView != null && notifyFavortieView.isNotify()) {
            items = CoreApplication.getInstance().getFavoriteItemsList();
            mAdapter.setMainListItemList(items, CoreApplication.getInstance().isHideIconBranding());
            checkSize();
            EventBus.getDefault().removeStickyEvent(notifyFavortieView);
        }
    }


    private void initView() {
        if (getActivity() != null && view != null) {
            items = new ArrayList<>();
//            items = PackageUtil.getFavoriteList(getActivity());
            recyclerView = view.findViewById(R.id.recyclerView);
            btnSelect = view.findViewById(R.id.btnSelect);
            btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FavoritesSelectionActivity
                            .class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }
            });
            linSelectFavouriteFood = view.findViewById(R.id.linSelectFavouriteFood);
            mLayoutManager = new GridLayoutManager(getActivity(), 4);
            recyclerView.setLayoutManager(mLayoutManager);
            if (itemDecoration != null) {
                recyclerView.removeItemDecoration(itemDecoration);
            }
            itemDecoration = new ItemOffsetDecoration(context, R.dimen.dp_10);
            recyclerView.addItemDecoration(itemDecoration);
            mAdapter = new FavoritesPaneAdapter(getActivity(), CoreApplication.getInstance().isHideIconBranding(), false, items);
            recyclerView.setAdapter(mAdapter);

            checkSize();

        }
    }

    private void checkSize() {
        if (items.size() > 0) {
            boolean containsFavourites = false;
            for (MainListItem item : items) {
                if (!TextUtils.isEmpty(item.getTitle())) {
                    containsFavourites = true;
                    break;
                }
            }
            if (containsFavourites) {
                linSelectFavouriteFood.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            } else {
                linSelectFavouriteFood.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }


        } else if (items.size() == 0) {
            linSelectFavouriteFood.setVisibility(View.VISIBLE);
        }
    }
}
