package co.siempo.phone.activities;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;
import android.widget.ViewFlipper;

import co.siempo.phone.R;
import co.siempo.phone.customviews.LockEditText;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;

public class IntentionEditActivity extends CoreActivity implements View.OnClickListener {

    private TextView txtSave;
    private Toolbar toolbar;
    private TextView hint;
    private LockEditText edtIntention;
    private ImageView imgClear;
    private LinearLayout linEditText;
    private TextView txtHelp;
    private ViewFlipper viewFlipper;
    private TextView txtOne;
    private TextView txtTwo;
    private LinearLayout linHelpWindow;
    private RelativeLayout pauseContainer;
    private String strIntentField;
    private long startTime = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Transition enterTrans = new Explode();
        getWindow().setEnterTransition(enterTrans);

        Transition returnTrans = new Slide();
        getWindow().setReturnTransition(returnTrans);
        setContentView(R.layout.activity_intention_edit);
        initView();
        bindView();
    }


    void bindView() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.hideSoftKeyboard(IntentionEditActivity.this, getWindow().getDecorView().getWindowToken());
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        strIntentField = PrefSiempo.getInstance(this).read(PrefSiempo.DEFAULT_INTENTION, "");
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
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (txtSave.getVisibility() == View.VISIBLE) {
                        PrefSiempo.getInstance(IntentionEditActivity.this).write(PrefSiempo.DEFAULT_INTENTION, strIntentField);
                        UIUtils.hideSoftKeyboard(IntentionEditActivity.this, getWindow().getDecorView().getWindowToken());
                        if (!strIntentField.equalsIgnoreCase("")) {
                            runAnimation();
                        } else {
                            finish();
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        edtIntention.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0) {
                    imgClear.setVisibility(View.VISIBLE);
                } else {
                    imgClear.setVisibility(View.GONE);
                }

                if (PrefSiempo.getInstance(IntentionEditActivity.this).read(PrefSiempo.DEFAULT_INTENTION, "").equalsIgnoreCase(s.toString())) {
                    txtSave.setVisibility(View.GONE);
                } else {
                    strIntentField = s.toString();
                    txtSave.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    void imgClear() {
        edtIntention.setText("");
    }

    void txtSave() {
        PrefSiempo.getInstance(IntentionEditActivity.this).write(PrefSiempo.DEFAULT_INTENTION, strIntentField);
        UIUtils.hideSoftKeyboard(IntentionEditActivity.this, getWindow().getDecorView().getWindowToken());
        if (!strIntentField.equalsIgnoreCase("")) {
            runAnimation();
        } else {
            finish();
        }
    }

    private void runAnimation() {
        toolbar.animate().alpha(0.0f).setDuration(200);
        hint.animate().alpha(0.0f).setDuration(200);
        imgClear.animate().alpha(0.0f).setDuration(200);
        linHelpWindow.animate().alpha(0.0f).setDuration(200);
        hint.setVisibility(View.GONE);

        imgClear.setVisibility(View.GONE);
        edtIntention.setFocusable(false);

        ValueAnimator anim = ValueAnimator.ofInt(linEditText.getMeasuredHeight() + UIUtils.getStatusBarHeight(this) + 40, pauseContainer.getHeight());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = linEditText.getLayoutParams();
                layoutParams.height = val;
                toolbar.setVisibility(View.GONE);
                linEditText.setLayoutParams(layoutParams);
            }
        });
        int from = getWindow().getNavigationBarColor();
        int to = ContextCompat.getColor(this, R.color.dialog_blue); // new color to
        // animate to

        ValueAnimator colorAnimation = ValueAnimator.ofArgb(from, to);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                getWindow().setNavigationBarColor((Integer) animator.getAnimatedValue());
            }
        });

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
            }
        });
        colorAnimation.setDuration(500);
        colorAnimation.start();

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }, 1500);
            }
        });
        anim.setDuration(500);
        anim.start();
    }

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
            txtOne.setText("PREVIOUS");
            txtTwo.setText("NEXT");
        }

    }

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

    public void txtHelp() {
        txtOne.setText("CLOSE");
        txtTwo.setText("NEXT");
        txtHelp.setVisibility(View.GONE);
        viewFlipper.setInAnimation(null);
        viewFlipper.setOutAnimation(null);
        viewFlipper.setDisplayedChild(0);
        linHelpWindow.setVisibility(View.VISIBLE);
    }


    private void initView() {
        txtSave = findViewById(R.id.txtSave);
        txtSave.setOnClickListener(this);
        toolbar = findViewById(R.id.toolbar);
        hint = findViewById(R.id.hint);
        edtIntention = findViewById(R.id.edtIntention);
        imgClear = findViewById(R.id.imgClear);
        imgClear.setOnClickListener(this);
        linEditText = findViewById(R.id.linEditText);
        txtHelp = findViewById(R.id.txtHelp);
        txtHelp.setOnClickListener(this);
        viewFlipper = findViewById(R.id.viewFlipper);
        txtOne = findViewById(R.id.txtOne);
        txtOne.setOnClickListener(this);
        txtTwo = findViewById(R.id.txtTwo);
        txtTwo.setOnClickListener(this);
        linHelpWindow = findViewById(R.id.linHelpWindow);
        pauseContainer = findViewById(R.id.pauseContainer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.txtSave:
                txtSave();
                break;
            case R.id.imgClear:
                imgClear();
                break;
            case R.id.txtHelp:
                txtHelp();
                break;
            case R.id.txtOne:
                txtOne();
                break;
            case R.id.txtTwo:
                txtTwo();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getInstance().logScreenUsageTime(this.getClass().getSimpleName(), startTime);
    }
}