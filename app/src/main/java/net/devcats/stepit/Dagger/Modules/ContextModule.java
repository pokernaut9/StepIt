package net.devcats.stepit.Dagger.Modules;

import android.content.Context;

import net.devcats.stepit.Dagger.Scopes.AppScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    private final Context context;

    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    @AppScope
    public Context context() {
        return context;
    }

}
