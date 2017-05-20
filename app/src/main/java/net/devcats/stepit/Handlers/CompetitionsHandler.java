package net.devcats.stepit.Handlers;

import net.devcats.stepit.Api.StepItApi;
import net.devcats.stepit.Model.ApiResponses.GetCompetitionsResponse;
import net.devcats.stepit.Model.Competition;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompetitionsHandler {

    private StepItApi stepItApi;
    private List<CompetitionsRepositoryCallbacks> callbacks = new ArrayList<>();

    public CompetitionsHandler(StepItApi stepItApi) {
        this.stepItApi = stepItApi;
    }

    public void registerListener(CompetitionsRepositoryCallbacks callback) {
        callbacks.add(callback);
    }

    public void unregisterCallback(CompetitionsRepositoryCallbacks callback) {
        callbacks.remove(callback);
    }

    public void getCompetitions() {
        Call<GetCompetitionsResponse> repos = stepItApi.getCompetitions(1);

        repos.enqueue(new Callback<GetCompetitionsResponse>() {
            @Override
            public void onResponse(Call<GetCompetitionsResponse> call, Response<GetCompetitionsResponse> response) {
                notifyCallbacksSuccess(response.body());
            }

            @Override
            public void onFailure(Call<GetCompetitionsResponse> call, Throwable t) {
                t.printStackTrace();
            }

        });
    }

    private void notifyCallbacksSuccess(GetCompetitionsResponse response) {
        for (CompetitionsRepositoryCallbacks callback : callbacks) {
            callback.onCompetitionsReceived(response.getCompetitions());
        }
    }

    public interface CompetitionsRepositoryCallbacks {
        void onCompetitionsReceived(List<Competition> competitions);
    }
}
