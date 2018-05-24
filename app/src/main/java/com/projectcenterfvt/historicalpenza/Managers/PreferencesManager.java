package com.projectcenterfvt.historicalpenza.Managers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by roman on 24.05.2018.
 */

public class PreferencesManager extends Activity {

    private static SharedPreferences preferences;
    private final String APP_PREFERENCES = "account";
    private final String APP_PREFERENCES_TOKEN = "token";
    private final String APP_PREFERENCES_VERSION = "version";
    private final String APP_PREFERENCES_FIRST_TIME = "first_time";
    private final String APP_PREFERENCES_NOTIFICATION = "notification";

    public PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public String getVersion() {
        return preferences.getString(APP_PREFERENCES_VERSION, "0.0.0");
    }

    public void setVersion(String version) {
        preferences.edit().putString(APP_PREFERENCES_VERSION, version).apply();
    }

    public boolean getFirstTime() {
        return preferences.getBoolean(APP_PREFERENCES_FIRST_TIME, true);
    }

    public void setFirstTime(boolean b) {
        preferences.edit().putBoolean(APP_PREFERENCES_FIRST_TIME, b).apply();
    }

    public String getToken() {
        return preferences.getString(APP_PREFERENCES_TOKEN, "");
    }

    public void setToken(String token) {
        preferences.edit().putString(APP_PREFERENCES_TOKEN, token).apply();
    }

    public boolean getNotificationStatus() {
        return preferences.getBoolean(APP_PREFERENCES_NOTIFICATION, true);
    }

    public void setNotificationStatus(boolean b) {
        preferences.edit().putBoolean(APP_PREFERENCES_NOTIFICATION, b).apply();
    }

}
