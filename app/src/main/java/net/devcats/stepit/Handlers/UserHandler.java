package net.devcats.stepit.Handlers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import net.devcats.stepit.Model.UserModel;
import net.devcats.stepit.Utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
        PreferencesUtils.getInstance().setString(context, KEY_USER_DATA, new Gson().toJson(user));
    }

    public UserModel loadUser(Context context) {
        return user = new Gson().fromJson(PreferencesUtils.getInstance().getString(context, KEY_USER_DATA), UserModel.class);
    }

    public boolean parseAndSaveUserFromJSON(Context context, JSONObject object) {
        try {
            user = new UserModel();

            user.setId(object.getInt("id"));
            user.setName(object.getString("name"));
            user.setEmail(object.getString("email"));

            saveUser(context);

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
