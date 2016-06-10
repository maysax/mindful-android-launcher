package minium.co.launcher2.data;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import minium.co.launcher2.events.ActionItemUpdateEvent;
import minium.co.launcher2.model.ActionItem;

/**
 * Created by Shahab on 6/10/2016.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ActionItemManager {

    private List<ActionItem> actionItems = new ArrayList<>();

    public void init() {
        actionItems.add(ActionItem.EMPTY);
    }

    public void clear() {
        actionItems.clear();
        actionItems.add(ActionItem.EMPTY);
        fireEvent();
    }

    public ActionItem getCurrent() {
        return actionItems.get(actionItems.size() - 1);
    }

    public void setCurrent(ActionItem item) {
        actionItems.set(actionItems.size() - 1, item);
        fireEvent();
    }

    public List<ActionItem> getItems() {
        return actionItems;
    }

    private void fireEvent() {
        EventBus.getDefault().post(new ActionItemUpdateEvent());
    }
}
