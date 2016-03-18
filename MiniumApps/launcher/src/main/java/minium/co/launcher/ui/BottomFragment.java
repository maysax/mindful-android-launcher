package minium.co.launcher.ui;


import android.support.v4.app.Fragment;
import android.widget.Button;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import minium.co.core.util.UIUtils;
import minium.co.launcher.R;


/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_bottom)
public class BottomFragment extends Fragment {

    @ViewById
    Button btn1;

    @ViewById
    Button btn2;

    @ViewById
    Button btn3;


    public BottomFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        btn1.setText("CALL");
        btn2.setText("SELECT");
        btn3.setText("MESSAGES");
    }

    @Click({R.id.btn1, R.id.btn2, R.id.btn3})
    void onClick() {
        UIUtils.alert(getContext(), "No yet implemented");
    }
}
