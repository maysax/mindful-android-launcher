package co.siempo.phone.event;

/**
 * Created by Shahab on 8/12/2016.
 */
public class FirebaseEvent {

    private String screenName;
    private long strStartTime;

    public FirebaseEvent(String screenName, long strStartTime) {
        this.strStartTime = strStartTime;
        this.screenName = screenName;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getStrStartTime() {
        return strStartTime;
    }
}
