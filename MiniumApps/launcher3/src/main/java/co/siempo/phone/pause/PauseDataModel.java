package co.siempo.phone.pause;

import java.util.ArrayList;

/**
 * Created by tkb on 2017-04-05.
 */

@SuppressWarnings("ALL")
public class PauseDataModel {
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

    public ArrayList<PauseDataModel> getDefaultPauseDataModel() {
        String[] subjects =
                {"Allow favorites", "Allow calls"};
        ArrayList<PauseDataModel> tempoList = new ArrayList<>();
        for (int i = 0; i < subjects.length; i++) {
            PauseDataModel pauseDataModel = new PauseDataModel();
            pauseDataModel.setName(subjects[i]);
            pauseDataModel.setId(i);
            pauseDataModel.setStatus(false);
            tempoList.add(pauseDataModel);
        }
        return tempoList;
    }

    public ArrayList<PauseDataModel> getPauseDataModel(boolean b, boolean b1) {
        String[] subjects =
                {"Allow favorites", "Allow calls"};
        boolean[] bools = {b, b1};
        ArrayList<PauseDataModel> tempoList = new ArrayList<>();
        for (int i = 0; i < subjects.length; i++) {
            PauseDataModel tempoDataModel = new PauseDataModel();
            tempoDataModel.setId(i);
            tempoDataModel.setName(subjects[i]);
            tempoDataModel.setStatus(bools[i]);
            tempoList.add(tempoDataModel);
        }
        return tempoList;
    }
}
