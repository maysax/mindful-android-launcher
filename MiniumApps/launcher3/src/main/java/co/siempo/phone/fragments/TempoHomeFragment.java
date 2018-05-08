package co.siempo.phone.fragments;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.siempo.phone.R;
import co.siempo.phone.activities.ChooseBackgroundActivity;
import co.siempo.phone.event.NotifyBackgroundChange;
import co.siempo.phone.utils.PrefSiempo;
import de.greenrobot.event.EventBus;

@EFragment(R.layout.fragment_tempo_home)
public class TempoHomeFragment extends CoreFragment {

    @ViewById
    Toolbar toolbar;

    @ViewById
    Switch switchDisableIntentionsControls;

    @ViewById
    RelativeLayout relAllowSpecificApps;

    @ViewById
    Switch switchCustomBackground;

    @ViewById
    RelativeLayout relCustomBackground;

    @ViewById
    Switch switchDarkTheme;

    @ViewById
    RelativeLayout relDarkTheme;

    public TempoHomeFragment() {
        // Required empty public constructor
    }


    @AfterViews
    void afterViews() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.homescreen);
//        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color
//                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });
        switchDisableIntentionsControls.setChecked(PrefSiempo.getInstance(context).read(PrefSiempo.IS_INTENTION_ENABLE, false));


        switchDisableIntentionsControls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefSiempo.getInstance(context).write(PrefSiempo
                        .IS_INTENTION_ENABLE, isChecked);
            }
        });

        if (PrefSiempo.getInstance(context).read(PrefSiempo
                .IS_DARK_THEME, false)) {
            switchDarkTheme.setChecked(true);
        } else {
            switchDarkTheme.setChecked(false);
        }

        switchDarkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefSiempo.getInstance(context).write(PrefSiempo
                        .IS_DARK_THEME, isChecked);
                if (isChecked) {
                    getActivity().setTheme(R.style.SiempoAppThemeDark);
                } else {
                    getActivity().setTheme(R.style.SiempoAppTheme);
                }
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startActivity(startMain);


            }
        });
    }

    @Click
    void relAllowSpecificApps() {
        switchDisableIntentionsControls.performClick();
    }

    @Click
    void relCustomBackground() {
        if (PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_BAG, "").equalsIgnoreCase("")) {
            startActivity(new Intent(context, ChooseBackgroundActivity.class));
        } else {
            PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_BAG, "");
            EventBus.getDefault().postSticky(new NotifyBackgroundChange(true));
            switchCustomBackground.setChecked(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        switchCustomBackground.setChecked(!PrefSiempo.getInstance(context).read(PrefSiempo
                .DEFAULT_BAG, "").equalsIgnoreCase(""));
    }

    @Click
    void relDarkTheme() {
        switchDarkTheme.performClick();
    }


}
