package co.siempo.phone.fragments;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import co.siempo.phone.R;
import co.siempo.phone.activities.JunkfoodFlaggingActivity;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.NotifyBottomView;
import co.siempo.phone.event.NotifyFavortieView;
import co.siempo.phone.event.NotifyJunkFoodView;
import co.siempo.phone.event.NotifyToolView;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.utils.PrefSiempo;
import de.greenrobot.event.EventBus;


public class AppMenuFragment extends CoreFragment implements View.OnClickListener {


    private View view;
    private Toolbar toolbar;
    private RelativeLayout relJunkFoodmize;
    private RelativeLayout relHideIconBranding;
    private RelativeLayout mRelChooseFlagapps;
    private RelativeLayout mRelOverUseScreen;
    private RelativeLayout mRelOverUseFlaggedApp;
    private Switch switchJunkFoodmize, switchHideIcon;
    private Context context;
    private long startTime = 0;

    public AppMenuFragment() {
        // Required empty public constructor
    }

    public static AppMenuFragment newInstance() {
        return new AppMenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_app_menu, container, false);
        initView(view);
        return view;
    }


    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.app_menus);
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });

        switchHideIcon = view.findViewById(R.id.switchHideIcon);
        switchHideIcon.setChecked(CoreApplication.getInstance().isHideIconBranding());

        switchJunkFoodmize = view.findViewById(R.id.switchJunkFoodmize);
        switchJunkFoodmize.setChecked(CoreApplication.getInstance().isRandomize());

        relJunkFoodmize = view.findViewById(R.id.relJunkFoodmize);
        relJunkFoodmize.setOnClickListener(this);

        relHideIconBranding = view.findViewById(R.id.relHideIconBranding);
        relHideIconBranding.setOnClickListener(this);

        mRelChooseFlagapps = view.findViewById(R.id.relChooseFlagApp);
        mRelChooseFlagapps.setOnClickListener(this);

        mRelOverUseScreen = view.findViewById(R.id.relReduceOveruseScreen);
        mRelOverUseScreen.setOnClickListener(this);

        mRelOverUseFlaggedApp = view.findViewById(R.id.relReduceOveruseFlagged);
        mRelOverUseFlaggedApp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.relJunkFoodmize:
                if (switchJunkFoodmize.isChecked()) {
                    switchJunkFoodmize.setChecked(false);
                    PrefSiempo.getInstance(context).write(PrefSiempo.IS_RANDOMIZE_JUNKFOOD, false);
                    CoreApplication.getInstance().setRandomize(false);
                } else {
                    switchJunkFoodmize.setChecked(true);
                    PrefSiempo.getInstance(context).write(PrefSiempo.IS_RANDOMIZE_JUNKFOOD, true);
                    CoreApplication.getInstance().setRandomize(true);
                }
                EventBus.getDefault().postSticky(new NotifyJunkFoodView(true));
                break;
            case R.id.relHideIconBranding:
                if (switchHideIcon.isChecked()) {
                    switchHideIcon.setChecked(false);
                    PrefSiempo.getInstance(context).write(PrefSiempo.IS_ICON_BRANDING, false);
                    CoreApplication.getInstance().setHideIconBranding(false);
                } else {
                    switchHideIcon.setChecked(true);
                    PrefSiempo.getInstance(context).write(PrefSiempo.IS_ICON_BRANDING, true);
                    CoreApplication.getInstance().setHideIconBranding(true);
                }
                EventBus.getDefault().postSticky(new NotifyJunkFoodView(true));
                EventBus.getDefault().postSticky(new NotifyFavortieView(true));
                EventBus.getDefault().postSticky(new NotifyToolView(true));
                EventBus.getDefault().postSticky(new NotifyBottomView(true));
                break;

            case R.id.relChooseFlagApp:
                Intent junkFoodFlagIntent = new Intent(context, JunkfoodFlaggingActivity.class);
                junkFoodFlagIntent.putExtra("FromAppMenu", true);
                startActivity(junkFoodFlagIntent);
                break;

            case R.id.relReduceOveruseFlagged:
                //UIUtils.alert(context,"Deter Dialog","Deter Dialog");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        FirebaseHelper.getInstance().logScreenUsageTime(this.getClass().getSimpleName(), startTime);
    }
}
