package minium.co.launcher2.events;

/**
 * Created by Shahab on 5/2/2016.
 */
public class LoadFragmentEvent {

    public static final int CONTACTS_LIST = 1;
    public static final int MAIN_FRAGMENT = 2;

    private int id;

    public LoadFragmentEvent(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
