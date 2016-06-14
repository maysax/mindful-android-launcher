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
        fireEvent("");
    }

    public void add(ActionItem item) {
        actionItems.add(item);
    }

    public ActionItem getCurrent() {
        return actionItems.get(actionItems.size() - 1);
    }

    public void setCurrent(ActionItem item) {
        actionItems.set(actionItems.size() - 1, item);
        nextRoute();

    }

    private void nextRoute() {
        if (getCurrent() == ActionItem.TEXT) {
            add(ActionItem.CONTACT);
            fireEvent(getCurrent().getActionText());
        }
    }

    public List<ActionItem> getItems() {
        return actionItems;
    }

    public void fireEvent() {
        fireEvent(getCurrent().getActionText());
    }

    private void fireEvent(String txt) {
        EventBus.getDefault().post(new ActionItemUpdateEvent(txt));
    }

    public void setActionText(String s) {
        getCurrent().setActionText(s);
        fireEvent(s);
    }

    private void removeLast() {
        actionItems.remove(actionItems.size() - 1);
    }

    public void onTextUpdate(char ch, int val) {
        switch (val) {
            case -2:
                removeLast();
                removeLast();
                if (actionItems.isEmpty()) actionItems.add(ActionItem.EMPTY);
                break;
            case -1:
                getCurrent().removeActionText();
                break;
            case 1:
                getCurrent().addActionText(ch);
                break;
        }

        fireEvent(getCurrent().getActionText());
    }
}
