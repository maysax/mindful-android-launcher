package minium.co.launcher2.model;

/**
 * Created by Shahab on 6/10/2016.
 */
public class OptionsListItem {

    String iconName;
    String text;

    public OptionsListItem(String iconName, String text) {
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
