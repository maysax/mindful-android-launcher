package co.siempo.phone.util;

import android.os.Vibrator;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 11/4/2016.
 */

@SuppressWarnings("ALL")
@EBean
public class VibrationUtils {

    @SystemService
    Vibrator vibrator;

    public void callVibration() {
        Tracer.d("simulateCallVibration called");
        // 1. Vibrate for 1000 milliseconds
        long milliseconds = 1000;
        vibrator.vibrate(milliseconds);

        // 2. Vibrate in a Pattern with 500ms on, 300ms off for 5 times
        // Start without a delay
        // Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] pattern = {0, 2000, 3000, 2000, 3000, 2000, 3000, 2000, 3000, 2000, 3000, 2000, 3000};

        // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
        vibrator.vibrate(pattern, -1);
    }

    public void vibrate() {
        long milliseconds = 1000;
        vibrator.vibrate(milliseconds);
    }

    public void cancel() {
        vibrator.cancel();
    }
}
