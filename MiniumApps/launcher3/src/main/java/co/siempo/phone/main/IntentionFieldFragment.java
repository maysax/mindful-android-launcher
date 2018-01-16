package co.siempo.phone.main;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.HelpActivity;
import co.siempo.phone.HelpActivity_;
import co.siempo.phone.IntentionEditActivity_;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.service.StatusBarService;
import co.siempo.phone.tempo.TempoActivity_;
import co.siempo.phone.tempo.TempoSettingsActivity_;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_intention_field)
public class IntentionFieldFragment extends CoreFragment {

    @ViewById
    ImageView icon;

    @ViewById
    ImageView imgOverFlow;

    @ViewById
    ImageView imgTempo;

    @ViewById
    RelativeLayout relTop;


    @ViewById
    LinearLayout linIF;

    @ViewById
    TextView txtIntention;

    @Pref
    DroidPrefs_ prefs;

    @Pref
    Launcher3Prefs_ launcherPrefs;


    private PopupWindow mPopupWindow;


    public IntentionFieldFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        Intent myService = new Intent(getActivity(), StatusBarService.class);
        getActivity().startService(myService);
        moveSearchBar(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (prefs.isContactUpdate().get() || prefs.isAppUpdated().get()) {
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
        if (prefs.isIntentionEnable().get()) {
            linIF.setVisibility(View.GONE);
        } else {
            linIF.setVisibility(View.VISIBLE);
        }
        txtIntention.setText(prefs.defaultIntention().get());
        try {
            UIUtils.hideSoftKeyboard(getActivity(), getActivity().getWindow().getDecorView().getWindowToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click
    void imgTempo() {
        Intent intent = new Intent(getActivity(), TempoActivity_.class);
        startActivity(intent);
    }

    @Click
    void txtIntention() {
        Intent intent = new Intent(getActivity(), IntentionEditActivity_.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
                        Intent intent = new Intent(getActivity(), HelpActivity_.class);
                        startActivity(intent);
                    }
                });
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setFocusable(true);
                mPopupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mPopupWindow.showAsDropDown(imgOverFlow, 0, (int) -imgOverFlow.getX() - 10);
//                mPopupWindow.showAtLocation(relTop, Gravity.TOP| Gravity.RIGHT, 0, 10);
                UIUtils.applyDim(root, 0.6f);
                UIUtils.hideSoftKeyboard(getActivity(), getActivity().getWindow().getDecorView().getWindowToken());
                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        UIUtils.clearDim(root);

                    }
                });
            }
        }
    }

    private void moveSearchBar(final boolean isUp) {
        ObjectAnimator animY;
        if (linIF != null) {
            if (isUp) {
                animY = ObjectAnimator.ofFloat(linIF, "y", 40);
            } else {
                animY = ObjectAnimator.ofFloat(linIF, "y", UIUtils.getScreenHeight(getActivity()) / 3);
            }
            animY.setDuration(10);
            AnimatorSet animSet = new AnimatorSet();
            animSet.play(animY);
            animSet.start();
        }
    }
}
