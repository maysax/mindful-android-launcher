package co.siempo.phone.utils;

import android.content.Context;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
                return object1.loadLabel(context.getPackageManager()).toString().compareTo(object2.loadLabel(context.getPackageManager()).toString());
            }
        });
        return list;
    }
}
