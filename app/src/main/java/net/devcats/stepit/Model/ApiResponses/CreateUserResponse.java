package net.devcats.stepit.Model.ApiResponses;

import com.google.gson.annotations.SerializedName;

import net.devcats.stepit.Model.User;

public class CreateUserResponse {

    @SerializedName("status")
    private int status;
    @SerializedName("data")
    private CreateUserResponseDetails details;

    public boolean getSuccess() {
        return status == 200;
    }

    public CreateUserResponseDetails getDetails() {
        return details;
    }

    public class CreateUserResponseDetails {
        @SerializedName("user")
        private User user;

        public User getUser() {
            return user;
        }
    }
}
