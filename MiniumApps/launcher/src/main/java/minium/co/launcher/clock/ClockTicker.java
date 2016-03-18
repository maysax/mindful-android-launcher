package minium.co.launcher.clock;

import android.os.Handler;
import android.os.SystemClock;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

/**
 * Created by shahab on 3/15/16.
 */
public class ClockTicker {
    Calendar mCalendar;

    private Runnable mTicker;
    private Handler mHandler;

    private boolean mTickerStopped = false;

    public ClockTicker() {
        initClock();
    }

    private void initClock() {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
    }

    public void start() {
        mTickerStopped = false;
        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped) return;
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                EventBus.getDefault().post(new ClockTickerEvent(mCalendar));
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }

    public void stop() {
        mTickerStopped = true;
    }
}
