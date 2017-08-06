package adamhurwitz.github.io.doordashlite.Network;

import java.util.List;

import adamhurwitz.github.io.doordashlite.dbflow.Restaurant;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by ahurwitz on 7/9/17.
 */

public interface Service {
    @GET("{path}")
    Observable<List<Restaurant>> getRestaurauntsFromNetwork(
            @Path("path") String path,
            @Query("lat") double lat,
            @Query("lng") double lng
    );
}
