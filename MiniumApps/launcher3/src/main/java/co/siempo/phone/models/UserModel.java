package co.siempo.phone.models;

/**
 * Created by rajeshjadi on 7/3/18.
 */

public class UserModel {
    private String userId;
    private String emailId;

    public UserModel() {
    }

    public UserModel(String userId, String emailId) {
        this.userId = userId;
        this.emailId = emailId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}
