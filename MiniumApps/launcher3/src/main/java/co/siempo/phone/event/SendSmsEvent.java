package co.siempo.phone.event;

/**
 * Created by rajeshjadi on 19/8/17.
 */

public class SendSmsEvent {
    private boolean isSendSms = false;
    private String strNumber;
    private String strMessage;

    public SendSmsEvent(boolean sendSms,String strNumber,String strMessage) {
        this.isSendSms = sendSms;
        this.strNumber = strNumber;
        this.strMessage = strMessage;
    }

    public boolean isSendSms() {
        return isSendSms;
    }

    public void setSendSms(boolean sendSms) {
        isSendSms = sendSms;
    }

    public String getStrNumber() {
        return strNumber;
    }

    public void setStrNumber(String strNumber) {
        this.strNumber = strNumber;
    }

    public String getStrMessage() {
        return strMessage;
    }

    public void setStrMessage(String strMessage) {
        this.strMessage = strMessage;
    }
}
