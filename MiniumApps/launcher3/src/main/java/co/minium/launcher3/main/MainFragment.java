package co.minium.launcher3.main;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.minium.launcher3.R;
import co.minium.launcher3.contact.PhoneNumbersAdapter;
import co.minium.launcher3.event.SearchLayoutEvent;
import co.minium.launcher3.token.TokenCompleteType;
import co.minium.launcher3.token.TokenItem;
import co.minium.launcher3.token.TokenItemType;
import co.minium.launcher3.token.TokenManager;
import co.minium.launcher3.token.TokenRouter;
import co.minium.launcher3.token.TokenUpdateEvent;
import co.minium.launcher3.token.TokenParser;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreFragment;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_main)
public class MainFragment extends CoreFragment {

    @ViewById
    ListView listView;

    @Pref
    DroidPrefs_ prefs;

    @Bean
    TokenManager manager;

    @Bean
    TokenRouter router;

    @Bean
    TokenParser parser;

    private MainListAdapter adapter;

    private MainFragmentMediator mediator;


    public MainFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mediator = new MainFragmentMediator(this);
        loadData();
    }

    @Background
    void loadData() {
        mediator.loadData();
        loadView();
    }

    @UiThread
    void loadView() {
        if (getActivity() != null) {
            adapter = new MainListAdapter(getActivity(), mediator.getItems());
            listView.setAdapter(adapter);
            adapter.getFilter().filter(manager.getCurrent().getTitle());
        }
    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {
        if (listView.getAdapter() instanceof PhoneNumbersAdapter) {
            mediator.listItemClicked2(router, position);
        } else {
            mediator.listItemClicked(router, position);
        }

    }

    public MainListAdapter getAdapter() {
        return adapter;
    }

    @Subscribe
    public void searchLayoutEvent(SearchLayoutEvent event) {
        parser.parse(event.getString());
        adapter.getFilter().filter(manager.getCurrent().getTitle());
    }

    @Subscribe
    public void tokenManagerEvent(TokenUpdateEvent event) {
        TokenItem current = manager.getCurrent();

        if (current.getCompleteType() == TokenCompleteType.FULL) {
            adapter.getFilter().filter("");
        } else if (current.getItemType() == TokenItemType.CONTACT) {
            if (current.getCompleteType() == TokenCompleteType.HALF) {
                mediator.contactNumberPicker(Integer.parseInt(current.getExtra1()));
            } else {
                mediator.contactPicker();
            }

        }
    }
}
