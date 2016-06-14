package minium.co.launcher2.helper;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import minium.co.core.log.LogConfig;
import minium.co.launcher2.data.ActionItemManager;
import minium.co.launcher2.model.ActionItem;

/**
 * Created by Shahab on 5/2/2016.
 */
@EBean
public class SearchTextParser {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "SearchTextParser";

    private final String KEY_TEXT = "text";
    private final String KEY_CALL = "call";
    private final String KEY_NOTES = "note";

    @Bean
    ActionItemManager manager;

    public void onClickedActionItem(int position) {
        if (position == 0) {
            manager.setCurrent(ActionItem.TEXT);
        } else if (position == 1) {
            manager.setCurrent(ActionItem.CALL);
        } else if (position == 2) {
            manager.setCurrent(ActionItem.NOTE);
        }
    }
}