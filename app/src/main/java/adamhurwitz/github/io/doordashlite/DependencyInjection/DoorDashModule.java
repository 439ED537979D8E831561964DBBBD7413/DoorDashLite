package adamhurwitz.github.io.doordashlite.DependencyInjection;

import android.app.Application;

import javax.inject.Singleton;

import adamhurwitz.github.io.doordashlite.BuildConfig;
import adamhurwitz.github.io.doordashlite.Network.Repository;
import adamhurwitz.github.io.doordashlite.Network.Service;
import adamhurwitz.github.io.doordashlite.StethoInitialization;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ahurwitz on 7/9/17.
 */
@Module
public class DoorDashModule {

    private static final String BASE_URL = "https://api.doordash.com/v2/";

    Application application;

    public DoorDashModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public OkHttpClient provideHttpClient(){
        if (BuildConfig.DEBUG) {
            return StethoInitialization.getStethoClient();
        } else {
            return new OkHttpClient();
        }
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient client){
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    public Service provideRestaurantService(Retrofit retrofit) {
        return retrofit.create(Service.class);
    }

    @Provides
    public Repository provideRepository(Service service){
        return new Repository(service);
    }
}
