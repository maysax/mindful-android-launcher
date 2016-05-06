package minium.co.launcher2.ui;


import android.os.Bundle;
import android.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.R;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_enter_message)
public class EnterMessageFragment extends CoreFragment {

    @ViewById
    EditText composeReplyText;

    @FragmentArg
    String phoneNumber;

    public EnterMessageFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        composeReplyText.requestFocus();
    }

    @Click
    void btnSendText() {
        // UIUtils.alert(getActivity(), "Sending sms...");
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, composeReplyText.getText().toString() , null, null);
    }
}
