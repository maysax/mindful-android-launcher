package co.siempo.phone.applist;

/**
 * Created by hardik on 23/11/17.
 * Model class for sections in notification manager
 * Different categories of app will be under different section
 */

public class HeaderAppList {
    /**
     * Name of the section
     */
    public String name;
    /**
     * Boolean to know whether notification is allowed/blocked for
     * the section
     * - True: notifications allowed
     * - False: notifications blocked
     */
    public boolean ischecked;
}
