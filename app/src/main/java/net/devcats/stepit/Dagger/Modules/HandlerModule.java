package net.devcats.stepit.Dagger.Modules;

import net.devcats.stepit.Dagger.Scopes.AppScope;
import net.devcats.stepit.Handlers.DeviceHandler;
import net.devcats.stepit.Handlers.PreferencesHandler;
import net.devcats.stepit.Handlers.UserHandler;

import dagger.Module;
import dagger.Provides;

@Module
public class HandlerModule {

    @Provides
    @AppScope
    DeviceHandler deviceHandler() {
        return DeviceHandler.getInstance();
    }

    @Provides
    @AppScope
    UserHandler userHandler() {
        return UserHandler.getInstance();
    }

    @Provides
    @AppScope
    PreferencesHandler preferencesHandler() {
        return PreferencesHandler.getInstance();
    }
}
