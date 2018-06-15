package co.siempo.phone.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
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
import co.siempo.phone.activities.DashboardActivity;
import co.siempo.phone.activities.EnableTempoActivity;
import co.siempo.phone.activities.HelpActivity;
import co.siempo.phone.activities.IntentionEditActivity;
import co.siempo.phone.activities.SettingsActivity_;
import co.siempo.phone.dialog.DialogTempoSetting;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.service.StatusBarService;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PermissionUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;


public class IntentionFragment extends CoreFragment implements View.OnClickListener {

    Context context;
    TextView txtIntention, txtHint;
    private View view;
    private ImageView imgTempo;
    private ImageView imgOverFlow, imgPullTab;
    private CardView cardView;
    private PopupWindow mPopupWindow;
    private RelativeLayout relRootLayout;
    private Window mWindow;
    private int defaultStatusBarColor;
    private PermissionUtil permissionUtil;
    private DialogTempoSetting dialogTempo;

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
        context = getActivity();
        permissionUtil = new PermissionUtil(context);
        Intent myService = new Intent(getActivity(), StatusBarService.class);
        getActivity().startService(myService);
        initView(view);


        return view;
    }


    public void hideView() {
        if (PrefSiempo.getInstance(getActivity()).read(PrefSiempo.TOGGLE_LEFTMENU, 0) >= 3) {
            if (imgPullTab != null) imgPullTab.setVisibility(View.GONE);
        } else {
            if (imgPullTab != null) imgPullTab.setVisibility(View.VISIBLE);
        }
        if (mWindow != null) {
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            mWindow.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.transparent));

        }
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
        txtHint = view.findViewById(R.id.txtHint);
        cardView = view.findViewById(R.id.cardView);
        cardView.setOnClickListener(this);

        hideView();
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            hideView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DashboardActivity.currentIndexDashboard == 1) {
            hideView();
        }
        if (dialogTempo != null && dialogTempo.isShowing()) {
            if (!permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS)
                    || !permissionUtil.hasGiven(PermissionUtil
                    .CALL_PHONE_PERMISSION)
                    || !PackageUtil.isSiempoLauncher(context)) {
                dialogTempo.dismiss();
            }
        }
        if (PrefSiempo.getInstance(getActivity()).read(PrefSiempo.IS_INTENTION_ENABLE, false)) {
            cardView.setVisibility(View.GONE);
        } else {
            cardView.setVisibility(View.VISIBLE);
        }
        if (PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_INTENTION, "").equalsIgnoreCase("")) {
            txtHint.setVisibility(View.INVISIBLE);
            txtIntention.setText(getString(R.string.what_s_your_intention));
            txtIntention.setTextColor(ContextCompat.getColor(getActivity(), R.color.hint_white));
        } else {
            txtHint.setVisibility(View.VISIBLE);
            txtHint.setText(getString(R.string.your_intention));
            txtIntention.setText(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_INTENTION, ""));
            txtIntention.setTextColor(ContextCompat.getColor(getActivity(), R.color.settings_title_black));
        }
        if (getActivity() != null) {
            if (PrefSiempo.getInstance(getActivity()).read(PrefSiempo.IS_APP_INSTALLED_FIRSTTIME_SHOW_TOOLTIP, true)) {
                if (!UIUtils.isMyLauncherDefault(getActivity())) {
                    android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                new ActivityHelper(getActivity()).handleDefaultLauncher(getActivity());
                                PrefSiempo.getInstance(getActivity()).write(PrefSiempo.IS_APP_INSTALLED_FIRSTTIME_SHOW_TOOLTIP, false);
                            } else {
                                new ActivityHelper(getActivity()).handleDefaultLauncher(getActivity());
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
                if (null != getActivity()) {
                    if (permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS)
                            && permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION)
                            && PackageUtil.isSiempoLauncher(context) &&
                            UIUtils.hasUsageStatsPermission(context)) {

                        TypedValue typedValue = new TypedValue();
                        Resources.Theme theme = context.getTheme();
                        theme.resolveAttribute(R.attr.dialog_style, typedValue, true);
                        int dialogStyle = typedValue.resourceId;
                        dialogTempo = new DialogTempoSetting(getActivity(), dialogStyle);
                        if (dialogTempo.getWindow() != null)
                            dialogTempo.getWindow().setGravity(Gravity.TOP);
                        dialogTempo.show();
                    } else {
                        Intent intent = new Intent(context, EnableTempoActivity.class);
                        startActivityForResult(intent, 100);
                    }

                }
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
            case R.id.cardView:
//                EventBus.getDefault().post(new ReduceOverUsageEvent(true));
                if (null != getActivity()) {
                    Intent intent = new Intent(getActivity(), IntentionEditActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
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
                            if (permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS)
                                    && permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION)
                                    && PackageUtil.isSiempoLauncher(context)  &&
                                    UIUtils.hasUsageStatsPermission(context)) {
                                TypedValue typedValue = new TypedValue();
                                Resources.Theme theme = context.getTheme();
                                theme.resolveAttribute(R.attr.dialog_style, typedValue, true);
                                int dialogStyle = typedValue.resourceId;
                                DialogTempoSetting dialogTempo = new
                                        DialogTempoSetting(getActivity(), dialogStyle);
                                if (dialogTempo.getWindow() != null)
                                    dialogTempo.getWindow().setGravity(Gravity.TOP);
                                dialogTempo.show();
                            } else {
                                Intent intent = new Intent(context, EnableTempoActivity.class);
                                startActivityForResult(intent, 100);
                            }
                        }
                    }
                });
                linSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Code for opening Tempo Settings
                        if (getActivity() != null) {
                            Intent intent = new Intent(getActivity(), SettingsActivity_.class);
                            startActivity(intent);
                            UIUtils.clearDim(root);
                            mPopupWindow.dismiss();
                        }
                    }
                });
                linHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getActivity() != null) {
                            UIUtils.clearDim(root);
                            mPopupWindow.dismiss();
                            Intent intent = new Intent(getActivity(), HelpActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mPopupWindow.showAsDropDown(imgOverFlow, 0, (int) -imgOverFlow.getX() - 10);
                UIUtils.applyDim(root, 0.7f);
                if (null != getActivity()) {
                    UIUtils.hideSoftKeyboard(getActivity(), getActivity().getWindow().getDecorView().getWindowToken());
                }
                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        UIUtils.clearDim(root);

                    }
                });
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imgTempo.performClick();
        }
    }


}
