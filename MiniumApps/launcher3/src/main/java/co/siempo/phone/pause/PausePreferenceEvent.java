package co.siempo.phone.pause;

/**
 * Created by Shahab on 2/27/2017.
 */

@SuppressWarnings("ALL")
public class PausePreferenceEvent {

    private PauseDataModel model;

    public PausePreferenceEvent(PauseDataModel pauseDataModel) {
        this.model = pauseDataModel;
    }

    public PauseDataModel getModel() {
        return model;
    }
}
