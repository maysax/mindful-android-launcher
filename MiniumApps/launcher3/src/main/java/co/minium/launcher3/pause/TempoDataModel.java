package co.minium.launcher3.pause;

import java.util.ArrayList;

/**
 * Created by tkb on 2017-04-05.
 */

public class TempoDataModel {
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

    public ArrayList<TempoDataModel> getDefaultTempoDataModel(){
        String[] subjects =
                {"Allow favorites","Allow calls"};
        ArrayList<TempoDataModel>tempoList = new ArrayList<>();
        for (int i= 0 ; i<subjects.length; i++){
            TempoDataModel tempoDataModel = new TempoDataModel();
            tempoDataModel.setName(subjects[i]);
            tempoDataModel.setStatus(false);
            tempoList.add(tempoDataModel);
        }
        return tempoList;
    }
}
