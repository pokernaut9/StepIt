package net.devcats.stepit.Handlers;

import android.content.Context;
import android.content.SharedPreferences;

import net.devcats.stepit.StepItApplication;

/**
 * Created by Ken Juarez on 12/19/16.
 * Class used to handle interaction between app and saved preferences.
 */

public class PreferencesHandler {

    private static PreferencesHandler instance;

    public static PreferencesHandler getInstance() {
        if (instance == null) {
            instance = new PreferencesHandler();
        }
        return instance;
    }

    public void setString(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public void setInt(Context context, String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE);
        return preferences.getInt(key, -1);
    }

    public void clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }

    public void remove(String key) {
        Context context = StepItApplication.getAppComponent().context();
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE);
        preferences.edit().remove(key).apply();
    }
}