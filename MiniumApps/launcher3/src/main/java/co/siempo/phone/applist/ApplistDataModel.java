package co.siempo.phone.applist;

import android.graphics.drawable.Drawable;

import co.siempo.phone.kiss.utils.UserHandle;

/**
 * Created by tkb on 2017-04-21.
 */

@SuppressWarnings("ALL")
public class ApplistDataModel {
    private String name;
    private String packageName;
    private Drawable icon;
    private UserHandle userHandle;
    private String activityName;

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

    public UserHandle getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(UserHandle userHandle) {
        this.userHandle = userHandle;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
