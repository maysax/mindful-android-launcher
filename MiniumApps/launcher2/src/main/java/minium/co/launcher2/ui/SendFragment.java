package minium.co.launcher2.ui;


import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import minium.co.core.ui.CoreFragment;
import minium.co.launcher2.R;
import minium.co.launcher2.data.ActionItemManager;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_send)
public class SendFragment extends CoreFragment {

    @Bean
    ActionItemManager manager;


    public SendFragment() {
        // Required empty public constructor
    }

    @Click
    void btnSend(View view) {
        AppCompatButton button = (AppCompatButton) view;
        button.setClickable(false);
        button.setText("SENDING...");
        manager.getCurrent().setCompleted(true);
        manager.fireEvent();
    }

}
