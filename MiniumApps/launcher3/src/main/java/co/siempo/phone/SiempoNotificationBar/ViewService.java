package co.siempo.phone.SiempoNotificationBar;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.annotations.SystemService;

/**
 * A service is use to creating the view and hide/show it.
 *
 */

@EIntentService
public class ViewService extends IntentService {

    private static final int SAFETY_MARGIN = 20;

    @SystemService
    protected WindowManager windowManager;

    @Bean
    protected ViewHolder holder;


    SharedPreferences preferences;
    public ViewService() {
        super(ViewService.class.getSimpleName());
    }

    @AfterInject
    public void init() {
        if (holder.getCurrentOverlay() == null ) {
            View overlayView;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                overlayView = new OreoOverlay(getApplicationContext());
                windowManager.addView(overlayView, OreoOverlay.createLayoutParams(retrieveStatusBarHeight() + SAFETY_MARGIN));
            }
            else{
                overlayView = new OverlayView(getApplicationContext());
                windowManager.addView(overlayView, OverlayView.createLayoutParams(retrieveStatusBarHeight() + SAFETY_MARGIN));
            }
            holder.setCurrentOverlay(overlayView);
        }
    }

    public int retrieveStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @ServiceAction
    protected void showMask() {
        holder.showView();
    }


    @ServiceAction
    protected void hideMask() {
        holder.hideView();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do nothing here
    }

}