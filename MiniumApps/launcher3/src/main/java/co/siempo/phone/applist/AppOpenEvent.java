package co.siempo.phone.applist;

/**
 * Created by Shahab on 5/11/2017.
 */

@SuppressWarnings("ALL")
public class AppOpenEvent {

    private String packageName;

    public AppOpenEvent(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }
}
