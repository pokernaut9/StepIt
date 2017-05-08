package net.devcats.stepit.UI.Home;

import net.devcats.stepit.Handlers.DeviceHandler;
import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.Model.Post;
import net.devcats.stepit.Repositories.PostsRepository;
import net.devcats.stepit.StepItApplication;

import java.util.List;

import javax.inject.Inject;

public class HomeFragmentPresenter implements DeviceHandler.DeviceResponseListener, PostsRepository.PostsRepositoryCallbacks {

    @Inject
    DeviceHandler deviceHandler;
    @Inject
    UserHandler userHandler;
    @Inject
    PostsRepository postsRepo;

    private HomeFragmentView view;

    HomeFragmentPresenter() {
        StepItApplication.getAppComponent().inject(this);
    }

    void attach(HomeFragmentView homeFragmentView) {
        view = homeFragmentView;

        deviceHandler.setDeviceResponseListener(this);
        postsRepo.setListener(this);

        deviceHandler.requestSteps();
    }

    void present() {
        view.setupUI();
        postsRepo.getPosts();

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
        postsRepo.setListener(null);
    }


    void refresh() {
        deviceHandler.requestSteps();
        postsRepo.getPosts();
    }

    void removeConnectedDevice() {
        deviceHandler.removeConnectedDevice();

    }

    @Override
    public void onStepsReceived(int steps) {
        view.onStepsReceived(steps);
    }

    @Override
    public void onPostsReceived(List<Post> posts) {
        view.updatePosts(posts);
    }

    interface HomeFragmentView {
        void setupUI();
        void onStepsReceived(int steps);
        void updatePosts(List<Post> posts);
        void showError();
    }
}
