package minium.co.launcher2.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import minium.co.launcher2.events.ImeActionDoneEvent;
import minium.co.launcher2.events.SearchTextChangedEvent;
import minium.co.launcher2.search.SearchTextChangeHandler;

/**
 * Created by Shahab on 4/26/2016.
 */
@EViewGroup(R.layout.search_layout)
public class SearchLayout extends LinearLayout {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "SearchLayout";

    @ViewById
    protected ChipsEditText txtSearchBox;

    @ViewById
    protected IconTextView btnClear;

    private boolean isWatching;

    /**
     * "@Text|@Al d. zo|Hello world
     * text|@Alfred d. zone|Hello World;
     * text|@Alfred d. zone|Hello world|;
     * @Alfred d.zone|@Text|Hello world;
     * Text|Alfred d. zone|Hello World;
     */
    private String formattedText = "";


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
        setOrientation(HORIZONTAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupViews();

    }

    void initFormattedText() {
        String[] splits = formattedText.split("\\|");

        String newText = "";
        for (String s : splits) {
            String str = s.replaceAll("@", "");
            str += " ";
            newText += str;
        }

        if (!formattedText.endsWith("|")) newText = newText.substring(0, newText.length() - 1);

        txtSearchBox.setText(newText);

        int startPos = 0;
        int endPos = 0;
        for (String s : splits) {
            endPos += s.length();
            if (s.startsWith("@")) {
                txtSearchBox.makeChip(startPos, endPos - 1, false);
            } else {
                endPos++; // space
            }

            startPos = endPos;
        }

        txtSearchBox.setSelection(newText.length());
    }

    @Trace(tag = TRACE_TAG)
    void setupViews() {
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

        txtSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Tracer.d("Keyboard done pressed.");
                    EventBus.getDefault().post(new ImeActionDoneEvent());
                    return false;   // restoring default behavior, dismiss keyboard on press
                }

                return false;
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
        } else {
            btnClear.setVisibility(INVISIBLE);
        }

        if (isWatching)
            EventBus.getDefault().post(new SearchTextChangedEvent(s.toString()));

        //updatedFormattedText(s.toString());
    }
}
