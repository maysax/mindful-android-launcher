package co.siempo.phone.service;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.util.ArrayList;

import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.NotifyFavortieView;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;
import de.greenrobot.event.EventBus;

/**
 * Created by rajeshjadi on 14/3/18.
 */

public class LoadFavoritePane extends AsyncTask<String, String, ArrayList<MainListItem>> {

    Context context;

    public LoadFavoritePane(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList<MainListItem> doInBackground(String... strings) {
        ArrayList<MainListItem> items;
        /**
         * Changes for SSA-1770 for checking whether Favourite Item list is empty or not.
         */
        items = PackageUtil.getFavoriteList(context, false);

        int itemsSize = items.size();
        int tempFavSize = 0;
        for (MainListItem favListItems : items) {
            if (TextUtils.isEmpty(favListItems.getPackageName())) {
                tempFavSize++;
            }
        }
        if (itemsSize == tempFavSize) {
            PrefSiempo.getInstance(context).write(PrefSiempo.FAVORITE_SORTED_MENU, "");
            items = PackageUtil.getFavoriteList(context, true);
        } else {
            items = PackageUtil.getFavoriteList(context, false);
        }
        return items;
    }

    @Override
    protected void onPostExecute(ArrayList<MainListItem> s) {
        super.onPostExecute(s);
        CoreApplication.getInstance().setFavoriteItemsList(s);
        EventBus.getDefault().postSticky(new NotifyFavortieView(true));
    }
}
