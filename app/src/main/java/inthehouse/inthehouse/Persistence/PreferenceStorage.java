package inthehouse.inthehouse.Persistence;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ryan on 2/18/2015.
 */
public class PreferenceStorage {

    private static String SHARED_PREFERENCES_NAME = "INTHEHOUSE_SHARED_PREFS";

    private static String AUTH_TOKEN_KEY = "AUTHTOKEN";
    private static String WIFI_MAC_KEY = "WIFIMAC";

    public static String getAuthToken(Context c) {
        return getSharedPreferences(c).getString(AUTH_TOKEN_KEY, null);
    }

    public static void setAuthToken(Context c, String token) {
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN_KEY, token);
        editor.commit();
    }

    public static String getWifiMac(Context c) {
        return getSharedPreferences(c).getString(WIFI_MAC_KEY, null);
    }

    public static void setWifiMac(Context c, String mac) {
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(WIFI_MAC_KEY, mac);
        editor.commit();
    }

    private static SharedPreferences getSharedPreferences(Context c) {
        return c.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
    }
}
