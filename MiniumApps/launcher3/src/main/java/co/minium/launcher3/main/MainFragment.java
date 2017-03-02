package co.minium.launcher3.main;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.minium.launcher3.R;
import co.minium.launcher3.contact.PhoneNumbersAdapter;
import co.minium.launcher3.event.CreateNoteEvent;
import co.minium.launcher3.event.SearchLayoutEvent;
import co.minium.launcher3.helper.ActivityHelper;
import co.minium.launcher3.token.TokenCompleteType;
import co.minium.launcher3.token.TokenItem;
import co.minium.launcher3.token.TokenItemType;
import co.minium.launcher3.token.TokenManager;
import co.minium.launcher3.token.TokenParser;
import co.minium.launcher3.token.TokenRouter;
import co.minium.launcher3.token.TokenUpdateEvent;
import co.minium.launcher3.ui.SearchLayout;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_main)
public class MainFragment extends CoreFragment {

    @ViewById
    ListView listView;

    @ViewById
    SearchLayout searchLayout;

    @ViewById
    CardView listViewLayout;

    @ViewById
    CardView afterEffectLayout;

    @ViewById
    ImageView icon;

    @ViewById
    TextView text;

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

    private boolean isKeyboardOpen;


    public MainFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {

        listViewLayout.setVisibility(View.GONE);
        afterEffectLayout.setVisibility(View.GONE);
        KeyboardVisibilityEvent.setEventListener(getActivity(), new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                isKeyboardOpen = isOpen;
                updateListViewLayout();
            }
        });
        moveSearchBar(false, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        searchLayout.askFocus();
        if (adapter != null) adapter.getFilter().filter("");

    }

    void updateListViewLayout() {
        try {
            int val;
            if (isKeyboardOpen) {
                val = Math.min(adapter.getCount() * 54, 240);
            } else {
                val = Math.min(adapter.getCount() * 54, 54 * 9);
            }

            // extra padding when there is something in listView
            if (val != 0) val += 8;

            listViewLayout.getLayoutParams().height = UIUtils.dpToPx(getActivity(), val);
            listViewLayout.requestLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        emptyChecker(event.getString());
        parser.parse(event.getString());
        adapter.getFilter().filter(manager.getCurrent().getTitle());
    }

    private void emptyChecker(String string) {
        if (!string.isEmpty()) {
            afterEffectLayout.setVisibility(View.GONE);
            moveSearchBar(true, null);
        } else {
            moveSearchBar(false, null);
        }
    }

    @Subscribe
    public void tokenManagerEvent(TokenUpdateEvent event) {
        TokenItem current = manager.getCurrent();

        if (current.getItemType() == TokenItemType.END_OP) {
            adapter.getFilter().filter("^");
        } else if (current.getCompleteType() == TokenCompleteType.FULL) {
            adapter.getFilter().filter("");
        } else if (current.getItemType() == TokenItemType.CONTACT) {
            if (current.getCompleteType() == TokenCompleteType.HALF) {
                mediator.contactNumberPicker(Integer.parseInt(current.getExtra1()));
            } else {
                mediator.contactPicker();
            }
        } else if (current.getItemType() == TokenItemType.DATA) {
            if (current.getTitle().isEmpty()) mediator.resetData();
            adapter.getFilter().filter(current.getTitle());
        }
    }

    void moveSearchBar(boolean isUp, final AnimatorListenerAdapter adapter) {
        ObjectAnimator animY;

        if (isUp) {
            animY = ObjectAnimator.ofFloat(searchLayout, "y", 0);
        } else {
            animY = ObjectAnimator.ofFloat(searchLayout, "y", 540);
        }

        if (adapter != null) {
            animY.addListener(adapter);
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(animY);
        animSet.start();
    }

    @Click
    void text() {
        String id = (String) text.getTag();
        if (id.equals("1")) {
            new ActivityHelper(getActivity()).openNotesApp();
        }
        afterEffectLayout.setVisibility(View.GONE);
        moveSearchBar(false, null);
    }

    @Subscribe
    public void mainListAdapterEvent(MainListAdapterEvent event) {
        if (event.getDataSize() == 0)
            listViewLayout.setVisibility(View.GONE);
        else {
            listViewLayout.setVisibility(View.VISIBLE);
            updateListViewLayout();
        }
    }

    @Subscribe
    public void createNoteEvent(CreateNoteEvent event) {
        icon.setImageResource(R.drawable.icon_save_note);
        text.setText("View saved note");
        text.setTag("1");
        afterEffectLayout.setVisibility(View.VISIBLE);

    }
}
