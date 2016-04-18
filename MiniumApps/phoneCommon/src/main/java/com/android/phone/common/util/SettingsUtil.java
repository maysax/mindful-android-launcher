/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.phone.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;

import com.android.phone.common.R;

import java.lang.CharSequence;
import java.lang.String;

public class SettingsUtil {
    /**
     * Obtain the setting for "vibrate when ringing" setting.
     *
     * Watch out: if the setting is missing in the device, this will try obtaining the old
     * "vibrate on ring" setting from AudioManager, and save the previous setting to the new one.
     */
    public static boolean getVibrateWhenRingingSetting(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null || !vibrator.hasVibrator()) {
            return false;
        }
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.VIBRATE_WHEN_RINGING, 0) != 0;
    }

    /**
     * Queries for a ringtone name, and sets the name using a handler.
     * This is a method was originally copied from com.android.settings.SoundSettings.
     *
     * @param context The application context.
     * @param handler The handler, which takes the name of the ringtone as a String as a parameter.
     * @param type The type of sound.
     * @param preference The preference being updated.
     * @param msg An integer identifying the message sent to the handler.
     */
    public static void updateRingtoneName(
            Context context, Handler handler, int type, Preference preference, int msg) {
        if (preference == null) {
            return;
        }

        final Uri ringtoneUri;
        boolean defaultRingtone = false;
        if (type == RingtoneManager.TYPE_RINGTONE) {
            // For ringtones, we can just lookup the system default because changing the settings
            // in Call Settings changes the system default.
            ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, type);
        } else {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            // For voicemail notifications, we use the value saved in Phone's shared preferences.
            String uriString = prefs.getString(preference.getKey(), null);
            if (TextUtils.isEmpty(uriString)) {
                // silent ringtone
                ringtoneUri = null;
            } else {
                if (uriString.equals(Settings.System.DEFAULT_NOTIFICATION_URI.toString())) {
                    // If it turns out that the voicemail notification is set to the system
                    // default notification, we retrieve the actual URI to prevent it from showing
                    // up as "Unknown Ringtone".
                    defaultRingtone = true;
                    ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, type);
                } else {
                    ringtoneUri = Uri.parse(uriString);
                }
            }
        }
        CharSequence summary = context.getString(R.string.ringtone_unknown);
        // Is it a silent ringtone?
        if (ringtoneUri == null) {
            summary = context.getString(R.string.ringtone_silent);
        } else {
            // Fetch the ringtone title from the media provider
            try {
                Cursor cursor = context.getContentResolver().query(ringtoneUri,
                        new String[] { MediaStore.Audio.Media.TITLE }, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        summary = cursor.getString(0);
                    }
                    cursor.close();
                }
            } catch (SQLiteException sqle) {
                // Unknown title for the ringtone
            }
        }
        if (defaultRingtone) {
            summary = context.getString(R.string.default_notification_description, summary);
        }
        handler.sendMessage(handler.obtainMessage(msg, summary));
    }
}
