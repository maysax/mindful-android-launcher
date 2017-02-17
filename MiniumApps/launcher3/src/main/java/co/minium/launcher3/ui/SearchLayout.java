package co.minium.launcher3.ui;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import com.eyeem.chips.BubbleStyle;
import com.eyeem.chips.ChipsEditText;
import com.joanzapata.iconify.widget.IconTextView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;

import co.minium.launcher3.R;
import co.minium.launcher3.event.SearchLayoutEvent;
import co.minium.launcher3.token.TokenCompleteType;
import co.minium.launcher3.token.TokenItem;
import co.minium.launcher3.token.TokenManager;
import co.minium.launcher3.token.TokenUpdateEvent;
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

    @Bean
    TokenManager manager;

    private String formattedTxt;
    private boolean isWatching = true;

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

        if (isWatching) {
            EventBus.getDefault().post(new SearchLayoutEvent(s.toString()));
        }
    }

    @Click
    void btnClear() {
        txtSearchBox.setText("");
    }

    @Subscribe
    public void tokenManagerEvent(TokenUpdateEvent event) {
        buildFormattedText();
        updateSearchField();
    }

    private void updateSearchField() {
        String[] splits = formattedTxt.split("\\|");

        String newText = "";
        boolean space = false;
        for (String s : splits) {
            if (space) newText += " "; space = true;
            newText += s.replaceAll("@", "").replaceAll("#", "");
        }

        if (formattedTxt.endsWith("|")) newText += " ";

        isWatching = false;
        txtSearchBox.setText(newText);
        isWatching = true;

        int startPos = 0;
        int endPos = 0;
        for (String s : splits) {
            endPos += s.length();
            if (s.startsWith("@")) {
                txtSearchBox.setCurrentBubbleStyle(BubbleStyle.build(getContext(), R.style.bubble_style_selected));
                txtSearchBox.makeChip(startPos, endPos - 1, false);
            } else if (s.startsWith("#")) {
                txtSearchBox.setCurrentBubbleStyle(BubbleStyle.build(getContext(), R.style.bubble_style_empty));
                txtSearchBox.makeChip(startPos, endPos - 1, false);
            } else {
                endPos++; // space
            }

            startPos = endPos;
        }
        txtSearchBox.setSelection(newText.length());
    }

    private void buildFormattedText() {
        formattedTxt = "";

        for (TokenItem item : manager.getItems()) {
            if (item.getCompleteType() == TokenCompleteType.FULL) {
                if (item.isChipable()) {
                    formattedTxt += "@";
                    formattedTxt += item.getTitle() + "|";
                }
            } else if (item.getCompleteType() == TokenCompleteType.HALF) {
                if (item.isChipable()) {
                    formattedTxt += "#";
                    formattedTxt += item.getTitle() + "|";
                }
            } else {
                formattedTxt += item.getTitle();
            }
        }
    }
}
