package minium.co.launcher2.helper;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.Trace;

import de.greenrobot.event.EventBus;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.launcher2.events.FilterContactsEvent;
import minium.co.launcher2.events.LoadFragmentEvent;
import minium.co.launcher2.events.MakeChipEvent;
import minium.co.launcher2.events.SearchTextChangedEvent;

/**
 * Created by Shahab on 5/2/2016.
 */
@EBean
public class SearchTextParser {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "SearchTextParser";

    private final String KEY_TEXT = "text";
    private final String KEY_CALL = "call";
    private final String KEY_NOTES = "note";


    @Trace(tag = TRACE_TAG)
    public void onTextChanged(SearchTextChangedEvent event) {
        String txt = event.getText();

        if (txt.length() == 4) {
            if (txt.toLowerCase().startsWith(KEY_TEXT)) {
                EventBus.getDefault().post(new MakeChipEvent(0, KEY_TEXT.length(), "Text"));

            } else if (txt.toLowerCase().startsWith(KEY_CALL)) {
                EventBus.getDefault().post(new MakeChipEvent(0, KEY_CALL.length(), "Call"));

            } else if (txt.toLowerCase().startsWith(KEY_NOTES)) {
                EventBus.getDefault().post(new MakeChipEvent(0, KEY_NOTES.length(), "Note"));

            }
        } else if (txt.length() == 6) {
            if (txt.toLowerCase().startsWith(KEY_TEXT)) {
                EventBus.getDefault().post(new LoadFragmentEvent(LoadFragmentEvent.CONTACTS_LIST));

            }
        } else if (txt.length() > 6) {
            EventBus.getDefault().post(new FilterContactsEvent());
        } else {
            // TODO: may be loading same fragment over and over again
            EventBus.getDefault().post(new LoadFragmentEvent(LoadFragmentEvent.MAIN_FRAGMENT));
        }
    }
}