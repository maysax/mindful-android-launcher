package co.siempo.phone.service;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
        ArrayList<MainListItem> items1 = new ArrayList<>();

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
//                    if (items1.size() < 12) {
                        items1.add(mainListItem);
//                    }
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


        try {
//            sortingMenu(s);

            CoreApplication.getInstance().setToolItemsList(s);
            CoreApplication.getInstance().setToolBottomItemsList(bottomDockList);
            EventBus.getDefault().postSticky(new NotifyBottomView(true));
            EventBus.getDefault().postSticky(new NotifyToolView(true));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    private void sortingMenu(ArrayList<MainListItem> s) {
//        if (TextUtils.isEmpty(PrefSiempo.getInstance(context).read(PrefSiempo
//                        .SORTED_MENU,
//                ""))) {
//
//            ArrayList<Long> sortedId = new ArrayList<>();
//            for (MainListItem mainListItem : s) {
//                sortedId.add((long) mainListItem.getId());
//
//            }
//            for (MainListItem mainListItem : bottomDockList) {
//                sortedId.add((long) mainListItem.getId());
//            }
//
//            Gson gson = new Gson();
//            String jsonListOfSortedCustomerIds = gson.toJson(sortedId);
//            PrefSiempo.getInstance(context).write(PrefSiempo.SORTED_MENU,
//                    jsonListOfSortedCustomerIds);
//
//
//            HashMap<Integer, AppMenu> integerAppMenuHashMap = CoreApplication
//                    .getInstance().getToolsSettings();
//
//            Iterator it = integerAppMenuHashMap.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry pair = (Map.Entry) it.next();
//                int id = (int) pair.getKey();
//                if (!sortedId.contains((long) id)) {
//                    ((AppMenu) pair.getValue()).setVisible(false);
//                }
//            }
//
//            String hashMapToolSettings = new Gson().toJson(integerAppMenuHashMap);
//            PrefSiempo.getInstance(context).write(PrefSiempo.TOOLS_SETTING,
//                    hashMapToolSettings);
//
//
//        } else {
//
//
//            String jsonListOfSortedToolsId = PrefSiempo.getInstance(context).read
//                    (PrefSiempo.SORTED_MENU, "");
//            //check for null
//            if (!jsonListOfSortedToolsId.isEmpty()) {
//
//                //loop through added ids
//
//                //convert onNoteListChangedJSON array into a List<Long>
//                Gson gson = new GsonBuilder()
//                        .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
//                List<Long> listOfSortedCustomersId = gson.fromJson(jsonListOfSortedToolsId, new TypeToken<List<Long>>() {
//                }.getType());
//
//
//                if (listOfSortedCustomersId.size() > 16) {
//
//                    List<Long> listOfToolsId = new ArrayList<>();
//                    List<Long> listOfRemoveId = new ArrayList<>();
//                    ArrayList<MainListItem> listItems = new ArrayList<>();
//                    listItems.addAll(s);
//                    listItems.addAll(bottomDockList);
//
//
//                    for (MainListItem listItem : listItems) {
//
//                        listOfToolsId.add((long) listItem.getId());
//                    }
//
//                    listOfRemoveId.addAll(listOfSortedCustomersId);
//                    listOfRemoveId.removeAll(listOfToolsId);
//
//                    listOfSortedCustomersId.removeAll(listOfRemoveId);
//
//
//                    String jsonListOfSortedCustomerIds = gson.toJson
//                            (listOfSortedCustomersId);
//                    PrefSiempo.getInstance(context).write(PrefSiempo.SORTED_MENU,
//                            jsonListOfSortedCustomerIds);
//
//
//                    HashMap<Integer, AppMenu> integerAppMenuHashMap = CoreApplication
//                            .getInstance().getToolsSettings();
//                    for (Long aLong : listOfRemoveId) {
//                        int id = aLong.intValue();
//                        integerAppMenuHashMap.get(id).setVisible
//                                (false);
//                    }
//
//
//                    String hashMapToolSettings = new Gson().toJson(integerAppMenuHashMap);
//                    PrefSiempo.getInstance(context).write(PrefSiempo.TOOLS_SETTING,
//                            hashMapToolSettings);
//
//
//
//                }
//
//
//            }
//
//
//        }
//    }


}
