package minium.co.launcher2.data;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

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
        actionItems.clear();
        actionItems.add(new ActionItem(ActionItem.ActionItemType.DATA));
    }

    public void clear() {
        init();
        fireEvent("");
    }

    public ActionItem get(int position) {
        return actionItems.get(position);
    }

    public ActionItem get(ActionItem.ActionItemType item) {
        for (ActionItem actionItem : actionItems) {
            if (actionItem.getType() == item) return actionItem;
        }
        return null;
    }

    public void add(ActionItem item) {
        actionItems.add(item);
    }

    public ActionItem getPrevious() {
        return actionItems.get(actionItems.size() - 2);
    }

    public ActionItem getCurrent() {
        return actionItems.get(actionItems.size() - 1);
    }

    public ActionItem getFirst() {
        return actionItems.get(0);
    }

    public void setCurrent(ActionItem item) {
        actionItems.set(actionItems.size() - 1, item);
    }

    public List<ActionItem> getItems() {
        return actionItems;
    }

    @UiThread(delay = 10L)
    public void fireEvent() {
        fireEvent(getCurrent().getActionText());
    }

    private void fireEvent(String txt) {
        EventBus.getDefault().post(new ActionItemUpdateEvent(txt));
    }

    public void removeLast() {
        actionItems.remove(actionItems.size() - 1);
    }

    public void onTextUpdate(String str, int val) {
        switch (val) {
            case -2:
                removeLast();
                removeLast();
                if (actionItems.isEmpty()) actionItems.add(new ActionItem(ActionItem.ActionItemType.EMPTY));
                break;
            case -1:
                getCurrent().removeActionText();
                break;
            case 1:
                getCurrent().addActionText(str);
                break;
        }
    }

    public boolean has(ActionItem.ActionItemType item) {
        for (ActionItem actionItem : actionItems) {
            if (actionItem.getType() == item) return true;
        }
        return false;
    }

    public int getLength() {
        return actionItems.size();
    }
}
