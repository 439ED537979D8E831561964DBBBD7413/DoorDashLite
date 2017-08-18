package adamhurwitz.github.io.doordashlite.Network;

import android.util.Log;
import android.util.Pair;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import adamhurwitz.github.io.doordashlite.MainViewModel;
import adamhurwitz.github.io.doordashlite.RxHelpers;
import adamhurwitz.github.io.doordashlite.dbflow.DoorDashDb;
import adamhurwitz.github.io.doordashlite.dbflow.Restaurant;
import adamhurwitz.github.io.doordashlite.dbflow.Restaurant_Table;
import rx.Observable;
import rx.subjects.ReplaySubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ahurwitz on 7/9/17.
 */

public class Repository {

    private Service service;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private Map<Integer, Restaurant> restaurantsMap = new HashMap<>();
    private ReplaySubject<List<Restaurant>> restaurantSubscriber = ReplaySubject.create();
    private ReplaySubject<Boolean> progressBarSubscriber = ReplaySubject.create();

    @Inject
    public Repository(Service service) {
        this.service = service;
    }

    public Observable<List<Restaurant>> getRestaurantsFromNetwork(String path,
                                                                  double lat, double lng){
        return service.getRestaurauntsFromNetwork(path, lat, lng);
    }

    public void initData(String path, double lat, double lng) {
        compositeSubscription.add(
                getRestaurantsFromDb()
                        .compose(RxHelpers.IOAndIOSchedulers())
                        .subscribe(restaurantsDb -> {
                            if (restaurantsDb.isEmpty()) {
                                populateDb(path, lat, lng);
                            } else {
                                updateDb(path, lat, lng);
                            }
                        }, throwable -> Log.v(MainViewModel.class.getSimpleName(), throwable.toString())));
    }

    public static Observable<List<Restaurant>> getRestaurantsFromDb() {
        return Observable.just(SQLite.select()
                .from(Restaurant.class)
                .orderBy(Restaurant_Table.isFavorite, false)
                .queryList());
    }

    private void populateDb(String path, double lat, double lng) {
        compositeSubscription.add(
                getRestaurantsFromNetwork(path, lat, lng)
                        .compose(RxHelpers.IOAndMainThreadSchedulers())
                        .flatMap(restaurantsFromNetwork -> Observable.from(restaurantsFromNetwork))
                        .doOnCompleted(() -> {
                            progressBarSubscriber.onNext(false);
                            List<Restaurant> restaurants = new ArrayList<>(restaurantsMap.values());
                            restaurantSubscriber.onNext(restaurants);
                            saveRestaurants(restaurants);
                        })
                        .subscribe(restaurantFromNetwork -> {
                                    progressBarSubscriber.onNext(true);
                                    restaurantsMap.put(restaurantFromNetwork.getId(), restaurantFromNetwork);
                                },
                                throwable -> Log.v(MainViewModel.class.getSimpleName(), throwable.toString())));
    }

    private void updateDb(String path, double lat, double lng) {
        compositeSubscription.add(
                Observable.zip(getRestaurantsFromNetwork(path, lat, lng), getRestaurantsFromDb(),
                        (networkRestaurants, dbRestaurants) -> new Pair<>(networkRestaurants, dbRestaurants))
                        .compose(RxHelpers.IOAndMainThreadSchedulers())
                        .doOnCompleted(() -> getRestaurantsFromDb()
                                .subscribe(restaurants -> {
                                    progressBarSubscriber.onNext(false);
                                    restaurantSubscriber.onNext(restaurants);
                                }))
                        .subscribe(pair -> {
                            progressBarSubscriber.onNext(true);
                            updateRestaurantMap(pair.second);
                            for (Restaurant restaurantFromNetwork : pair.first) {
                                int id = restaurantFromNetwork.getId();
                                if (restaurantsMap.containsKey(id)) {
                                    Restaurant restaurantDb = restaurantsMap.get(id);
                                    restaurantDb.setStatus(restaurantDb.getStatus() != restaurantFromNetwork.getStatus() ? restaurantFromNetwork.getStatus() : restaurantDb.getStatus());
                                    restaurantDb.setDelivery_fee(restaurantDb.getDelivery_fee() != restaurantFromNetwork.getDelivery_fee() ? restaurantFromNetwork.getDelivery_fee() : restaurantDb.getDelivery_fee());
                                } else {
                                    restaurantsMap.put(id, restaurantFromNetwork);
                                }
                            }
                            ArrayList<Restaurant> restaurants = new ArrayList<>(restaurantsMap.values());
                            saveRestaurants(restaurants);
                        }, throwable -> Log.e(MainViewModel.class.getSimpleName(), throwable.toString())));
    }

    private void updateRestaurantMap(List<Restaurant> restaurants) {
        for (Restaurant restaurant : restaurants) {
            if (!restaurantsMap.containsKey(restaurant.getId())) {
                restaurantsMap.put(restaurant.getId(), restaurant);
            }
        }
    }

    private void saveRestaurants(List<Restaurant> restaurants) {
        FlowManager.getDatabase(DoorDashDb.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        (ProcessModelTransaction.ProcessModel<Restaurant>) (restaurant, Object)
                                -> restaurant.save()).addAll(restaurants).build())
                .error((transaction, error) -> {
                    Log.e(MainViewModel.class.getSimpleName(), error.toString());
                })
                .success(transaction -> {
                    Log.v(MainViewModel.class.getSimpleName(), "Restaurant Table: SUCCESS");
                })
                .build()
                .execute();
    }

    public Observable<List<Restaurant>> restaurantLoadedEvent(){
        return restaurantSubscriber.asObservable();
    }

    public Observable<Boolean> progressBarEvent(){
        return progressBarSubscriber.asObservable();
    }

    public void clearSubscriptions() {
        compositeSubscription.clear();
    }

}
