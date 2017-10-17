package co.siempo.phone.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Shahab on 5/26/2017.
 */

public interface IDynamicStatus {
    IntentFilter getIntentFilter();

    void register(Context context);

    void unregister(Context context);

    void handleIntent(Context context, Intent intent);

}
