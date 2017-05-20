package net.devcats.stepit.Handlers;

import android.content.Context;

import com.google.gson.Gson;
import net.devcats.stepit.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ken Juarez on 12/17/16.
 * Used to manage all activity with the current StepIt user
 */

public class UserHandler {

    private static final String KEY_USER_DATA = "user_data";

    private User user;
    private static UserHandler instance;

    public static UserHandler getInstance() {
        if (instance == null) {
            instance = new UserHandler();
        }
        return instance;
    }

    public void removeUser() {
        user = new User();
        saveUser();
    }

    public User getUser() {
        return user;
    }

    public void saveUser() {
        PreferencesHandler.getInstance().setString(KEY_USER_DATA, new Gson().toJson(user));
    }

    public User loadUser() {
        return user = new Gson().fromJson(PreferencesHandler.getInstance().getString(KEY_USER_DATA), User.class);
    }

    public boolean parseAndSaveUserFromJSON(JSONObject object) {
        try {
            user = new User();

            user.setId(object.getInt("id"));
            user.setName(object.getString("name"));
            user.setEmail(object.getString("email"));

            saveUser();

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setUser(User user) {
        this.user = user;
        saveUser();
    }
}
