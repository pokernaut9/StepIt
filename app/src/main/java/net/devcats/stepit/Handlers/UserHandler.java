package net.devcats.stepit.Handlers;

import com.google.gson.Gson;

import net.devcats.stepit.Api.Responses.UpdateStepsResponse;
import net.devcats.stepit.Api.StepItApi;
import net.devcats.stepit.Model.User;
import net.devcats.stepit.StepItApplication;
import net.devcats.stepit.Utils.DateUtils;
import net.devcats.stepit.Utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHandler {

    private static final String KEY_USER_DATA = "user_data";

    @Inject
    StepItApi stepItApi;

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
        PreferencesHandler.getInstance().setString(KEY_USER_DATA, new Gson().toJson(user));
    }

    public User loadUser() {
        return user = new Gson().fromJson(PreferencesHandler.getInstance().getString(KEY_USER_DATA), User.class);
    }

    public void setUser(User user) {
        this.user = user;
        saveUser();
    }
}
