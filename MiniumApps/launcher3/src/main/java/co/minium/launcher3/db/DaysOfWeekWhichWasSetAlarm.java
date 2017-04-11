package co.minium.launcher3.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by tkb on 2017-03-24.
 */
@Entity
public class DaysOfWeekWhichWasSetAlarm {
    @Id
    Long id;
    String day;
    Integer dayValue;
    boolean isChecked;
    @Generated(hash = 1981945478)
    public DaysOfWeekWhichWasSetAlarm(Long id, String day, Integer dayValue,
            boolean isChecked) {
        this.id = id;
        this.day = day;
        this.dayValue = dayValue;
        this.isChecked = isChecked;
    }
    @Generated(hash = 1803996558)
    public DaysOfWeekWhichWasSetAlarm() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDay() {
        return this.day;
    }
    public void setDay(String day) {
        this.day = day;
    }
    public Integer getDayValue() {
        return this.dayValue;
    }
    public void setDayValue(Integer dayValue) {
        this.dayValue = dayValue;
    }
    public boolean getIsChecked() {
        return this.isChecked;
    }
    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

   

}
