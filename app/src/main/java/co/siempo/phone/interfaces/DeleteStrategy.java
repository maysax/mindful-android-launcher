package co.siempo.phone.interfaces;

import co.siempo.phone.models.Notification;

/**
 * Created by tkb on 2017-04-03.
 */


public interface DeleteStrategy {
    void delete(Notification notification);

    void deleteAll();
}
