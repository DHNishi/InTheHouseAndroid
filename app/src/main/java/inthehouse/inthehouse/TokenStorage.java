package inthehouse.inthehouse;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ryan on 2/18/2015.
 */
public class TokenStorage {

    private static String SHARED_PREFERENCES_NAME = "AUTHTOKEN_SHARED_PREFS";
    private static String AUTH_TOKEN_KEY = "AUTHTOKEN";

    static String getAuthToken(Context c) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getString(AUTH_TOKEN_KEY, null);
    }

    static void setAuthToken(Context c, String token) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN_KEY, token);
        editor.commit();
    }
}
