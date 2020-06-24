package co.siempo.phone.preferences;

import co.siempo.phone.utils.PrefSiempo;

public final class Preferences {
    public static final String KEY_DARK_THEME = PrefSiempo.IS_DARK_THEME;
    public static final String KEY_INTENTIONS_DISABLED = PrefSiempo.IS_INTENTION_ENABLE;
    public static final String KEY_CUSTOM_BACKGROUND_ENABLED = PrefSiempo.DEFAULT_BAG_ENABLE;
    public static final String KEY_CUSTOM_BACKGROUND = PrefSiempo.DEFAULT_BAG;
    public static final String KEY_STATUS_BAR_HIDDEN = PrefSiempo.DEFAULT_NOTIFICATION_ENABLE;
    public static final String KEY_SCREEN_OVERLAY_ENABLED = PrefSiempo.DEFAULT_SCREEN_OVERLAY;
    public static final String KEY_HIDE_ICON_BRANDING = PrefSiempo.IS_ICON_BRANDING;
    public static final String KEY_RANDOMIZE_JUNKFOOD = PrefSiempo.IS_RANDOMIZE_JUNKFOOD;
    // Use a different key than PrefSiempo, since this implementation uses a String, PrefSiempo uses an int to store the values
    public static final String KEY_DETER_FROM_JUNKFOOD_AFTER = "deter_after_minutes";
    public static final String KEY_SLEEP_MODE_ENABLED = PrefSiempo.IS_SLEEP_ENABLE;
    public static final String KEY_DND_ENABLED = PrefSiempo.IS_DND_ENABLE;
    public static final String KEY_ANALYTICS_ENABLED = PrefSiempo.IS_FIREBASE_ANALYTICS_ENABLE;
    public static final String KEY_EMAIL_ADDRESS = PrefSiempo.USER_EMAILID;

    private Preferences() {
        // Private constructor to enforce singleton
    }
}
