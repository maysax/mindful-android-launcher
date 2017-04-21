package co.minium.launcher3.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Shahab on 4/21/2017.
 */

@Entity
public class StatusBarNotificationStorage {

    @Id
    private Long id;

    private String packageName;

    private String title;

    private String content;

    private Long postTime;

    @Generated(hash = 1830063501)
    public StatusBarNotificationStorage(Long id, String packageName, String title,
            String content, Long postTime) {
        this.id = id;
        this.packageName = packageName;
        this.title = title;
        this.content = content;
        this.postTime = postTime;
    }

    @Generated(hash = 1805300731)
    public StatusBarNotificationStorage() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPostTime() {
        return this.postTime;
    }

    public void setPostTime(Long postTime) {
        this.postTime = postTime;
    }

}
