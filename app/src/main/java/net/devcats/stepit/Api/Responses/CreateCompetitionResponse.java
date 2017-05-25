package net.devcats.stepit.Api.Responses;

import com.google.gson.annotations.SerializedName;

import net.devcats.stepit.Model.Competition;

public class CreateCompetitionResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private Competition competition;

    public Competition getCompetition() {
        return competition;
    }
}
