package net.devcats.stepit.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import net.devcats.stepit.Model.Device;

/**
 * Created by Ken Juarez on 12/19/16.
 * Class used to handle interaction between app and saved preferences.
 */

public class PreferencesUtils {

    private static PreferencesUtils instance;

    private Context mContext;

    public static PreferencesUtils getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesUtils(context);
        }
        return instance;
    }

    public PreferencesUtils(Context context) {
        mContext = context;
    }

    public void setString(String key, String value) {
        SharedPreferences preferences = mContext.getSharedPreferences(mContext.getApplicationInfo().name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        SharedPreferences preferences = mContext.getSharedPreferences(mContext.getApplicationInfo().name, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public void setInt(String key, int value) {
        SharedPreferences preferences = mContext.getSharedPreferences(mContext.getApplicationInfo().name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        SharedPreferences preferences = mContext.getSharedPreferences(mContext.getApplicationInfo().name, Context.MODE_PRIVATE);
        return preferences.getInt(key, -1);
    }

    public void clear() {
        SharedPreferences preferences = mContext.getSharedPreferences(mContext.getApplicationInfo().name, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }

    public void remove(String key) {
        SharedPreferences preferences = mContext.getSharedPreferences(mContext.getApplicationInfo().name, Context.MODE_PRIVATE);
        preferences.edit().remove(key).apply();
    }
}
