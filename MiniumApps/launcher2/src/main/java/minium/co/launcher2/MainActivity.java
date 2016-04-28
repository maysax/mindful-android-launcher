package minium.co.launcher2;

import android.hardware.input.InputManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;

@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    @ViewById
    ViewGroup parentLayout;

    @SystemService
    InputMethodManager im;

    SoftKeyboard softKeyboard;

    @AfterViews
    void afterViews() {

        softKeyboard = new SoftKeyboard(parentLayout, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged()
        {

            @Override
            public void onSoftKeyboardHide()
            {
                // Code here
                Tracer.i("onSoftKeyboardHide");
            }

            @Override
            public void onSoftKeyboardShow()
            {
                // Code here
                Tracer.i("onSoftKeyboardShow");
            }
        });
    }

    /* Prevent memory leaks:
*/
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        softKeyboard.unRegisterSoftKeyboardCallback();
    }
}
