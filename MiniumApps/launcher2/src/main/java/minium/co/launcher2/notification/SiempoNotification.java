package minium.co.launcher2.notification;

import minium.co.launcher2.model.MissedCallItem;
import minium.co.launcher2.model.ReceivedSMSItem;

/**
 * Created by Shahab on 10/11/2016.
 */

public class SiempoNotification {

    private MissedCallItem missedCallItem;

    private ReceivedSMSItem smsItem;

    private boolean isMissedCallItem;

    private boolean isSmsItem;

    public SiempoNotification(MissedCallItem missedCallItem) {
        this.missedCallItem = missedCallItem;
        this.smsItem = null;
        this.isMissedCallItem = true;
        this.isSmsItem = false;
    }

    public SiempoNotification(ReceivedSMSItem smsItem) {
        this.smsItem = smsItem;
        this.missedCallItem = null;
        this.isSmsItem = true;
        this.isMissedCallItem = false;
    }

    public MissedCallItem getMissedCallItem() {
        return missedCallItem;
    }

    public ReceivedSMSItem getSmsItem() {
        return smsItem;
    }

    public boolean isMissedCallItem() {
        return isMissedCallItem;
    }

    public boolean isSmsItem() {
        return isSmsItem;
    }

    @Override
    public String toString() {
        return "SiempoNotification{" +
                "missedCallItem=" + missedCallItem +
                ", smsItem=" + smsItem +
                ", isMissedCallItem=" + isMissedCallItem +
                ", isSmsItem=" + isSmsItem +
                '}';
    }
}
