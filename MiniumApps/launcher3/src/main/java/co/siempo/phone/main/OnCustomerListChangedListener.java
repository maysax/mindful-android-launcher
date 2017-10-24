package co.siempo.phone.main;

import java.util.List;

import co.siempo.phone.model.MainListItem;

/**
 * Created by rajeshjadi on 18/10/17.
 */

public interface OnCustomerListChangedListener {

    void onNoteListChanged(List<MainListItem> customers);
}
