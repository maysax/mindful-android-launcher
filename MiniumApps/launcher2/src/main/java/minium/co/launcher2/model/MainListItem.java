package minium.co.launcher2.model;

/**
 * Created by Shahab on 4/29/2016.
 */
public class MainListItem {

    String iconName;
    String text;

    public MainListItem(String iconName, String text) {
        this.iconName = iconName;
        this.text = text;
    }

    public String getIconName() {
        return iconName;
    }

    public String getText() {
        return text;
    }
}
