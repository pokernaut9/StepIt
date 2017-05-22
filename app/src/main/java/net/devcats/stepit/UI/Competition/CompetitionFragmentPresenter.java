package net.devcats.stepit.UI.Competition;

import com.google.gson.Gson;

import net.devcats.stepit.Handlers.CompetitionsHandler;
import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.Model.Competition;
import net.devcats.stepit.StepItApplication;

import java.util.List;

import javax.inject.Inject;

public class CompetitionFragmentPresenter implements CompetitionsHandler.CompetitionsRepositoryCallbacks{

    @Inject
    UserHandler userHandler;
    @Inject
    CompetitionsHandler competitionsHandler;

    private CompetitionFragmentView view;
    private Competition competition;

    CompetitionFragmentPresenter(String competitionString) {
        StepItApplication.getAppComponent().inject(this);
        competition = new Gson().fromJson(competitionString, Competition.class);
    }

    void attach(CompetitionFragmentView competitionFragmentView) {
        view = competitionFragmentView;
        competitionsHandler.registerListener(this);
    }

    void present() {
        view.setupUI(competition);
    }

    void refresh() {
        competitionsHandler.getCompetitions(userHandler.getUser().getId(), competition.getId());
    }

    void detach() {
        view = null;
        competitionsHandler.unregisterCallback(this);
    }

    @Override
    public void onCompetitionsReceived(List<Competition> competitions) {
        view.updateCompetition(competition);
    }

    interface CompetitionFragmentView {
        void setupUI(Competition competition);
        void updateCompetition(Competition competition);
    }

}
