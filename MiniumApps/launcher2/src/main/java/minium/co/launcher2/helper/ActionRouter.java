package minium.co.launcher2.helper;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import minium.co.launcher2.MainActivity;
import minium.co.launcher2.data.ActionItemManager;
import minium.co.launcher2.events.LoadFragmentEvent;
import minium.co.launcher2.model.ActionItem;

/**
 * Created by Shahab on 6/14/2016.
 */
@EBean
public class ActionRouter {

    @Bean
    ActionItemManager manager;

    public void onActionItemUpdate(MainActivity activity) {
        ActionItem current = manager.getCurrent();
        if (current == ActionItem.CONTACT) {
            activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.CONTACTS_LIST));
        }
//        else if (current == ActionItem.TEXT || current == ActionItem.CALL) {
//            manager.add(ActionItem.CONTACT);
//            loadFragment(ContactsPickerFragment_.builder().build());
//        } else if (current == ActionItem.EMPTY) {
//            loadFragment(FilterFragment_.builder().build());
//        }
    }
}
