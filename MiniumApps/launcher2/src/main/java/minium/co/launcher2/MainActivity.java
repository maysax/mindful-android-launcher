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

    @AfterViews
    void afterViews() {

    }
}
