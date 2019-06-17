package co.siempo.phone.interfaces;

import java.util.ArrayList;

import co.siempo.phone.models.MainListItem;

/**
 * Created by rajeshjadi on 18/10/17.
 */

public interface OnToolItemListChangedListener {

    void onToolItemListChanged(ArrayList<MainListItem> customers,int position);
}
