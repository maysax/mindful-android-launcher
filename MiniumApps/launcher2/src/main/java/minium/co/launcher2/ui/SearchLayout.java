package minium.co.launcher2.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.eyeem.chips.ChipsEditText;
import com.joanzapata.iconify.widget.IconTextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import minium.co.launcher2.R;

/**
 * Created by Shahab on 4/26/2016.
 */
@EViewGroup(R.layout.search_layout)
public class SearchLayout extends LinearLayout {

    @ViewById
    protected ChipsEditText txtSearchBox;

    @ViewById
    protected IconTextView btnClear;

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

    public SearchLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.edittext_rounded_corners);
        setOrientation(HORIZONTAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupViews();
    }

    private void setupViews() {
        btnClear.setVisibility(INVISIBLE);

        txtSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    btnClear.setVisibility(VISIBLE);
                } else {
                    btnClear.setVisibility(INVISIBLE);
                }

                if (s.toString().toLowerCase().startsWith("text")) {
                    txtSearchBox.makeChip(0, 4, true);
                }

                if (s.toString().toLowerCase().startsWith("call")) {
                    txtSearchBox.makeChip(0, 4, false);
                }
            }
        });

        btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearchBox.getText().clear();
            }
        });
    }
}
