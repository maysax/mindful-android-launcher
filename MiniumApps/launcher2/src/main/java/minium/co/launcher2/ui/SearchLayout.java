package minium.co.launcher2.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eyeem.chips.ChipsEditText;
import com.joanzapata.iconify.widget.IconTextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang3.StringUtils;

import de.greenrobot.event.EventBus;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.launcher2.R;
import minium.co.launcher2.events.SearchTextChangedEvent;

/**
 * Created by Shahab on 4/26/2016.
 */
@EViewGroup(R.layout.search_layout)
public class SearchLayout extends LinearLayout {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "SearchLayout";

    @ViewById
    protected TextView constantChar;

    @ViewById
    protected ChipsEditText txtSearchBox;

    @ViewById
    protected IconTextView btnClear;

    private boolean isWatching;

    private String formattedText;

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
        formattedText = "";
        setBackgroundResource(R.drawable.edittext_rounded_corners);
        setOrientation(HORIZONTAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupViews();

    }

    @Trace(tag = TRACE_TAG)
    void setupViews() {
        txtSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handleAfterTextChanged(s);
            }
        });

        txtSearchBox.setText("");

        btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearchBox.getText().clear();
            }
        });
    }

    private void handleAfterTextChanged(Editable s) {
        if (s.length() != 0) {
            btnClear.setVisibility(VISIBLE);
            constantChar.setTextColor(getResources().getColor(R.color.black));
        } else {
            btnClear.setVisibility(INVISIBLE);
            constantChar.setTextColor(getResources().getColor(R.color.colorAccent));
        }

        if (isWatching)
            EventBus.getDefault().post(new SearchTextChangedEvent(s.toString()));

        updatedFormattedText(s.toString());
    }

    private void updatedFormattedText(String s) {
        if (s.trim().isEmpty()) {
            formattedText = "";
        } else if (s.length() < formattedText.length()) {
            formattedText = formattedText.substring(0, s.length() + 1);
            Tracer.i("afterTextChanged: " + s + " formatted: " + formattedText);
        }
    }

    @Trace(tag = TRACE_TAG)
    public void setText(String text) {
        txtSearchBox.setText(text);
    }

    @Trace(tag = TRACE_TAG)
    public void makeChip(String text) {
        if (formattedText.toLowerCase().startsWith(text.toLowerCase())) return;
        isWatching = false;

        formattedText += text + "|";

        String[] splits = formattedText.split("\\|");
        String newText = StringUtils.join(splits, " ") + " ";
        txtSearchBox.setText(newText);


        int startPos = 0;
        int endPos = 0;
        for (String s : splits) {
            endPos += s.length();
            txtSearchBox.makeChip(startPos, endPos, false);
            endPos++; // space
            startPos = endPos;
        }
        EventBus.getDefault().post(new SearchTextChangedEvent(txtSearchBox.getText().toString()));
        isWatching = true;

        txtSearchBox.setSelection(newText.length());
    }
}
