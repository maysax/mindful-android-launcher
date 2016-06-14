package minium.co.launcher2.helper;

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

    public void onActionItemUpdate(MainActivity activity, ActionItemManager manager) {
        ActionItem current = manager.getCurrent();
        if (current == ActionItem.TEXT) {

        }

        if (current == ActionItem.CONTACT) {
            if (current.isCompleted()) {
                manager.add(ActionItem.DATA);
                activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.SEND));
            } else
                activity.onEvent(new LoadFragmentEvent(LoadFragmentEvent.CONTACTS_LIST));
        }
    }
}
