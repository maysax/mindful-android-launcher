package co.siempo.phone.main;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by itc on 25/02/17.
 */

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final String TAG = "GestureListener";
    private Listener mListener;

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      //  Log.i(TAG,e1.toString()+"\n"+e2.toString());
      //  Log.d(TAG,"distanceX = "+distanceX+",distanceY = "+distanceY);
        if (mListener == null)
            return true;

        if (distanceX == 0 && Math.abs(distanceY) > 1){
            mListener.onScrollVertical(distanceY);
        }

        if (distanceY == 0 && Math.abs(distanceX) > 1){
            mListener.onScrollHorizontal(distanceX);
        }
        return true;
    }


    public void setListener(Listener mListener) {
        this.mListener = mListener;
    }

    public interface Listener{
        /**
         * left scroll dx >0
         * right scroll dx <0
         * @param dx
         */
        void onScrollHorizontal(float dx);

        /**
         * upward scroll dy > 0
         * downward scroll dy < 0
         * @param dy
         */
        void onScrollVertical(float dy);
    }
}
