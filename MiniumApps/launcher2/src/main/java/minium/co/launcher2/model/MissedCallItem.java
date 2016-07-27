package minium.co.launcher2.model;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by Shahab on 7/27/2016.
 */
public class MissedCallItem extends SugarRecord {

    String number;

    Date date;

    int hasDisplayed;

    public MissedCallItem() {
    }

    public MissedCallItem(String number, Date date, int hasDisplayed) {
        this.number = number;
        this.date = date;
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

    public int hasDisplayed() {
        return hasDisplayed;
    }

    public MissedCallItem setDisplayed(int hasDisplayed) {
        this.hasDisplayed = hasDisplayed;
        return this;
    }

    @Override
    public String toString() {
        return "MissedCallItem{" +
                "id=" + getId() +
                ", number=" + number +
                ", date=" + date +
                ", hasDisplayed=" + hasDisplayed +
                '}';
    }
}
