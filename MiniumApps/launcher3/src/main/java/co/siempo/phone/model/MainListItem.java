package co.siempo.phone.model;

import co.siempo.phone.R;

/**
 * Created by Shahab on 2/16/2017.
 */

@SuppressWarnings("ALL")
public class MainListItem {

    private int id;
    private String title;
    private String subTitle;
    private boolean isEnabled = true;
    private MainListItemType itemType = MainListItemType.ACTION;
    private String icon;
    private int iconRes;

    public MainListItem(int id, String title, String icon) {
        this(id, title, icon, MainListItemType.ACTION);
    }

    public MainListItem(int id, String title, String icon, MainListItemType itemType) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.itemType = itemType;
        this.iconRes = R.drawable.icon_sms;
    }

    public MainListItem(int id, String title, String icon, int iconRes, MainListItemType itemType) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.itemType = itemType;
        this.iconRes = iconRes;
    }

    public MainListItem(int id, String title, int iconRes, MainListItemType itemType) {
        this.id = id;
        this.title = title;
        this.itemType = itemType;
        this.iconRes = iconRes;
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

    public int getIconRes() {
        return iconRes;
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
                ", isVisible=" + isEnabled +
                ", itemType=" + itemType +
                ", icon='" + icon + '\'' +
                '}';
    }
}
