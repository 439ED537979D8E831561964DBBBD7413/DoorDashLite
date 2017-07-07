package adamhurwitz.github.io.doordashlite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import retrofit2.http.GET;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.doordash.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    private void initData() {
        OkHttpClient client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        retrofit.create(DoorDashService.class)
                .getRestauraunts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurants -> {
                    //todo: add into adapter
                    //todo: add test case here
                    for (Restaurant restaurant : restaurants) {
                        Log.v(MainActivity.class.getSimpleName(), "DATA_RETURNED: " +
                        restaurant.getName() + " " +
                        restaurant.getId() + " " +
                        restaurant.getStatus() + " " +
                        restaurant.getDescription() + " " +
                        restaurant.getCover_img_url() + " " +
                        restaurant.getDelivery_fee());
                    }
                }, throwable -> Log.e(MainActivity.class.getSimpleName(), throwable.toString()));
    }

    public interface DoorDashService {
        @GET("/v2/restaurant/?lat=37.422740&lng=-122.139956")
        Observable<ArrayList<Restaurant>> getRestauraunts();
    }
}
