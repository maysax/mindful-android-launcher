package co.siempo.phone.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;

import co.siempo.phone.R;
import co.siempo.phone.activities.FavoritesSelectionActivity;
import co.siempo.phone.adapters.FavoritesPaneAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.customviews.ItemOffsetDecoration;
import co.siempo.phone.event.NotifyFavortieView;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.util.AppUtils;

public class FavoritePaneFragment extends CoreFragment {
    private View view;
    private RecyclerView recyclerView;
    private ArrayList<MainListItem> items = new ArrayList<>();
    private FavoritesPaneAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite_pane, container, false);
        initView();
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && getActivity() != null) {

            AppUtils.notificationBarManaged(getActivity(), null);
        }
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
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
            items = CoreApplication.getInstance().getFavoriteItemsList();
            recyclerView = view.findViewById(R.id.recyclerView);
            btnSelect = view.findViewById(R.id.btnSelect);
            btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(requireContext(), FavoritesSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
            linSelectFavouriteFood = view.findViewById(R.id.linSelectFavouriteFood);
            mLayoutManager = new GridLayoutManager(getActivity(), 4);
            recyclerView.setLayoutManager(mLayoutManager);
            if (itemDecoration != null) {
                recyclerView.removeItemDecoration(itemDecoration);
            }
            itemDecoration = new ItemOffsetDecoration(requireContext(), R.dimen.dp_10);
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


        } else {
            linSelectFavouriteFood.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
