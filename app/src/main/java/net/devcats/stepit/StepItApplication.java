package net.devcats.stepit;

import android.app.Application;

import net.devcats.stepit.Dagger.Components.AppComponent;
import net.devcats.stepit.Dagger.Components.DaggerAppComponent;
import net.devcats.stepit.Dagger.Modules.ContextModule;

import timber.log.Timber;

public class StepItApplication extends Application {

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        appComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

}
