package net.devcats.stepit.Api.Responses;


import com.google.gson.annotations.SerializedName;

import net.devcats.stepit.Model.Competition;

import java.util.List;

public class GetCompetitionsResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private List<Competition> competitions;

    public List<Competition> getCompetitions() {
        return competitions;
    }
}
