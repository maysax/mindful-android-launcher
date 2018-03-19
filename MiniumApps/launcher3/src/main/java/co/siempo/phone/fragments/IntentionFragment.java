package co.siempo.phone.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.siempo.phone.R;
import co.siempo.phone.activities.HelpActivity;
import co.siempo.phone.activities.IntentionEditActivity;
import co.siempo.phone.activities.SettingsActivity_;
import co.siempo.phone.dialog.DialogTempoSetting;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.service.StatusBarService;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;


public class IntentionFragment extends CoreFragment implements View.OnClickListener {

    private View view;
    private ImageView imgTempo;
    private ImageView imgOverFlow, imgPullTab;
    private TextView txtIntention;
    private LinearLayout linIF;
    private PopupWindow mPopupWindow;
    private RelativeLayout relRootLayout;
    private Window mWindow;
    private int defaultStatusBarColor;

    public IntentionFragment() {
        // Required empty public constructor
    }

    public static IntentionFragment newInstance() {
        return new IntentionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_intention, container, false);
        mWindow = getActivity().getWindow();
        Intent myService = new Intent(getActivity(), StatusBarService.class);
        getActivity().startService(myService);
        initView(view);
        return view;
    }


    private void initView(View view) {
        relRootLayout = view.findViewById(R.id.relRootLayout);
        imgTempo = view.findViewById(R.id.imgTempo);
        imgTempo.setOnClickListener(this);
        imgPullTab = view.findViewById(R.id.imgPullTab);
        imgPullTab.setOnClickListener(this);
        imgOverFlow = view.findViewById(R.id.imgOverFlow);
        imgOverFlow.setOnClickListener(this);
        txtIntention = view.findViewById(R.id.txtIntention);
        txtIntention.setOnClickListener(this);
        linIF = view.findViewById(R.id.linIF);

        // clear FLAG_TRANSLUCENT_STATUS flag:
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        defaultStatusBarColor = mWindow.getStatusBarColor();

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PrefSiempo.getInstance(getActivity()).read(PrefSiempo.IS_INTENTION_ENABLE, false)) {
            linIF.setVisibility(View.GONE);
        } else {
            linIF.setVisibility(View.VISIBLE);
        }
        txtIntention.setText(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_INTENTION, ""));
        if (getActivity() != null) {
            if (PrefSiempo.getInstance(getActivity()).read(PrefSiempo.IS_APP_INSTALLED_FIRSTTIME_SHOW_TOOLTIP, true)) {
                if (!UIUtils.isMyLauncherDefault(getActivity())) {
                    android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (Settings.canDrawOverlays(getActivity())) {
                                    new ActivityHelper(getActivity()).handleDefaultLauncher(getActivity());
                                    /*
                                     * commented in order to remove overlay
                                     * permission
                                     */
                                    //((CoreActivity) getActivity()).loadDialog();
                                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.IS_APP_INSTALLED_FIRSTTIME_SHOW_TOOLTIP, false);
                                }
                            } else {
                                new ActivityHelper(getActivity()).handleDefaultLauncher(getActivity());
                                    /*
                                     * commented in order to remove overlay
                                     * permission
                                     */
                                //((CoreActivity) getActivity()).loadDialog();
                                PrefSiempo.getInstance(getActivity()).write(PrefSiempo.IS_APP_INSTALLED_FIRSTTIME_SHOW_TOOLTIP, false);
                            }

                        }
                    }, 500);
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgTempo:
                DialogTempoSetting dialogTempo = new DialogTempoSetting(getActivity());
                if (dialogTempo.getWindow() != null)
                    dialogTempo.getWindow().setGravity(Gravity.TOP);
                dialogTempo.show();
                break;
            case R.id.imgPullTab:
                ObjectAnimator animY = ObjectAnimator.ofFloat(relRootLayout, "translationX", 100f, 0f);
                animY.setDuration(700);//1sec
                animY.setInterpolator(new BounceInterpolator());
                animY.setRepeatCount(0);
                animY.start();

                break;
            case R.id.imgOverFlow:
                showOverflowDialog();
                break;
            case R.id.txtIntention:
                Intent intent = new Intent(getActivity(), IntentionEditActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            default:
                break;
        }
    }

    private void showOverflowDialog() {
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

                linTempo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getActivity() != null) {
                            UIUtils.clearDim(root);
                            mPopupWindow.dismiss();
                            DialogTempoSetting dialogTempo = new DialogTempoSetting(getActivity());
                            if (dialogTempo.getWindow() != null)
                                dialogTempo.getWindow().setGravity(Gravity.TOP);
                            dialogTempo.show();
                        }
                    }
                });
                linSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Code for opening Tempo Settings
                        Intent intent = new Intent(getActivity(), SettingsActivity_.class);
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

                    }
                });
            }
        }
    }


}
