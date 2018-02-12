package co.siempo.phone.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.adapters.PanePagerAdapter;
import co.siempo.phone.adapters.ToolsMenuAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.customviews.ItemOffsetDecoration;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PrefSiempo;
import me.relex.circleindicator.CircleIndicator;


public class PaneFragment extends CoreFragment implements View.OnClickListener {

    private View view;
    private LinearLayout linTopDoc;
    private CircleIndicator indicator;
    private ViewPager pagerPane;
    private PanePagerAdapter mPagerAdapter;
    private LinearLayout linPane;
    private LinearLayout linBottomDoc;
    private RecyclerView recyclerViewBottomDoc;
    private List<MainListItem> items = new ArrayList<>();
    private ToolsMenuAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemOffsetDecoration itemDecoration;

    public PaneFragment() {
        // Required empty public constructor
    }

    public static PaneFragment newInstance() {
        return new PaneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pane, container, false);
        context = (CoreActivity) getActivity();
        initView(view);
        return view;
    }

    private void initView(View view) {
        linTopDoc = view.findViewById(R.id.linTopDoc);
        linTopDoc.setOnClickListener(this);
        linPane = view.findViewById(R.id.linPane);
        linPane.setOnClickListener(this);
        linBottomDoc = view.findViewById(R.id.linBottomDoc);
        linBottomDoc.setOnClickListener(this);
        indicator = view.findViewById(R.id.indicator);
        pagerPane = view.findViewById(R.id.pagerPane);

        mPagerAdapter = new PanePagerAdapter(getChildFragmentManager());
        pagerPane.setAdapter(mPagerAdapter);
        indicator.setViewPager(pagerPane);
        pagerPane.setCurrentItem(2);

        bindBottomDoc();

    }

    private void bindBottomDoc() {
        ArrayList<MainListItem> itemsLocal = new ArrayList<>();
        new MainListItemLoader(getActivity()).loadItemsDefaultApp(itemsLocal);
        Set<Integer> list = new HashSet<>();

        for (Map.Entry<Integer, AppMenu> entry : CoreApplication.getInstance().getToolsSettings().entrySet()) {
            if (entry.getValue().isBottomDoc()) {
                list.add(entry.getKey());
            }
        }

        for (MainListItem mainListItem : itemsLocal) {
            if (list.contains(mainListItem.getId())) {
                items.add(mainListItem);
            }
        }

        recyclerViewBottomDoc = view.findViewById(R.id.recyclerViewBottomDoc);
        mLayoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerViewBottomDoc.setLayoutManager(mLayoutManager);
        if (itemDecoration != null) {
            recyclerViewBottomDoc.removeItemDecoration(itemDecoration);
        }
        itemDecoration = new ItemOffsetDecoration(context, R.dimen.dp_1);
        recyclerViewBottomDoc.addItemDecoration(itemDecoration);
        boolean isHideIconBranding = PrefSiempo.getInstance(context).read(PrefSiempo.IS_ICON_BRANDING, true);
        mAdapter = new ToolsMenuAdapter(getActivity(), isHideIconBranding, true, items);
        recyclerViewBottomDoc.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.linTopDoc:
                break;
            case R.id.pagerPane:
                break;
            case R.id.linPane:
                break;
            case R.id.linBottomDoc:
                break;
        }
    }
}
