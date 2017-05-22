package net.devcats.stepit.Dagger.Modules;

import net.devcats.stepit.Api.StepItApi;
import net.devcats.stepit.Dagger.Scopes.AppScope;
import net.devcats.stepit.Handlers.CompetitionsHandler;
import net.devcats.stepit.Handlers.DeviceHandler;
import net.devcats.stepit.Handlers.PreferencesHandler;
import net.devcats.stepit.Handlers.UserHandler;

import dagger.Module;
import dagger.Provides;

@Module(includes = AppModule.class)
public class HandlerModule {

    @Provides
    @AppScope
    DeviceHandler deviceHandler() {
        return new DeviceHandler();
    }

    @Provides
    @AppScope
    UserHandler userHandler() {
        return new UserHandler();
    }

    @Provides
    @AppScope
    PreferencesHandler preferencesHandler() {
        return new PreferencesHandler();
    }

    @Provides
    @AppScope
    CompetitionsHandler competitionsHandler(StepItApi stepItApi) {
        return new CompetitionsHandler(stepItApi);
    }
}
