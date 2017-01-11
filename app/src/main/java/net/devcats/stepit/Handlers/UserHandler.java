package net.devcats.stepit.Handlers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import net.devcats.stepit.Model.UserModel;

/**
 * Created by Ken Juarez on 12/17/16.
 * Used to manage all activity with the current StepIt user
 */

public class UserHandler {

    private static final String KEY_USER_DATA = "user_data";

    private UserModel user;
    private static UserHandler instance;

    public static UserHandler getInstance() {
        if (instance == null) {
            instance = new UserHandler();
        }
        return instance;
    }

    public void removeUser(Context context) {
        user = new UserModel();
        saveUser(context);
    }

    public UserModel getUser() {
        return user;
    }

    public void saveUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_DATA, new Gson().toJson(user));
        editor.apply();
    }

    public UserModel loadUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE);
        return user = new Gson().fromJson(preferences.getString(KEY_USER_DATA, ""), UserModel.class);
    }
}
