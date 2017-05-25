package net.devcats.stepit.UI.Competition;

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
    private int competitionId;

    CompetitionFragmentPresenter(int competitionId) {
        StepItApplication.getAppComponent().inject(this);
        this.competitionId = competitionId;
    }

    void attach(CompetitionFragmentView competitionFragmentView) {
        view = competitionFragmentView;
        competitionsHandler.registerListener(this);
    }

    void present() {
        view.setupUI();
        view.setAddNewCompetitionButtonVisibility(true);
        refresh();
    }

    void refresh() {
        competitionsHandler.getCompetitions(userHandler.getUser().getId());
    }

    void detach() {
        view = null;
        competitionsHandler.unregisterCallback(this);
    }

    @Override
    public void onCompetitionCreated(Competition competition) {
        // ignore
    }

    @Override
    public void onCompetitionsReceived(List<Competition> competitions) {
        for (Competition competition : competitions) {
            if (competition.getId() == competitionId) {
                view.updateCompetition(competition);
                return;
            }
        }
    }

    @Override
    public void onError() {
        view.showError();
    }

    void addUserToCompetition() {
        // TODO
    }

    interface CompetitionFragmentView {
        void setupUI();
        void updateCompetition(Competition competition);
        void showError();
        void setAddNewCompetitionButtonVisibility(boolean visibile);
    }

}
