package co.siempo.phone.tempo;

/**
 * Created by Shahab on 2/27/2017.
 */

public class TempoPreferenceEvent {

    private TempoDataModel model;

    public TempoPreferenceEvent(TempoDataModel tempoDataModel) {
        this.model = tempoDataModel;
    }

    public TempoDataModel getModel() {
        return model;
    }
}
