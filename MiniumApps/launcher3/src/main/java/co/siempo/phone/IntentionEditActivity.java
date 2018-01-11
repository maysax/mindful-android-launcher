package co.siempo.phone;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;
import android.widget.ViewFlipper;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.app.Launcher3App;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

@WindowFeature({Window.FEATURE_CONTENT_TRANSITIONS})
@EActivity(R.layout.activity_intention_edit)
public class IntentionEditActivity extends CoreActivity {
    @ViewById
    Toolbar toolbar;

    @ViewById
    TextView txtSave;

    @ViewById
    TextView txtOne;

    @ViewById
    TextView txtTwo;

    @ViewById
    ViewFlipper viewFlipper;

    @ViewById
    TextView txtHelp;

    @ViewById
    LockEditText edtIntention;
    @ViewById
    ImageView imgClear;

    @ViewById
    LinearLayout linHelpWindow;

    @ViewById
    LinearLayout linEditText;

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
        // inside your activity (if you did not enable transitions in your theme)
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.hideSoftKeyboard(IntentionEditActivity.this, getWindow().getDecorView().getWindowToken());
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
        linEditText.setTransitionName("linEditText");
        edtIntention.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (txtSave.getVisibility() == View.VISIBLE) {
                        droidPrefs.defaultIntention().put(strIntentField);
                        UIUtils.hideSoftKeyboard(IntentionEditActivity.this, getWindow().getDecorView().getWindowToken());
                        Intent intent = new Intent(IntentionEditActivity.this, IntentionConfirmationActivity_.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(IntentionEditActivity.this, linEditText, "linEditText");
                        startActivity(intent, options.toBundle());
                        finish();
                    }
                    return true;
                }
                return false;
            }
        });
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

    @SuppressLint("RestrictedApi")
    @Click
    void txtSave() {
        droidPrefs.defaultIntention().put(strIntentField);
        UIUtils.hideSoftKeyboard(IntentionEditActivity.this, getWindow().getDecorView().getWindowToken());
        if (!strIntentField.equalsIgnoreCase("")) {
            Intent intent = new Intent(IntentionEditActivity.this, IntentionConfirmationActivity_.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(IntentionEditActivity.this, linEditText, "linEditText");
            startActivity(intent, options.toBundle());
            finish();
        }
    }

    @Click
    public void txtOne() {
        if (viewFlipper.getDisplayedChild() == 0) {
            linHelpWindow.setVisibility(View.GONE);
            txtHelp.setVisibility(View.VISIBLE);
            viewFlipper.setInAnimation(null);
            viewFlipper.setOutAnimation(null);
            viewFlipper.setDisplayedChild(0);
        } else if (viewFlipper.getDisplayedChild() == 1) {
            txtOne.setText("CLOSE");
            txtTwo.setText("NEXT");
            viewFlipper.setInAnimation(this, R.anim.in_from_left);
            viewFlipper.setOutAnimation(this, R.anim.out_to_right);
            viewFlipper.showPrevious();
        } else if (viewFlipper.getDisplayedChild() == 2) {
            viewFlipper.setInAnimation(this, R.anim.in_from_left);
            viewFlipper.setOutAnimation(this, R.anim.out_to_right);
            viewFlipper.showPrevious();
        } else if (viewFlipper.getDisplayedChild() == 3) {
            viewFlipper.setInAnimation(this, R.anim.in_from_left);
            viewFlipper.setOutAnimation(this, R.anim.out_to_right);
            viewFlipper.showPrevious();
        }

    }

    @Click
    public void txtTwo() {
        if (viewFlipper.getDisplayedChild() == 0) {
            txtOne.setText("PREVIOUS");
            txtTwo.setText("NEXT");
            viewFlipper.setInAnimation(this, R.anim.in_from_right);
            viewFlipper.setOutAnimation(this, R.anim.out_to_left);
            viewFlipper.showNext();
        } else if (viewFlipper.getDisplayedChild() == 1) {
            txtOne.setText("PREVIOUS");
            txtTwo.setText("NEXT");
            viewFlipper.setInAnimation(this, R.anim.in_from_right);
            viewFlipper.setOutAnimation(this, R.anim.out_to_left);
            viewFlipper.showNext();
        } else if (viewFlipper.getDisplayedChild() == 2) {
            txtOne.setText("PREVIOUS");
            txtTwo.setText("CLOSE");
            viewFlipper.setInAnimation(this, R.anim.in_from_right);
            viewFlipper.setOutAnimation(this, R.anim.out_to_left);
            viewFlipper.showNext();
        } else if (viewFlipper.getDisplayedChild() == 3) {
            linHelpWindow.setVisibility(View.GONE);
            txtHelp.setVisibility(View.VISIBLE);

        }
    }

    @Click
    public void txtHelp() {
        txtOne.setText("CLOSE");
        txtTwo.setText("NEXT");
        txtHelp.setVisibility(View.GONE);
        viewFlipper.setInAnimation(null);
        viewFlipper.setOutAnimation(null);
        viewFlipper.setDisplayedChild(0);
        linHelpWindow.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


}