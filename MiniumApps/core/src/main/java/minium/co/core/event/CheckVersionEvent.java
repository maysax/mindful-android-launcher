package minium.co.core.event;

/**
 * Created by Shahab on 1/5/2017.
 */

public class CheckVersionEvent {

    private int version;
    public static String ALPHA = "1";
    public static String BETA = "2";
    private String versionName;

    public CheckVersionEvent(int version) {
        this.version = version;
    }

    public CheckVersionEvent(int version,String versionName) {
        this.version = version;
        this.versionName = versionName;
    }

    public int getVersion() {
        return version;
    }


    public String getVersionName() {
        return versionName;
    }
}
