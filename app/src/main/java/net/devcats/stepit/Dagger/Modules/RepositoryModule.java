package net.devcats.stepit.Dagger.Modules;

import net.devcats.stepit.Dagger.Scopes.AppScope;
import net.devcats.stepit.Repositories.PostsRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    @AppScope
    PostsRepository postsRepository() {
        return PostsRepository.getInstance();
    }

}
