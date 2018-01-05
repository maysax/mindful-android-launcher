package co.siempo.phone;


import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.app.Launcher3App;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

@EActivity(R.layout.activity_intention_edit)
public class IntentionEditActivity extends CoreActivity {
    @ViewById
    Toolbar toolbar;
    @ViewById
    TextView txtSave;
    @ViewById
    EditText edtIntention;
    @ViewById
    ImageView imgClear;
    @Pref
    DroidPrefs_ droidPrefs;
    String strIntentField;
    private String TAG = "IntentionEditActivity";

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
        }
    }


    @AfterViews
    void afterViews() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        strIntentField = droidPrefs.defaultIntention().get();
        edtIntention.setText(strIntentField);
        edtIntention.setHorizontallyScrolling(false);
        edtIntention.setMaxLines(2);
        edtIntention.setFilters(new InputFilter[]{new InputFilter.LengthFilter(48)});
        edtIntention.setSelection(edtIntention.getText().length());
        txtSave.setVisibility(View.GONE);

        if (strIntentField.trim().length() > 0) {
            imgClear.setVisibility(View.VISIBLE);
        } else {
            imgClear.setVisibility(View.GONE);
        }
        edtIntention.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    droidPrefs.defaultIntention().put(strIntentField);
                    UIUtils.hideSoftKeyboard(IntentionEditActivity.this, getWindow().getDecorView().getWindowToken());
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    @EditorAction(R.id.edtIntention)
    void edtIntention(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            droidPrefs.defaultIntention().put(strIntentField);
            UIUtils.hideSoftKeyboard(IntentionEditActivity.this, getWindow().getDecorView().getWindowToken());
            finish();
        }
    }


    @AfterTextChange(R.id.edtIntention)
    void edtIntention(Editable s, TextView hello) {
        if (s.toString().trim().length() > 0) {
            imgClear.setVisibility(View.VISIBLE);
        } else {
            imgClear.setVisibility(View.GONE);
        }

        if (droidPrefs.defaultIntention().get().equalsIgnoreCase(s.toString())) {
            txtSave.setVisibility(View.GONE);
        } else {
            strIntentField = s.toString();
            txtSave.setVisibility(View.VISIBLE);
        }
    }


    @Click
    void imgClear() {
        edtIntention.setText("");
    }

    @Click
    void txtSave() {
        droidPrefs.defaultIntention().put(strIntentField);
        UIUtils.hideSoftKeyboard(IntentionEditActivity.this, getWindow().getDecorView().getWindowToken());
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


}