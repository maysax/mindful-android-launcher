package co.minium.launcher3.pause;

/**
 * Created by Shahab on 2/27/2017.
 */

public class PausePreferenceEvent {

    private PauseDataModel model;

    public PausePreferenceEvent(PauseDataModel pauseDataModel) {
        this.model = pauseDataModel;
    }

    public PauseDataModel getModel() {
        return model;
    }
}
