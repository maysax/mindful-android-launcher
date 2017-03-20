package co.minium.launcher3.mm.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by tkb on 2017-03-16.
 */
@Entity
public class ActivitiesStorage {
    @Id
    private Long id;
    String name;
    Integer  time;
    boolean isComplete;
    @Generated(hash = 1764214735)
    public ActivitiesStorage(Long id, String name, Integer time,
            boolean isComplete) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.isComplete = isComplete;
    }
    @Generated(hash = 137043216)
    public ActivitiesStorage() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getTime() {
        return this.time;
    }
    public void setTime(Integer time) {
        this.time = time;
    }
    public boolean getIsComplete() {
        return this.isComplete;
    }
    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

}
