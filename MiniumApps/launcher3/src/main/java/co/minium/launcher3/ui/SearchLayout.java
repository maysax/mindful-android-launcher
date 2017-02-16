package co.minium.launcher3.ui;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eyeem.chips.ChipsEditText;
import com.joanzapata.iconify.widget.IconTextView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;

import co.minium.launcher3.R;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

import static minium.co.core.log.LogConfig.TRACE_TAG;

/**
 * Created by Shahab on 2/16/2017.
 */
@EViewGroup(R.layout.search_layout)
public class SearchLayout extends CardView {

    @ViewById
    protected ChipsEditText txtSearchBox;

    @ViewById
    protected IconTextView btnClear;

    private boolean isWatching;

    public SearchLayout(Context context) {
        super(context);
        init();
    }

    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        isWatching = true;
        setCardElevation(4.0f);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupViews();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Trace(tag = TRACE_TAG)
    void setupViews() {
        txtSearchBox.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        txtSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                handleAfterTextChanged(s);
            }
        });

        txtSearchBox.setText("");
    }

    private void handleAfterTextChanged(Editable s) {
        if (s.length() != 0) {
            btnClear.setVisibility(VISIBLE);
        } else {
            btnClear.setVisibility(INVISIBLE);
        }
    }

    @Click
    void btnClear() {
        isWatching = false;
        txtSearchBox.setText("");
        isWatching = true;
    }

    @Subscribe
    public void fao(Object obj) {

    }
}
