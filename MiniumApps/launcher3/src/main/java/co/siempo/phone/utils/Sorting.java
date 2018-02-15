package co.siempo.phone.utils;

import android.content.Context;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import co.siempo.phone.models.MainListItem;

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
        Collections.sort(list, new Comparator<ResolveInfo>() {
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
}
