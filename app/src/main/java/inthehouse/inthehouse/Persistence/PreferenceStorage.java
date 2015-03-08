package inthehouse.inthehouse.Persistence;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ryan on 2/18/2015.
 */
public class PreferenceStorage {

    private static String SHARED_PREFERENCES_NAME = "INTHEHOUSE_SHARED_PREFS";

    private static String AUTH_TOKEN_KEY = "AUTHTOKEN";
    private static String WIFI_SSID_KEY = "WIFISSID";
    private static String INCOGNITO_TIMEOUT = "INCOGTIMEOUT";
    private static String LAST_CHECKIN_TIME = "CHECKINTIME";

    public static String getAuthToken(Context c) {
        return getSharedPreferences(c).getString(AUTH_TOKEN_KEY, null);
    }

    public static void setAuthToken(Context c, String token) {
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN_KEY, token);
        editor.commit();
    }

    public static String getWifiSSID(Context c) {
        return getSharedPreferences(c).getString(WIFI_SSID_KEY, null);
    }

    public static void setWifiSSID(Context c, String mac) {
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(WIFI_SSID_KEY, mac);
        editor.commit();
    }

    public static boolean isIncognito(Context c) {
        long time = Long.valueOf(getSharedPreferences(c).getString(INCOGNITO_TIMEOUT, "0"));
        return System.currentTimeMillis() < time || time == -1;
    }

    public static void setIncognitoTimeout(Context c, long time) {
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String newTimeout = String.valueOf(System.currentTimeMillis() + time);
        editor.putString(INCOGNITO_TIMEOUT, newTimeout);
        editor.commit();
    }

    public static void toggleIncognito(Context c) {
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String newTimeout = isIncognito(c) ? "0" : "-1";
        editor.putString(INCOGNITO_TIMEOUT, newTimeout);
        editor.commit();
    }

    public static void setIncognitoOn(Context c) {
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String newTimeout = String.valueOf(-1);
        editor.putString(INCOGNITO_TIMEOUT, newTimeout);
        editor.commit();
    }

    public static void setIncognitoOff(Context c) {
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String newTimeout = String.valueOf(0);
        editor.putString(INCOGNITO_TIMEOUT, newTimeout);
        editor.commit();
    }

    public static void setLastCheckinTime(Context c) {
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String time = String.valueOf(System.currentTimeMillis());
        editor.putString(LAST_CHECKIN_TIME, time);
        editor.commit();
    }

    public static long getLastCheckinTime(Context c) {
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        long time = Long.valueOf(sharedPreferences.getString(LAST_CHECKIN_TIME, "0"));
        return time;
    }

    private static SharedPreferences getSharedPreferences(Context c) {
        return c.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
    }
}
