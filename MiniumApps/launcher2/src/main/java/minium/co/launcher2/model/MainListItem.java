package minium.co.launcher2.model;

/**
 * Created by Shahab on 6/24/2016.
 */
public class MainListItem {

    private ActionListItem actionListItem;
    private ContactListItem contactListItem;
    private OptionsListItem optionsListItem;
    private boolean isEnabled = true;

    public boolean isEnabled() {
        return isEnabled;
    }



    public enum ItemType {
        ACTION_LIST_ITEM, CONTACT_ITEM, OPTION_ITEM
    }

    private ItemType type;

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public MainListItem(ActionListItem actionListItem) {
        this.actionListItem = actionListItem;
        this.type = ItemType.ACTION_LIST_ITEM;

    }

    public MainListItem(ContactListItem contactListItem) {
        this.contactListItem = contactListItem;
        this.type = ItemType.CONTACT_ITEM;
    }

    public MainListItem(OptionsListItem optionsListItem) {
        this.optionsListItem = optionsListItem;
        this.type = ItemType.OPTION_ITEM;
    }

    public ItemType getType() {
        return type;
    }

    public ActionListItem getActionListItem() {
        return actionListItem;
    }

    public ContactListItem getContactListItem() {
        return contactListItem;
    }

    public OptionsListItem getOptionsListItem() {
        return optionsListItem;
    }
}
