package co.siempo.phone.event;

/**
 * Created by Shahab on 2/16/2017.
 */

public class SearchLayoutEvent {

    private String str;

    public SearchLayoutEvent(String s) {
        this.str = s;
    }

    public String getString() {
        return str;
    }
}
