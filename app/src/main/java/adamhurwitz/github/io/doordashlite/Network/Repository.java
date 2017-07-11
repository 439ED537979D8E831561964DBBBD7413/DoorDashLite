package adamhurwitz.github.io.doordashlite.Network;

import java.util.List;

import javax.inject.Inject;

import adamhurwitz.github.io.doordashlite.dbflow.Restaurant;
import rx.Observable;

/**
 * Created by ahurwitz on 7/9/17.
 */

public class Repository {

    private Service service;

    @Inject
    public Repository(Service service) {
        this.service = service;
    }

    public Observable<List<Restaurant>> getRestaurantsFromNetwork(){
        return service.getRestaurauntsFromNetwork();
    }
}
