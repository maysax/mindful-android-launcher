package co.siempo.phone.service;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.NotifyBottomView;
import co.siempo.phone.event.NotifyToolView;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;
import de.greenrobot.event.EventBus;

/**
 * Created by rajeshjadi on 14/3/18.
 */

public class LoadToolPane extends AsyncTask<String, String, ArrayList<MainListItem>> {

    Context context;
    ArrayList<MainListItem> bottomDockList;

    public LoadToolPane(Context context) {
        this.context = context;
        bottomDockList = new ArrayList<>();
    }

    @Override
    protected ArrayList<MainListItem> doInBackground(String... strings) {

        ArrayList<MainListItem> items = new ArrayList<>();
        ArrayList<MainListItem> items1 = new ArrayList<>(12);

        try {
            new MainListItemLoader(context).loadItemsDefaultApp(items);
            items = PackageUtil.getToolsMenuData(context, items);
            Set<Integer> list = new HashSet<>();

            if (null != CoreApplication.getInstance() && null != CoreApplication
                    .getInstance().getToolsSettings()) {
                for (Map.Entry<Integer, AppMenu> entry : CoreApplication.getInstance().getToolsSettings().entrySet()) {
                    if (entry.getValue().isBottomDoc()) {
                        list.add(entry.getKey());
                    }
                }
            }

            for (MainListItem mainListItem : items) {
                if (list.contains(mainListItem.getId())) {
                    bottomDockList.add(mainListItem);
                } else {
                    if (items1.size() < 12) {
                        items1.add(mainListItem);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return items1;
    }

    @Override
    protected void onPostExecute(ArrayList<MainListItem> s) {
        super.onPostExecute(s);

        if (TextUtils.isEmpty(PrefSiempo.getInstance(context).read(PrefSiempo
                        .SORTED_MENU,
                ""))) {
            ArrayList<Long> sortedId = new ArrayList<>();
            for (MainListItem mainListItem : s) {
                sortedId.add((long) mainListItem.getId());
            }
            for (MainListItem mainListItem : bottomDockList) {
                sortedId.add((long) mainListItem.getId());
            }

            Gson gson = new Gson();
            String jsonListOfSortedCustomerIds = gson.toJson(sortedId);
            PrefSiempo.getInstance(context).write(PrefSiempo.SORTED_MENU,
                    jsonListOfSortedCustomerIds);


        }
        CoreApplication.getInstance().setToolItemsList(s);
        CoreApplication.getInstance().setToolBottomItemsList(bottomDockList);
        EventBus.getDefault().postSticky(new NotifyBottomView(true));
        EventBus.getDefault().postSticky(new NotifyToolView(true));

    }
}
