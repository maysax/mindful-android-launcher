package minium.co.launcher2.events;

/**
 * Created by Shahab on 5/2/2016.
 */
public class LoadFragmentEvent {

    public static final int CONTACTS_LIST = 1;
    public static final int MAIN_FRAGMENT = 2;
    public static final int CALL_LOG = 3;
    public static final int CONTACTS_NUMBER_LIST = 4;
    public static final int OPTIONS = 6;
    public static final int OPTIONS_2 = 7;
    public static final int CONTEXTUAL_OPTIONS = 8;
    public static final int NOTIFICATION_SCHEDULER = 9;

    private int id;

    public LoadFragmentEvent(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
