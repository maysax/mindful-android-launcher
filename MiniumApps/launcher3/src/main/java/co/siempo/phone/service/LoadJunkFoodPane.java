package co.siempo.phone.service;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.NotifyJunkFoodView;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.Sorting;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.EventBus;

/**
 * Created by rajeshjadi on 14/3/18.
 */

public class LoadJunkFoodPane extends AsyncTask<String, String, ArrayList<String>> {

    private PrefSiempo prefSiempo;

    public LoadJunkFoodPane(PrefSiempo prefSiempo) {
        this.prefSiempo = prefSiempo;
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        Set<String> junkFoodList = prefSiempo.read(PrefSiempo.JUNKFOOD_APPS, new HashSet<String>());
        ArrayList<String> items = new ArrayList<>();
        ArrayList<String> itemsToRemove = new ArrayList<>();
        for (String junkApp : junkFoodList) {
            if (UIUtils.isAppInstalledAndEnabled(junkApp)) {
                items.add(junkApp);
            } else {
                itemsToRemove.add(junkApp);
            }
        }

        junkFoodList.removeAll(itemsToRemove);
        prefSiempo.write(PrefSiempo.JUNKFOOD_APPS, junkFoodList);

        if (junkFoodList.size() > 0) {
            if (prefSiempo.read(PrefSiempo.IS_RANDOMIZE_JUNKFOOD, true)) {
                Collections.shuffle(items);
            } else {
                items = Sorting.sortJunkAppAssignment(items);
            }
        }
        return items;
    }

    @Override
    protected void onPostExecute(ArrayList<String> s) {
        super.onPostExecute(s);
        CoreApplication.getInstance().setJunkFoodList(s);
        EventBus.getDefault().postSticky(new NotifyJunkFoodView(true));
    }
}
