package minium.co.launcher2.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.IOException;

import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 8/3/2016.
 */
public class AudioUtils {

    public void playNotificationSound(Context context) {
        Tracer.i("Playing sound...");
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, defaultRingtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    mp.release();
                }
            });
            mediaPlayer.start();
            Tracer.i("Playing sound completed");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Tracer.e(e, e.getMessage());
        } catch (SecurityException e) {
            e.printStackTrace();
            Tracer.e(e, e.getMessage());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Tracer.e(e, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Tracer.e(e, e.getMessage());
        }
    }
}
