package minium.co.launcher2.helper;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.UiThread;

import de.greenrobot.event.EventBus;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.launcher2.MainActivity;
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
                MainActivity.SELECTED_OPTION = 1;
                EventBus.getDefault().post(new MakeChipEvent(0, KEY_TEXT.length(), "Text"));

            } else if (txt.toLowerCase().startsWith(KEY_CALL)) {
                MainActivity.SELECTED_OPTION = 2;
                EventBus.getDefault().post(new MakeChipEvent(0, KEY_CALL.length(), "Call"));

            } else if (txt.toLowerCase().startsWith(KEY_NOTES)) {
                MainActivity.SELECTED_OPTION = 3;
                EventBus.getDefault().post(new MakeChipEvent(0, KEY_NOTES.length(), "Note"));

            }
        } else if (txt.length() == 5) {
            if (txt.toLowerCase().startsWith(KEY_TEXT) || txt.toLowerCase().startsWith(KEY_CALL)) {
                EventBus.getDefault().post(new LoadFragmentEvent(LoadFragmentEvent.CONTACTS_LIST));
            }
        } else if (txt.length() > 5) {
            EventBus.getDefault().post(new FilterContactsEvent(txt.substring(5)));
        } else {
            // TODO: may be loading same fragment over and over again
            EventBus.getDefault().post(new LoadFragmentEvent(LoadFragmentEvent.MAIN_FRAGMENT));
        }
    }
}