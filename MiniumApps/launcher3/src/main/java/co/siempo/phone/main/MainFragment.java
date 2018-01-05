package co.siempo.phone.main;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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

import co.siempo.phone.HelpActivity;
import co.siempo.phone.MainActivity;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.event.CreateNoteEvent;
import co.siempo.phone.event.SearchLayoutEvent;
import co.siempo.phone.event.SendSmsEvent;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.service.StatusBarService;
import co.siempo.phone.tempo.TempoActivity_;
import co.siempo.phone.tempo.TempoSettingsActivity_;
import co.siempo.phone.token.TokenCompleteType;
import co.siempo.phone.token.TokenItem;
import co.siempo.phone.token.TokenItemType;
import co.siempo.phone.token.TokenManager;
import co.siempo.phone.token.TokenParser;
import co.siempo.phone.token.TokenRouter;
import co.siempo.phone.token.TokenUpdateEvent;
import co.siempo.phone.ui.SearchLayout;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_main)
public class MainFragment extends CoreFragment {

    @ViewById
    public
    SearchLayout searchLayout;
    @ViewById
    ListView listView;
    @ViewById
    CardView listViewLayout;

    @ViewById
    CardView afterEffectLayout;

    @ViewById
    ImageView icon;

    @ViewById
    ImageView imgOverFlow;

    @ViewById
    ImageView imgTempo;

    @ViewById
    RelativeLayout relTop;

    @ViewById
    TextView text;

    @Pref
    DroidPrefs_ prefs;

    @Pref
    Launcher3Prefs_ launcherPrefs;


    @Bean
    TokenRouter router;

    @Bean
    TokenParser parser;

    private PopupWindow mPopupWindow;

    private MainListAdapter adapter;

    private MainFragmentMediator mediator;

