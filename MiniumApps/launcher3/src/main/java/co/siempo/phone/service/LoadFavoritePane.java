package co.siempo.phone.service;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.NotifyFavortieView;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PackageUtil;
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
        items = PackageUtil.getFavoriteList(context);
        return items;
    }

    @Override
    protected void onPostExecute(ArrayList<MainListItem> s) {
        super.onPostExecute(s);
        CoreApplication.getInstance().setFavoriteItemsList(s);
        EventBus.getDefault().postSticky(new NotifyFavortieView(true));
    }
}
