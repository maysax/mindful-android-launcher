package co.siempo.phone.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.receivers.ScreenOffAdminReceiver;
import co.siempo.phone.utils.PermissionUtil;
import co.siempo.phone.utils.PrefSiempo;

import static android.content.Context.NOTIFICATION_SERVICE;

@EFragment(R.layout.fragment_icon_labels)
public class IconLabelsFragment extends CoreFragment {

    @ViewById
    Toolbar toolbar;

    @ViewById
    Switch switchIconToolsVisibility;

    @ViewById
    Switch switchIconFavoriteVisibility;

    @ViewById
    Switch switchIconJunkFoodVisibility;




    public IconLabelsFragment() {
        // Required empty public constructor
    }


    @AfterViews
    void afterViews() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.icon_label);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });

        switchIconToolsVisibility.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_ICON_TOOLS_TEXT_VISIBILITY_ENABLE, false));
        switchIconToolsVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch sb = (Switch) v;
                if (sb.isChecked()) {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_TOOLS_TEXT_VISIBILITY_ENABLE, true);
                } else  {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_TOOLS_TEXT_VISIBILITY_ENABLE, false);
                }
            }
        });

        switchIconFavoriteVisibility.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_ICON_FAVORITE_TEXT_VISIBILITY_ENABLE, false));
        switchIconFavoriteVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch sb = (Switch) v;
                if (sb.isChecked()) {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_FAVORITE_TEXT_VISIBILITY_ENABLE, true);
                } else  {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_FAVORITE_TEXT_VISIBILITY_ENABLE, false);
                }
            }
        });

        switchIconJunkFoodVisibility.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_ICON_JUNKFOOD_TEXT_VISIBILITY_ENABLE, false));
        switchIconJunkFoodVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch sb = (Switch) v;
                if (sb.isChecked()) {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_JUNKFOOD_TEXT_VISIBILITY_ENABLE, true);
                } else  {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_JUNKFOOD_TEXT_VISIBILITY_ENABLE, false);
                }
            }
        });

    }

}
