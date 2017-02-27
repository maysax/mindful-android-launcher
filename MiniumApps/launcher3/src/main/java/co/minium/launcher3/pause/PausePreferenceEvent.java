package co.minium.launcher3.pause;

/**
 * Created by Shahab on 2/27/2017.
 */

public class PausePreferenceEvent {

    private boolean isAllowFavorites;
    private boolean isAllowCalls;

    public PausePreferenceEvent() {
    }

    public PausePreferenceEvent(boolean isAllowFavorites, boolean isAllowCalls) {
        this.isAllowFavorites = isAllowFavorites;
        this.isAllowCalls = isAllowCalls;
    }

    public boolean isAllowFavorites() {
        return isAllowFavorites;
    }

    public PausePreferenceEvent setAllowFavorites(boolean allowFavorites) {
        isAllowFavorites = allowFavorites;
        return this;
    }

    public boolean isAllowCalls() {
        return isAllowCalls;
    }

    public PausePreferenceEvent setAllowCalls(boolean allowCalls) {
        isAllowCalls = allowCalls;
        return this;
    }
}
