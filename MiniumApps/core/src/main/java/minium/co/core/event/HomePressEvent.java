package minium.co.core.event;

/**
 * Created by Shahab on 8/12/2016.
 */
public class HomePressEvent {

    private boolean isVisible;

    public HomePressEvent(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
