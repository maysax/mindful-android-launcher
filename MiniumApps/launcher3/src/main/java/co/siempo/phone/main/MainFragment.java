package co.siempo.phone.main;


import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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

import co.siempo.phone.MainActivity;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.contact.PhoneNumbersAdapter;
import co.siempo.phone.event.CreateNoteEvent;
import co.siempo.phone.event.SearchLayoutEvent;
import co.siempo.phone.event.SendSmsEvent;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.token.TokenCompleteType;
import co.siempo.phone.token.TokenItem;
import co.siempo.phone.token.TokenItemType;
import co.siempo.phone.token.TokenManager;
import co.siempo.phone.token.TokenParser;
import co.siempo.phone.token.TokenRouter;
import co.siempo.phone.token.TokenUpdateEvent;
import co.siempo.phone.ui.SearchLayout;
import de.greenrobot.event.Subscribe;
import minium.co.core.log.Tracer;
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
    public
    SearchLayout searchLayout;

    @ViewById
    CardView listViewLayout;

    @ViewById
    CardView afterEffectLayout;

    @ViewById
    ImageView icon;

    @ViewById
    TextView text;


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


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            boolean isVisible = false;
            if(intent.hasExtra("IsNotificationVisible")){
                isVisible = intent.getBooleanExtra("IsNotificationVisible", false);
            }
            if(searchLayout!=null && searchLayout.getTxtSearchBox()!=null){
                searchLayout.getTxtSearchBox().setNotificationVisible(isVisible);
            }
            if (isVisible) {
                UIUtils.hideSoftKeyboard(getActivity(), getActivity().getWindow().getDecorView().getWindowToken());
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("IsNotificationVisible"));
        if (adapter != null) adapter.getFilter().filter("");
    }

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
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
        try {
            if (event.getString().equalsIgnoreCase("") || event.getString().equalsIgnoreCase("/")
                    || (event.getString().startsWith("/") && event.getString().length() == 2)) {
                listView.smoothScrollToPosition(0);
            }
            emptyChecker(event.getString());
            parser.parse(event.getString());
           if(adapter!=null) adapter.getFilter().filter(manager.getCurrent().getTitle());
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    @Subscribe
    public void sendSmsEvent(SendSmsEvent event) {
        try {
            if (event.isSendSms()) {
                MainActivity.isTextLenghGreater = "";
                afterEffectLayout.setVisibility(View.GONE);
                moveSearchBar(true, null);
                // manager.clear();
            }
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
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
        try {
            TokenItem current = manager.getCurrent();

            if (current.getItemType() == TokenItemType.END_OP) {
                mediator.defaultData();
            } else if (current.getItemType() == TokenItemType.CONTACT) {
                if (current.getCompleteType() == TokenCompleteType.HALF) {
                    mediator.contactNumberPicker(Integer.parseInt(current.getExtra1()));
                } else {
                    mediator.contactPicker();
                }
            } else if (current.getItemType() == TokenItemType.DATA) {
                if (manager.get(0).getItemType() == TokenItemType.DATA) {
                    mediator.resetData();
                    if (adapter != null) adapter.getFilter().filter(current.getTitle());
                } else {
                    mediator.resetData();
                    if (current.getTitle().trim().isEmpty()) {
                        if (adapter != null) adapter.getFilter().filter("^");
                    } else {
                        if (adapter != null) adapter.getFilter().filter(current.getTitle());
                    }

                }
            }
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    void moveSearchBar(boolean isUp, final AnimatorListenerAdapter adapter) {
        ObjectAnimator animY;

        if (isUp) {
            animY = ObjectAnimator.ofFloat(searchLayout, "y", 40);
        } else {
            animY = ObjectAnimator.ofFloat(searchLayout, "y", UIUtils.getScreenHeight(getActivity()) / 3);
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
            new ActivityHelper(getActivity()).openNotesApp(true);
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
        text.setText(R.string.view_save_note);
        text.setTag("1");
        afterEffectLayout.setVisibility(View.VISIBLE);

    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            if (searchLayout != null)
                searchLayout.askFocus();
        }
    }

    public TokenManager getManager() {
        return manager;
    }
}
