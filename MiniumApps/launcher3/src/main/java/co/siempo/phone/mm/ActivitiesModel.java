package co.siempo.phone.mm;

import java.util.ArrayList;

/**
 * Created by tkb on 2017-03-10.
 */

public class ActivitiesModel {
    private String title;
    private String timeValue;

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public String getTimeValue() {
        return timeValue;
    }

    private void setTimeValue(String timeValue) {
        this.timeValue = timeValue;
    }

    public ArrayList<ActivitiesModel> getActivityModel() {
        String[] values = {"Meditation", "Workout", "Reading", "Journaling", "Pause"};

        ArrayList<ActivitiesModel> activitiDataArray = new ArrayList<>();
        ActivitiesModel activitiesModel;

        for (int i = 0; i < values.length; i++) {

            activitiesModel = new ActivitiesModel();
            activitiesModel.setTitle(values[i]);
            activitiesModel.setTimeValue(i + "0 " + "mins");
            activitiDataArray.add(activitiesModel);
        }
        return activitiDataArray;
    }

    public ArrayList<ActivitiesModel> getActivityModel2() {
        String[] values = {"Meditation", "Workout", "Reading"};

        ArrayList<ActivitiesModel> activitiDataArray = new ArrayList<>();
        ActivitiesModel activitiesModel;

        for (int i = 0; i < values.length; i++) {

            activitiesModel = new ActivitiesModel();
            activitiesModel.setTitle(values[i]);
            activitiesModel.setTimeValue(i + "0 " + "mins");
            activitiDataArray.add(activitiesModel);
        }
        return activitiDataArray;
    }
}
