package net.devcats.stepit.UI.NewCompetition;

import net.devcats.stepit.Handlers.CompetitionsHandler;
import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.Model.Competition;
import net.devcats.stepit.StepItApplication;

import javax.inject.Inject;

public class NewCompetitionFragmentPresenter {

    @Inject
    UserHandler userHandler;
    @Inject
    CompetitionsHandler competitionsHandler;

    private NewCompetitionView view;

    NewCompetitionFragmentPresenter() {
        StepItApplication.getAppComponent().inject(this);
    }

    void attach(NewCompetitionView view) {
        this.view = view;

    }

    void present() {
        view.setAddNewCompetitionButtonVisibility(false);
    }

    void refresh() {

    }

    void detach() {
        view = null;
    }

    void createNewCompetition(Competition competition) {
        competitionsHandler.createCompetition(userHandler.getUser().getId(), competition);
    }

    interface NewCompetitionView {
        void setAddNewCompetitionButtonVisibility(boolean visible);
    }
}
