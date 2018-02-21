package co.siempo.phone.utils;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import co.siempo.phone.models.MainListItem;

import static java.util.Collections.sort;

/**
 * Created by rajeshjadi on 9/2/18.
 */

public class Sorting {
    /**
     * Sort the application alphabetically.
     *
     * @param context
     * @param list
     * @return
     */
    public static ArrayList<ResolveInfo> sortAppAssignment(final Context context, ArrayList<ResolveInfo> list) {
        sort(list, new Comparator<ResolveInfo>() {
            @Override
            public int compare(final ResolveInfo object1, final ResolveInfo object2) {
                if (object1 != null && object2 != null) {
                    return object1.loadLabel(context.getPackageManager()).toString().compareTo(object2.loadLabel(context.getPackageManager()).toString());
                } else {
                    return 1;
                }
            }
        });
        return list;
    }

    public static ArrayList<MainListItem> sortToolAppAssignment(final Context context, ArrayList<MainListItem> list) {
        Collections.sort(list, new Comparator<MainListItem>() {
            @Override
            public int compare(final MainListItem object1, final MainListItem object2) {
                return object1.getTitle().compareTo(object2.getTitle());
            }
        });
        return list;
    }


    public static List<MainListItem> sortAppList(final Context context, List<MainListItem> list) {
        sort(list, new Comparator<MainListItem>() {
            @Override
            public int compare(final MainListItem object1, final MainListItem object2) {
                if (!TextUtils.isEmpty(object1.getTitle()) && !TextUtils.isEmpty(object1.getTitle())) {
                    if (object1.getId() == -1) {
                        return object1.getTitle().toString().compareTo(object2.getTitle().toString());
                    } else {
                        return 0;
                    }
                } else {
                    return 0;
                }
            }
        });
        return list;
    }

    public static List<MainListItem> sortList(List<MainListItem> items) {

        sort(items, new Comparator<MainListItem>() {
            public int compare(MainListItem o1, MainListItem o2) {
                Date o1Date = o1.getDate();
                Date o2Date = o2.getDate();
                if (o1.getDate() == null) {
                    Calendar cal = GregorianCalendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.DAY_OF_YEAR, -50);
                    o1Date = cal.getTime();
                }
                if (o2.getDate() == null) {
                    Calendar cal = GregorianCalendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.DAY_OF_YEAR, -50);
                    o2Date = cal.getTime();
                }
                if (o1Date != null && o2Date != null) {
                    if (o1Date.after(o2Date)) {
                        return -1;
                    }
                    else if (o1Date.equals(o2Date)) {
                        return 0;
                    }
                    else {
                        return 1;
                    }
                } else {
                    return 1;
                }
            }
        });
        return items;
    }
}
