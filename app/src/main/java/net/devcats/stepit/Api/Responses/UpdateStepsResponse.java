package net.devcats.stepit.Api.Responses;

import com.google.gson.annotations.SerializedName;

public class UpdateStepsResponse {

    @SerializedName("status")
    private int status;

    @SerializedName("data")
    private UpdateStepsResponseDetails details;

    public boolean getSuccess() {
        return status == 200;
    }

    private class UpdateStepsResponseDetails {
        @SerializedName("message")
        String message;

        public String getMessage() {
            return message;
        }
    }
}
