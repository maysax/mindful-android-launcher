package co.minium.launcher3.notification.remove_notification_strategy;

import co.minium.launcher3.notification.Notification;

/**
 * Created by tkb on 2017-04-03.
 */

public class DeleteIteam {
    private DeleteStrategy deleteStrategy;
    public DeleteIteam(DeleteStrategy deleteStrategy){
        this.deleteStrategy = deleteStrategy;
    }

    public void executeDelete(Notification notification){
        deleteStrategy.delete(notification);
    }
}
