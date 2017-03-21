package co.minium.launcher3.call;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import java.util.Date;

/**
 * Created by Shahab on 3/21/2017.
 */

@Entity
public class CallStorage {
    @Id
    private Long id;
    private String title;
    private java.util.Date _date;
    private Integer contactId;
    private Long snoozeTime;
    private Boolean isRead;
    @Generated(hash = 126881849)
    public CallStorage(Long id, String title, java.util.Date _date,
            Integer contactId, Long snoozeTime, Boolean isRead) {
        this.id = id;
        this.title = title;
        this._date = _date;
        this.contactId = contactId;
        this.snoozeTime = snoozeTime;
        this.isRead = isRead;
    }
    @Generated(hash = 189930293)
    public CallStorage() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public java.util.Date get_date() {
        return this._date;
    }
    public void set_date(java.util.Date _date) {
        this._date = _date;
    }
    public Integer getContactId() {
        return this.contactId;
    }
    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }
    public Long getSnoozeTime() {
        return this.snoozeTime;
    }
    public void setSnoozeTime(Long snoozeTime) {
        this.snoozeTime = snoozeTime;
    }
    public Boolean getIsRead() {
        return this.isRead;
    }
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
