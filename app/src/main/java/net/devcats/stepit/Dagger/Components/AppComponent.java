package net.devcats.stepit.Dagger.Components;

import android.content.Context;

import net.devcats.stepit.Dagger.Modules.AppModule;
import net.devcats.stepit.Dagger.Modules.ContextModule;
import net.devcats.stepit.Dagger.Modules.HandlerModule;
import net.devcats.stepit.Dagger.Modules.RepositoryModule;
import net.devcats.stepit.Dagger.Scopes.AppScope;
import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.UI.Competition.CompetitionFragmentPresenter;
import net.devcats.stepit.UI.Home.HomeFragmentPresenter;
import net.devcats.stepit.UI.Login.LoginActivity;
import net.devcats.stepit.UI.SelectDevice.SelectDeviceFragment;
import net.devcats.stepit.Handlers.DeviceHandlers.FitBitDevice;
import net.devcats.stepit.MainActivity;
import net.devcats.stepit.UI.SignUp.CreateAccountActivity;

import dagger.Component;

@AppScope
@Component(modules = {AppModule.class, HandlerModule.class, ContextModule.class, RepositoryModule.class})
public interface AppComponent {

    Context context();

    void inject(MainActivity mainActivity);
    void inject(LoginActivity loginActivity);
    void inject(SelectDeviceFragment selectDeviceFragment);
    void inject(FitBitDevice fitBitDevice);
    void inject(HomeFragmentPresenter homeFragmentPresenter);
    void inject(CreateAccountActivity createAccountActivity);
    void inject(CompetitionFragmentPresenter competitionFragmentPresenter);
    void inject(UserHandler userHandler);

}
