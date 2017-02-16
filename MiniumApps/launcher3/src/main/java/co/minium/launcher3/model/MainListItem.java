package co.minium.launcher3.model;

/**
 * Created by Shahab on 2/16/2017.
 */

public class MainListItem {

    private int id;
    private String title;
    private String subTitle;
    private boolean isEnabled = true;
    private MainListItemType itemType = MainListItemType.ACTION;
    private String icon;

    public MainListItem(int id, String title, String icon) {
        this(id, title, icon, MainListItemType.ACTION);
    }

    public MainListItem(int id, String title, String icon, MainListItemType itemType) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.itemType = itemType;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public MainListItemType getItemType() {
        return itemType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "MainListItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", isEnabled=" + isEnabled +
                ", itemType=" + itemType +
                ", icon='" + icon + '\'' +
                '}';
    }
}
