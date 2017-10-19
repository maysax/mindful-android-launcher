package co.siempo.phone.notification.remove_notification_strategy;

import co.siempo.phone.notification.Notification;

/**
 * Created by tkb on 2017-04-03.
 */


public class DeleteItem {
    private DeleteStrategy deleteStrategy;

    public DeleteItem(DeleteStrategy deleteStrategy) {
        this.deleteStrategy = deleteStrategy;
    }

    public void executeDelete(Notification notification) {
        deleteStrategy.delete(notification);
    }

    public void deleteAll(){
        deleteStrategy.deleteAll();
    }
}
