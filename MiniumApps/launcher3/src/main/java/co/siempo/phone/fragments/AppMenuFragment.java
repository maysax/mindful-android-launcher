package co.siempo.phone.fragments;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import co.siempo.phone.R;
import co.siempo.phone.utils.PrefSiempo;


public class AppMenuFragment extends CoreFragment implements View.OnClickListener {


    private View view;
    private Toolbar toolbar;
    private RelativeLayout relJunkFoodmize;
    private RelativeLayout relHideIconBranding;
    private Switch switchJunkFoodmize, switchHideIcon;
    private Context context;

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
        switchHideIcon.setChecked(PrefSiempo.getInstance(context).read(PrefSiempo.IS_ICON_BRANDING, true));

        switchJunkFoodmize = view.findViewById(R.id.switchJunkFoodmize);
        switchJunkFoodmize.setChecked(PrefSiempo.getInstance(context).read(PrefSiempo.IS_RANDOMIZE_JUNKFOOD, true));

        relJunkFoodmize = view.findViewById(R.id.relJunkFoodmize);
        relJunkFoodmize.setOnClickListener(this);

        relHideIconBranding = view.findViewById(R.id.relHideIconBranding);
        relHideIconBranding.setOnClickListener(this);


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
                } else {
                    switchJunkFoodmize.setChecked(true);
                    PrefSiempo.getInstance(context).write(PrefSiempo.IS_RANDOMIZE_JUNKFOOD, true);
                }

                break;
            case R.id.relHideIconBranding:
                if (switchHideIcon.isChecked()) {
                    switchHideIcon.setChecked(false);
                    PrefSiempo.getInstance(context).write(PrefSiempo.IS_ICON_BRANDING, false);
                } else {
                    switchHideIcon.setChecked(true);
                    PrefSiempo.getInstance(context).write(PrefSiempo.IS_ICON_BRANDING, true);
                }
                break;
        }
    }
}
