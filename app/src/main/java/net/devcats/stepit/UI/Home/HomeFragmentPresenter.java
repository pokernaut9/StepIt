package net.devcats.stepit.UI.Home;

import android.graphics.Bitmap;
import android.view.View;

import com.bumptech.glide.Glide;

import net.devcats.stepit.Handlers.DeviceHandler;
import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.Model.Competition;
import net.devcats.stepit.Handlers.CompetitionsHandler;
import net.devcats.stepit.StepItApplication;

import java.util.List;

import javax.inject.Inject;

public class HomeFragmentPresenter implements DeviceHandler.DeviceResponseListener, CompetitionsHandler.CompetitionsRepositoryCallbacks {

    @Inject
    DeviceHandler deviceHandler;
    @Inject
    UserHandler userHandler;
    @Inject
    CompetitionsHandler competitionsRepository;

    private HomeFragmentView view;

    HomeFragmentPresenter() {
        StepItApplication.getAppComponent().inject(this);
    }

    void attach(HomeFragmentView homeFragmentView) {
        view = homeFragmentView;

        deviceHandler.setDeviceResponseListener(this);
        competitionsRepository.registerListener(this);
    }

    void present() {
        view.setName(userHandler.getUser().getName());
        view.updateProfilePicture(userHandler.getUser().getProfilePicture());

        deviceHandler.requestSteps();
        competitionsRepository.getCompetitions();


//        btnDisconnectDevice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DeviceHandler.getInstance().removeConnectedDevice(getActivity());
//            }
//        });
//
//        btnGetSteps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DeviceHandler.getInstance().requestSteps();
//            }
//        });
//
//        btnClearPreferences.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

    }

    void detach() {
        view = null;
        deviceHandler.setDeviceResponseListener(null);
        competitionsRepository.unregisterCallback(this);
    }


    void refresh() {
        deviceHandler.requestSteps();
        competitionsRepository.getCompetitions();
    }

    void removeConnectedDevice() {
        deviceHandler.removeConnectedDevice();
    }

    @Override
    public void onStepsReceived(int steps) {
        view.onStepsReceived(steps);
    }

    @Override
    public void onCompetitionsReceived(List<Competition> competitions) {
//        competitions.add(0, new Competition()); // TODO: MAJOR HACK!!! FIGURE THIS OUT
        view.onCompetitionsReceived(competitions);
    }

    interface HomeFragmentView {
        void setName(String name);
        void onStepsReceived(int steps);
        void onCompetitionsReceived(List<Competition> competitions);
        void showError();

        void updateProfilePicture(String profilePicturePath);
    }
}
