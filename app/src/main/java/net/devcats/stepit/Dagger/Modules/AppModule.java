package net.devcats.stepit.Dagger.Modules;

import com.fatboyindustrial.gsonjodatime.DateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.devcats.stepit.Api.StepItApi;
import net.devcats.stepit.BuildConfig;
import net.devcats.stepit.Dagger.Scopes.AppScope;

import org.joda.time.DateTime;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = NetworkModule.class)
public class AppModule {

    @Provides
    @AppScope
    public StepItApi stepItApi(Retrofit stepItRetrofit) {
        return stepItRetrofit.create(StepItApi.class);
    }

    @Provides
    @AppScope
    public Retrofit retrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .baseUrl(BuildConfig.BASE_URL)
                .build();
    }

    @Provides
    @AppScope
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeConverter());
        return gsonBuilder.create();
    }
}
