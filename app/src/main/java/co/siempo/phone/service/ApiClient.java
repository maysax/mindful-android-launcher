package co.siempo.phone.service;

import android.content.Context;
import androidx.annotation.NonNull;

/**
 * Created by Shahab on 1/10/2017.
 */
public class ApiClient extends CoreAPIClient {
    private Context context;

    @NonNull
    public static ApiClient getInstance(Context context) {
        return new ApiClient(context);
    }

    public ApiClient(Context context) {
        this.context = context;
    }

    public void rebind(Context context) {
        this.context = context;
    }

    @Override
    protected String getAppName() {
        return "siempo";
    }
}
