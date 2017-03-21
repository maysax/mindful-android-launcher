package co.minium.launcher3.mm.model;

import co.minium.launcher3.app.Launcher3App;
import minium.co.core.app.CoreApplication;

/**
 * Created by tkb on 2017-03-16.
 */

public class DBUtility {
    private DBUtility(){

    }
    private static ActivitiesStorageDao daoSession= null;
    public static ActivitiesStorageDao GetActivitySession(){
        if (daoSession==null){
            daoSession = ((Launcher3App) CoreApplication.getInstance()).getDaoSession().getActivitiesStorageDao();
            return daoSession;
        }else {
            return daoSession;
        }
    }
}
