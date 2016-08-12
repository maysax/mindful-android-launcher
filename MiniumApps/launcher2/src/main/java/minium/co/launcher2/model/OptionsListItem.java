package minium.co.launcher2.model;

/**
 * Created by Shahab on 6/10/2016.
 */
public class OptionsListItem {

    int id;
    String iconName;
    String text;

    public OptionsListItem(int id, String iconName, String text) {
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
