package adamhurwitz.github.io.doordashlite;

import android.support.annotation.NonNull;
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

import adamhurwitz.github.io.doordashlite.Network.Repository;
import adamhurwitz.github.io.doordashlite.dbflow.DoorDashDb;
import adamhurwitz.github.io.doordashlite.dbflow.Restaurant;
import adamhurwitz.github.io.doordashlite.dbflow.Restaurant_Table;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ahurwitz on 7/9/17.
 */

public class MainViewModel {

    @Inject
    protected Repository repository;

    private MainView mainView;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private Map<Integer, Restaurant> restaurantsMap = new HashMap<>();

    public MainViewModel(@NonNull MainView mainView) {
        this.mainView = mainView;
        DoorDashLiteApplication.getApp().getDataComponent().inject(this);
    }

    public interface MainView {
        void addItems(List<Restaurant> restaurants);

        void setProgressBar(boolean status);
    }

    public void initData() {
        compositeSubscription.add(
                getRestaurantsFromDb()
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(restaurantsDb -> {
                            if (restaurantsDb.isEmpty()) {
                                populateDb();
                            } else {
                                updateDb();
                            }
                        }, throwable -> Log.v(MainViewModel.class.getSimpleName(), throwable.toString())));
    }

    private void populateDb() {
        compositeSubscription.add(
                repository.getRestaurantsFromNetwork()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(restaurantsFromNetwork -> Observable.from(restaurantsFromNetwork))
                        .doOnCompleted(() -> {
                            mainView.setProgressBar(false);
                            List<Restaurant> restaurants = new ArrayList<>(restaurantsMap.values());
                            mainView.addItems(restaurants);
                            saveRestaurants(restaurants);
                        })
                        .subscribe(restaurantFromNetwork -> {
                                    mainView.setProgressBar(true);
                                    restaurantsMap.put(restaurantFromNetwork.getId(), restaurantFromNetwork);
                                },
                                throwable -> Log.v(MainViewModel.class.getSimpleName(), throwable.toString())));
    }

    private void updateDb() {
        compositeSubscription.add(
                Observable.zip(repository.getRestaurantsFromNetwork(), getRestaurantsFromDb(),
                        (networkRestaurants, dbRestaurants) -> new Pair<>(networkRestaurants, dbRestaurants))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnCompleted(() -> getRestaurantsFromDb()
                                .subscribe(restaurants -> {
                                    mainView.setProgressBar(false);
                                    mainView.addItems(restaurants);
                                }))
                        .subscribe(pair -> {
                            mainView.setProgressBar(true);
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
                        }, throwable -> {
                            Log.e(MainViewModel.class.getSimpleName(), throwable.toString());
                        }));
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
                        (ProcessModelTransaction.ProcessModel<Restaurant>) (restaurant)
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

    public static Observable<List<Restaurant>> getRestaurantsFromDb() {
        return Observable.just(SQLite.select()
                .from(Restaurant.class)
                .orderBy(Restaurant_Table.isFavorite, false)
                .queryList());
    }

    public static boolean isFavorite(int id) {
        return SQLite.select(Restaurant_Table.isFavorite)
                .from(Restaurant.class)
                .where(Restaurant_Table.id.is(id))
                .querySingle()
                .isFavorite();
    }

    public void clearSubscriptions() {
        compositeSubscription.clear();
    }

}
