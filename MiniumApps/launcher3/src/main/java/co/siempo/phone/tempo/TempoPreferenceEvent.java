package co.siempo.phone.tempo;

/**
 * Created by Shahab on 2/27/2017.
 */

@SuppressWarnings("ALL")
public class TempoPreferenceEvent {

    private TempoDataModel model;

    public TempoPreferenceEvent(TempoDataModel tempoDataModel) {
        this.model = tempoDataModel;
    }

    public TempoDataModel getModel() {
        return model;
    }
}
