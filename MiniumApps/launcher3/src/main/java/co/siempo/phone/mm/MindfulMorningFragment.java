package co.siempo.phone.mm;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.util.VibrationUtils;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

/**
 * Created by tkb on 2017-03-13.
 */

@EFragment(R.layout.mm_layout)
public class MindfulMorningFragment extends CoreFragment {
    MediaPlayer mMediaPlayer;
    @ViewById
    ImageView crossActionBar;
    @ViewById
    Button pause_button;
    @Bean
    VibrationUtils vibrationUtils;
    @ViewById
    TextView txtAlarmTime, txtAmPm;
    @Pref
    Launcher3Prefs_ launcherPrefs;

    @Click
    void pause_button() {

        if (mMediaPlayer != null)
            mMediaPlayer.stop();

        vibrationUtils.cancel();
        //((CoreActivity) getActivity()).loadChildFragment(new MindfulMorningList_(), R.id.mainView);
        ((CoreActivity) getActivity()).loadFragment(new MindfulMorningList_(), R.id.mainView, "Main");

    }

    @Click
    void crossActionBar() {
        getActivity().onBackPressed();
    }

    @AfterViews
    public void afterViews() {

        String SavedTime = launcherPrefs.time().get();
        String[] timeArray = SavedTime.split(":");
        String totalTime = "";

        totalTime = timeArray[0] + ":" + timeArray[1];
        txtAlarmTime.setText(totalTime);

        if (timeArray[2].equals("0")) {
            txtAmPm.setText(" AM");
        } else {
            txtAmPm.setText(" PM");
        }

        // Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds

        //v.vibrate(500);

        // ringtone = RingtoneManager.getRingtone(getActivity(), notification);
        //ringtone.play();
        vibrationUtils.callVibration();

        try {
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(getActivity(), alert);
            final AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (Exception e) {
        }
    }


}
