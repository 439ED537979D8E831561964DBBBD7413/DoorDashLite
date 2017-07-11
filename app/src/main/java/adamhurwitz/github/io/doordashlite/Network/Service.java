package adamhurwitz.github.io.doordashlite.Network;

import java.util.List;

import adamhurwitz.github.io.doordashlite.dbflow.Restaurant;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by ahurwitz on 7/9/17.
 */

public interface Service {
    @GET("/v2/restaurant/?lat=37.422740&lng=-122.139956")
    Observable<List<Restaurant>> getRestaurauntsFromNetwork();
}
