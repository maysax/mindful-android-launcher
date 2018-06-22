package co.siempo.phone.event;

/**
 * Created by rajeshjadi on 19/8/17.
 */

public class SendSmsDetect {
    private int strNumber = -1;

    public SendSmsDetect(int strNumber) {
        this.strNumber = strNumber;
    }

    public int getStrNumber() {
        return strNumber;
    }

    public void setStrNumber(int strNumber) {
        this.strNumber = strNumber;
    }
}
