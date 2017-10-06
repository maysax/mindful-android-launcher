package co.siempo.phone.SiempoNotificationBar;

import android.view.View;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;


/**
 * A singleton class that is use to hold a reference to overlay.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ViewHolder {

    protected View currentOverlay;

    protected boolean shown;

    @UiThread
    protected void showView() {
        currentOverlay.setVisibility(View.VISIBLE);
        shown = true;
    }

    @UiThread
    protected void hideView() {
       if (currentOverlay!=null)currentOverlay.setVisibility(View.GONE);
        shown = false;
    }

    public void setCurrentOverlay(View currentOverlay) {
        this.currentOverlay = currentOverlay;
    }

    public View getCurrentOverlay() {
        return currentOverlay;
    }

    public boolean isShown() {
        return shown;
    }

}