    private boolean isKeyboardOpen;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            boolean isVisible = false;
            if (intent != null && intent.hasExtra("IsNotificationVisible")) {
                isVisible = intent.getBooleanExtra("IsNotificationVisible", false);
            }
            if (searchLayout != null && searchLayout.getTxtSearchBox() != null) {
                searchLayout.getTxtSearchBox().setNotificationVisible(isVisible);
            }
            if (isVisible && getActivity() != null) {
                UIUtils.hideSoftKeyboard(getActivity(), getActivity().getWindow().getDecorView().getWindowToken());
            }
        }
    };

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        CoreApplication.getInstance().isIfScreen = true;
        super.onStart();
    }

    @Override
    public void onStop() {
        CoreApplication.getInstance().isIfScreen = false;
        super.onStop();
    }

    @AfterViews
    void afterViews() {

        Intent myService = new Intent(getActivity(), StatusBarService.class);
        getActivity().startService(myService);
        if (listViewLayout != null) listViewLayout.setVisibility(View.GONE);
        if (afterEffectLayout != null) afterEffectLayout.setVisibility(View.GONE);
        KeyboardVisibilityEvent.setEventListener(getActivity(), new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                isKeyboardOpen = isOpen;
                updateListViewLayout();
            }
        });
        moveSearchBar(false);


    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("IsNotificationVisible"));
        if (adapter != null) adapter.getFilter().filter("");
        if (searchLayout != null) {
            searchLayout.askFocus();
        }
        // If new app installed or if any contact is update/create this booleans
        // becomes true from StatusService class.
        if (prefs.isContactUpdate().get() || prefs.isAppUpdated().get()) {
            loadData();
            if (prefs.isContactUpdate().get()) {
                prefs.isContactUpdate().put(false);
            }
            if (prefs.isAppUpdated().get()) {
                prefs.isAppUpdated().put(false);
            }
        }
        if (prefs.isTempoNotificationControlsDisabled().get()) {
            imgTempo.setVisibility(View.GONE);
        } else {
            imgTempo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private synchronized void updateListViewLayout() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (listViewLayout != null) {
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
                        }
                    } catch (Exception e) {
                        CoreApplication.getInstance().logException(e);
                        e.printStackTrace();
                    }
                }
            });
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
            adapter.getFilter().filter(TokenManager.getInstance().getCurrent().getTitle());
        }
    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {
        mediator.listItemClicked(router, position, searchLayout.getTxtSearchBox().getStrText());
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
            if (adapter != null)
                adapter.getFilter().filter(TokenManager.getInstance().getCurrent().getTitle());
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }
    }

    @Subscribe
    public void sendSmsEvent(SendSmsEvent event) {
        try {
            if (event.isSendSms()) {
                MainActivity.isTextLenghGreater = "";
                if (afterEffectLayout != null) afterEffectLayout.setVisibility(View.GONE);
                moveSearchBar(true);
                // manager.clear();
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }
    }

    private void emptyChecker(String string) {
        if (!string.isEmpty()) {
            if (afterEffectLayout != null) afterEffectLayout.setVisibility(View.GONE);
            moveSearchBar(true);
        } else {
            moveSearchBar(false);
        }
    }

    @Subscribe
    public void tokenManagerEvent(TokenUpdateEvent event) {
        try {
            TokenItem current = TokenManager.getInstance().getCurrent();
            if (current != null) {
                if (current.getItemType() == TokenItemType.END_OP) {
                    mediator.defaultData();
                } else if (current.getItemType() == TokenItemType.CONTACT) {
                    if (current.getCompleteType() == TokenCompleteType.HALF) {
                        mediator.contactNumberPicker(Integer.parseInt(current.getExtra1()));
                    } else {
                        mediator.contactPicker();
                    }
                } else if (current.getItemType() == TokenItemType.DATA) {
                    if (TokenManager.getInstance().get(0).getItemType() == TokenItemType.DATA) {
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
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }
    }

    private void moveSearchBar(final boolean isUp) {
        ObjectAnimator animY;
        if (searchLayout != null) {
            if (isUp) {
                animY = ObjectAnimator.ofFloat(searchLayout, "y", 40);
            } else {
                animY = ObjectAnimator.ofFloat(searchLayout, "y", UIUtils.getScreenHeight(getActivity()) / 3);
            }
            AnimatorSet animSet = new AnimatorSet();
            animSet.play(animY);
            animSet.start();
            animSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isUp) {
                        if(relTop!=null)
                            relTop.setVisibility(View.GONE);
                    } else {
                        if(relTop!=null)
                            relTop.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    @Click
    void text() {
        String id = (String) text.getTag();
        if (id.equals("1") && getActivity() != null) {
            new ActivityHelper(getActivity()).openNotesApp(true);
        }
        if (afterEffectLayout != null) afterEffectLayout.setVisibility(View.GONE);
        moveSearchBar(false);
    }

    @Click
    void imgTempo() {
        Intent intent = new Intent(getActivity(), TempoActivity_.class);
        startActivity(intent);
    }


    @Click
    void imgOverFlow() {
        if (getActivity() != null && imgOverFlow != null) {
            //popupMenu();
            final ViewGroup root = (ViewGroup) getActivity().getWindow().getDecorView().getRootView();
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Inflate the custom layout/view
            View customView;
            if (inflater != null) {
                customView = inflater.inflate(R.layout.home_popup, null);

                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

                // Set an elevation value for popup window
                // Call requires API level 21
                if (Build.VERSION.SDK_INT >= 21) {
                    mPopupWindow.setElevation(5.0f);
                }

                LinearLayout linHelp = customView.findViewById(R.id.linHelp);
                LinearLayout linSettings = customView.findViewById(R.id.linSettings);
                LinearLayout linTempo = customView.findViewById(R.id.linTempo);
                if (prefs.isTempoNotificationControlsDisabled().get()) {
                    linTempo.setVisibility(View.GONE);
                } else {
                    linTempo.setVisibility(View.VISIBLE);
                }

                linTempo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getActivity() != null) {
                            UIUtils.clearDim(root);
                            mPopupWindow.dismiss();
                            Intent intent = new Intent(getActivity(), TempoActivity_.class);
                            startActivity(intent);
                            // getActivity().overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                        }
                    }
                });
                linSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Code for opening Tempo Settings
                        Intent intent = new Intent(getActivity(), TempoSettingsActivity_.class);
                        startActivity(intent);
                        UIUtils.clearDim(root);
                        mPopupWindow.dismiss();
                    }
                });
                linHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UIUtils.clearDim(root);
                        mPopupWindow.dismiss();
                        Intent intent = new Intent(getActivity(), HelpActivity.class);
                        startActivity(intent);
                    }
                });
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setFocusable(true);
                mPopupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mPopupWindow.showAsDropDown(imgOverFlow, 0, (int) -imgOverFlow.getX() - 10);
                UIUtils.applyDim(root, 0.6f);
                UIUtils.hideSoftKeyboard(getActivity(), getActivity().getWindow().getDecorView().getWindowToken());
                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        UIUtils.clearDim(root);
                        searchLayout.askFocus();
                    }
                });
            }
        }
    }


    @Subscribe
    public void mainListAdapterEvent(MainListAdapterEvent event) {
        if (event.getDataSize() == 0) {
            if (listViewLayout != null) listViewLayout.setVisibility(View.GONE);
        } else {
            if (listViewLayout != null) listViewLayout.setVisibility(View.VISIBLE);
            updateListViewLayout();
        }
    }

    @Subscribe
    public void createNoteEvent(CreateNoteEvent event) {
        if (icon != null) icon.setImageResource(R.drawable.icon_save_note);
        if (text != null) {
            text.setText(R.string.view_save_note);
            text.setTag("1");
        }
        if (afterEffectLayout != null) afterEffectLayout.setVisibility(View.VISIBLE);

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
        return TokenManager.getInstance();
    }
}
