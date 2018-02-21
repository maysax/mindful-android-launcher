package co.siempo.phone.interfaces;

import java.util.ArrayList;

import co.siempo.phone.models.MainListItem;

/**
 * Created by rajeshjadi on 18/10/17.
 */

public interface OnFavoriteItemListChangedListener {

    void onFavoriteItemListChanged(ArrayList<MainListItem> customers);
}
