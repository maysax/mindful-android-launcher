package co.siempo.phone.tempo;

import java.util.ArrayList;

/**
 * Created by tkb on 2017-04-05.
 */

public class TempoDataModel {
    private int id;
    private String name;
    private boolean status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<TempoDataModel> getDefaultTempoDataModel() {
        String[] subjects =
                {"Allow favorites", "Allow calls"};
        ArrayList<TempoDataModel> tempoList = new ArrayList<>();
        for (int i = 0; i < subjects.length; i++) {
            TempoDataModel tempoDataModel = new TempoDataModel();
            tempoDataModel.setId(i);
            tempoDataModel.setName(subjects[i]);
            tempoDataModel.setStatus(false);
            tempoList.add(tempoDataModel);
        }
        return tempoList;
    }

    public ArrayList<TempoDataModel> getTempoDataModel(boolean b, boolean b1) {
        String[] subjects =
                {"Allow favorites", "Allow calls"};
        boolean[] bools = {b, b1};
        ArrayList<TempoDataModel> tempoList = new ArrayList<>();
        for (int i = 0; i < subjects.length; i++) {
            TempoDataModel tempoDataModel = new TempoDataModel();
            tempoDataModel.setId(i);
            tempoDataModel.setName(subjects[i]);
            tempoDataModel.setStatus(bools[i]);
            tempoList.add(tempoDataModel);
        }
        return tempoList;
    }
}
