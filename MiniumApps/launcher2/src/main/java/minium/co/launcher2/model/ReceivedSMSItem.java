package minium.co.launcher2.model;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by Shahab on 8/3/2016.
 */
public class ReceivedSMSItem extends SugarRecord {

    String number;

    Date date;

    String body;

    int hasDisplayed;

    public ReceivedSMSItem() {

    }

    public ReceivedSMSItem(String number, Date date, String body, int hasDisplayed) {
        this.number = number;
        this.date = date;
        this.body = body;
        this.hasDisplayed = hasDisplayed;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getHasDisplayed() {
        return hasDisplayed;
    }

    public ReceivedSMSItem setDisplayed(int hasDisplayed) {
        this.hasDisplayed = hasDisplayed;
        return this;
    }
}
