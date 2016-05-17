package minium.co.flow.utils;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.text.TextUtils;

import java.lang.reflect.Field;

import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 5/17/2016.
 */
public class ServiceUtils {

    private static String ENABLED_NOTIFICATION_LISTENERS = null;

    private static final TextUtils.SimpleStringSplitter COLON_SPLITTER = new TextUtils.SimpleStringSplitter(':');

    /** @return True if a given {@link android.service.notification.NotificationListenerService} is enabled. */
    public static <T extends NotificationListenerService> boolean isNotificationListenerServiceRunning(Context context, Class<T> clazz) {
        return isSettingsServiceEnabled(context, getEnabledNotificationListeners(), getServiceComponentNames(clazz));
    }

    public static String[] getServiceComponentNames(Class<?> clazz) {
        return new String[]{
                clazz.getPackage().getName() + '/' + clazz.getName(),
                clazz.getPackage().getName() + "/." + clazz.getSimpleName()};
    }

    /**
     * API 4+, check if the given {@link AccessibilityService} is enabled.
     *
     * @return True if id is an enabled {@link AccessibilityService}.
     */
    public static boolean isSettingsServiceEnabled(Context context, String setting, String[] ids) {
        // Check the list of system settings to see if a service is running.
        String eServices = Settings.Secure.getString(context.getContentResolver(), setting);
        if (!TextUtils.isEmpty(eServices) && null != ids) {
            TextUtils.SimpleStringSplitter splitter = COLON_SPLITTER;
            splitter.setString(eServices);
            while (splitter.hasNext()) {
                String aService = splitter.next();
                if (contains(ids, aService)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String getEnabledNotificationListeners() {
        try {
            Field field = Settings.Secure.class.getDeclaredField("ENABLED_NOTIFICATION_LISTENERS");
            if (null != field) {
                field.setAccessible(true);
                String mbr = (String) field.get(null);
                ENABLED_NOTIFICATION_LISTENERS = mbr;
                return ENABLED_NOTIFICATION_LISTENERS;
            }
        } catch (Throwable t) {
            Tracer.e(t, "getEnabledNotificationListeners()");
        }
        ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
        return ENABLED_NOTIFICATION_LISTENERS;
    }

    /**
     * Checks that value is present as at least one of the elements of the array.
     *
     * @param array the array to check in
     * @param value the value to check for
     * @return true if the value is present in the array
     */
    public static <T> boolean contains(T[] array, T value) {
        for (T element : array) {
            if (element == null) {
                if (value == null) return true;
            } else {
                if (value != null && element.equals(value)) return true;
            }
        }
        return false;
    }

}
