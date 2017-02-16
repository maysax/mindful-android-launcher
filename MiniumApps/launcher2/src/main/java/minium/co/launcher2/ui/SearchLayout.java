package minium.co.launcher2.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eyeem.chips.BubbleStyle;
import com.eyeem.chips.ChipsEditText;
import com.joanzapata.iconify.widget.IconTextView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.launcher2.R;
import minium.co.launcher2.data.ActionItemManager;
import minium.co.launcher2.events.ActionItemUpdateEvent;
import minium.co.launcher2.events.BackPressEvent;
import minium.co.launcher2.events.ImeActionDoneEvent;
import minium.co.launcher2.model.ActionItem;

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

    @Bean
    ActionItemManager manager;

    private boolean isWatching;

    /**
     * "@Text|@Al d. zo|Hello world
     * text|@Alfred d. zone|Hello World;
     * text|@Alfred d. zone|Hello world|;
     * @Alfred d.zone|@Text|Hello world;
     * Text|Alfred d. zone|Hello World;
     */
    private String formattedText = "";

    private String previousText = "";

    private int nextAvailablePos = 0;


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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
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
            String str = s.replaceAll("@", "").replaceAll("#", "");
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

        previousText = newText;
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
                isWatching = false;
                manager.clear();
                isWatching = true;
            }
        });
    }

    private void handleAfterTextChanged(Editable s) {
        if (s.length() != 0) {
            btnClear.setVisibility(VISIBLE);
        } else {
            btnClear.setVisibility(INVISIBLE);
        }

        if (isWatching) {
            if (previousText.length() > s.length()) {
                if (formattedText.endsWith("|")) {
                    manager.onTextUpdate("", -2);
                } else {
                    manager.onTextUpdate(String.valueOf(previousText.charAt(previousText.length() - 1)), -1);
                }
            } else if (previousText.length() <= s.length()) {
                manager.onTextUpdate(s.subSequence(getCurrentAvailablePos(), s.length()).toString(), 1);
            }
            manager.fireEvent();
        }

    }

    private int getCurrentAvailablePos() {
        List<ActionItem> items = manager.getItems();
        int ret = 0;
        for ( ActionItem item : items ) {
            if (item.isCompleted() && item.getType() != ActionItem.ActionItemType.CONTACT_NUMBER) {
                ret += item.getActionText().length() + 1;
            }
        }
        return ret;
    }

    @Subscribe
    public void onEvent(ActionItemUpdateEvent event) {
        formattedTextBuilder();
    }

    private void formattedTextBuilder() {
        isWatching = false;
        List<ActionItem> items = manager.getItems();
        String ret = "";
        for (ActionItem item : items) {
            if (item.isCompleted()) {
                if (item.getType() == ActionItem.ActionItemType.TEXT ||
                        item.getType() == ActionItem.ActionItemType.CALL ||
                        item.getType() == ActionItem.ActionItemType.NOTE) {

                    ret += "@";
                    ret += item.getActionText() + "|";
                } else if (item.getType() == ActionItem.ActionItemType.CONTACT) {
                    if (manager.has(ActionItem.ActionItemType.CONTACT_NUMBER) && manager.get(ActionItem.ActionItemType.CONTACT_NUMBER).isCompleted()) {
                        ret += "@";
                    } else {
                        ret += "#";
                    }
                    ret += item.getActionText() + "|";
                } else if (item.getType() == ActionItem.ActionItemType.CONTACT_NUMBER) {
                    if (!manager.has(ActionItem.ActionItemType.CONTACT)) {
                        ret += "@";
                        ret += item.getActionText() + "|";
                    }
                } else
                    ret += item.getActionText() + "|";

            } else {
                ret += item.getActionText();
            }
        }
        Tracer.d("Previous: " + formattedText + " New: " + ret);
        formattedText = ret;
        initFormattedText();
        isWatching = true;
    }

    @Subscribe
    public void onBackPressed(BackPressEvent event) {
        if (btnClear.getVisibility() == VISIBLE)
            btnClear.performClick();
    }
}
