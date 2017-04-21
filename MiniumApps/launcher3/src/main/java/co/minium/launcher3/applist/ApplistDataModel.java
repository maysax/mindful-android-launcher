package co.minium.launcher3.applist;

import android.graphics.drawable.Drawable;

/**
 * Created by tkb on 2017-04-21.
 */

public class ApplistDataModel {
    private String name;
    private String packageName;
    private Drawable icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
