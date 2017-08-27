package com.example;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class DaoGen {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "co.minium.launcher3.db");

        Entity entity = schema.addEntity("TableNotificationSms");
        entity.addIdProperty();
        entity.addStringProperty("_contact_title");//string
        entity.addStringProperty("_message"); //string
        entity.addDateProperty("_date");//java date
        entity.addIntProperty("_contact_id");//int
        entity.addIntProperty("_sms_id"); //int
        entity.addLongProperty("_snooze_time");//long
        entity.addBooleanProperty("_is_read");//bool

        DaoGenerator DG = new DaoGenerator();
        DG.generateAll(schema, "./launcher3/src/main/java");
    }
}
