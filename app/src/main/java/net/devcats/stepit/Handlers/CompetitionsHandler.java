package net.devcats.stepit.Handlers;

import net.devcats.stepit.Api.Responses.CreateCompetitionResponse;
import net.devcats.stepit.Api.Responses.UpdateStepsResponse;
import net.devcats.stepit.Api.StepItApi;
import net.devcats.stepit.Api.Responses.GetCompetitionsResponse;
import net.devcats.stepit.Model.Competition;
import net.devcats.stepit.Utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

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

    public void createCompetition(int userId, Competition competition) {
        Call<CreateCompetitionResponse> createCompetitionResponseCall = stepItApi.createCompetition(userId, competition.getName(), competition.getDescription(), competition.getSize(), DateUtils.formatServerDate(competition.getStartDate()), DateUtils.formatServerDate(competition.getEndDate()));
        createCompetitionResponseCall.enqueue(new Callback<CreateCompetitionResponse>() {
            @Override
            public void onResponse(Call<CreateCompetitionResponse> call, Response<CreateCompetitionResponse> response) {
                notifyCallbacksOnCreateSuccess(response.body());
            }

            @Override
            public void onFailure(Call<CreateCompetitionResponse> call, Throwable t) {
                notifyCallbacksOnError();
                t.printStackTrace();
            }
        });
    }

    private void notifyCallbacksOnCreateSuccess(CreateCompetitionResponse response) {
        for (CompetitionsRepositoryCallbacks callback : callbacks) {
            callback.onCompetitionCreated(response.getCompetition());
        }
    }

    public void getCompetitions(int userId) {

        Call<GetCompetitionsResponse> getCompetitionsResponseCall = stepItApi.getCompetitions(userId);

        getCompetitionsResponseCall.enqueue(new Callback<GetCompetitionsResponse>() {
            @Override
            public void onResponse(Call<GetCompetitionsResponse> call, Response<GetCompetitionsResponse> response) {
                notifyCallbacksSuccess(response.body());
            }

            @Override
            public void onFailure(Call<GetCompetitionsResponse> call, Throwable t) {
                notifyCallbacksOnError();
                t.printStackTrace();
            }

        });
    }

    public void updateUsersSteps(final int userId, final int steps) {

        Call<UpdateStepsResponse> updateStepsResponseCall = stepItApi.updateSteps(userId, DateUtils.getCurrentDate(), steps);
        updateStepsResponseCall.enqueue(new Callback<UpdateStepsResponse>() {
            @Override
            public void onResponse(Call<UpdateStepsResponse> call, Response<UpdateStepsResponse> response) {
                // Do nothing
            }

            @Override
            public void onFailure(Call<UpdateStepsResponse> call, Throwable t) {
                notifyCallbacksOnError();
                t.printStackTrace();
            }
        });
    }

    private void notifyCallbacksSuccess(GetCompetitionsResponse response) {
        for (CompetitionsRepositoryCallbacks callback : callbacks) {
            callback.onCompetitionsReceived(response.getCompetitions());
        }
    }

    private void notifyCallbacksOnError() {
        for (CompetitionsRepositoryCallbacks callback : callbacks) {
            callback.onError();
        }
    }

    public interface CompetitionsRepositoryCallbacks {
        void onCompetitionCreated(Competition competition);
        void onCompetitionsReceived(List<Competition> competitions);
        void onError();
    }
}
