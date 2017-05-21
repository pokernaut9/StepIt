package net.devcats.stepit.Api.Responses;

import com.google.gson.annotations.SerializedName;

import net.devcats.stepit.Model.User;


public class LoginResponse {

    @SerializedName("status")
    private int status;

    @SerializedName("data")
    private LoginResponseDetails data;

    public LoginResponseDetails getLoginDetails() {
        return data;
    }

    public class LoginResponseDetails {
        @SerializedName("success")
        boolean success;

        @SerializedName("user")
        User user;

        public boolean getSuccess() {
            return success;
        }

        public User getUser() {
            return user;
        }
    }
}
