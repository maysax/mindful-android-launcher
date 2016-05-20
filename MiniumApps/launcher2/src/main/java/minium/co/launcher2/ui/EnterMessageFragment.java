package minium.co.launcher2.ui;


import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.messages.SmsObserver;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_enter_message)
public class EnterMessageFragment extends CoreFragment  {

    @ViewById
    EditText composeReplyText;

    @ViewById
    TextView btnSendText;

    @FragmentArg
    String phoneNumber;

    public EnterMessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        resetViews();
    }

    void resetViews() {
        btnSendText.setText("{fa-paper-plane 24dp}");
        btnSendText.setClickable(true);
        composeReplyText.setText("");
    }

    @AfterViews
    void afterViews() {
        composeReplyText.requestFocus();
    }

    @Click
    void btnSendText() {
        if (composeReplyText.getText().length() == 0) {
            UIUtils.alert(getActivity(), "Message box is empty. Please enter text into message box.");
            return;
        }
        btnSendText.setText("{fa-spinner 24dp spin}");
        btnSendText.setClickable(false);

        try {
            new SmsObserver(getActivity(), phoneNumber, composeReplyText.getText().toString()).start();

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, composeReplyText.getText().toString() , null, null);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
            resetViews();
            UIUtils.toast(getActivity(), "The message will not get sent.");
        }
    }
}
