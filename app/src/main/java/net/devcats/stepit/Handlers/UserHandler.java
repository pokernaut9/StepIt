package net.devcats.stepit.Handlers;

import com.google.gson.Gson;

import net.devcats.stepit.Api.StepItApi;
import net.devcats.stepit.Model.User;
import net.devcats.stepit.StepItApplication;

import javax.inject.Inject;

public class UserHandler {

    private static final String KEY_USER_DATA = "user_data";

    @Inject
    StepItApi stepItApi;
    @Inject
    PreferencesHandler preferencesHandler;

    private User user;

    public UserHandler() {
        StepItApplication.getAppComponent().inject(this);
    }

    public void removeUser() {
        user = new User();
        saveUser();
    }

    public User getUser() {
        return user;
    }

    public void saveUser() {
        preferencesHandler.setString(KEY_USER_DATA, new Gson().toJson(user));
    }

    public User loadUser() {
        return user = new Gson().fromJson(preferencesHandler.getString(KEY_USER_DATA), User.class);
    }

    public void setUser(User user) {
        this.user = user;
        saveUser();
    }
}
