package adamhurwitz.github.io.doordashlite.DependencyInjection;

import android.app.Application;

import javax.inject.Singleton;

import adamhurwitz.github.io.doordashlite.Network.Repository;
import adamhurwitz.github.io.doordashlite.Network.Service;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

/**
 * Created by ahurwitz on 7/9/17.
 */
@Module
public class DoorDashModule {

    private static final String BASE_URL = "https://api.doordash.com";

    Application application;

    public DoorDashModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public OkHttpClient provideHttpClient(){
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient){
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(okHttpClient)
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
