package minium.co.launcher2.model;

/**
 * Created by Shahab on 4/29/2016.
 */
public class ActionListItem {

    int id;
    String iconName;
    String text;

    public ActionListItem(int id, String iconName, String text) {
        this.id = id;
        this.iconName = iconName;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getIconName() {
        return iconName;
    }

    public String getText() {
        return text;
    }
}
