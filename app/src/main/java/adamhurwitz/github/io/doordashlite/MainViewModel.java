package adamhurwitz.github.io.doordashlite;

import android.support.annotation.NonNull;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import javax.inject.Inject;

import adamhurwitz.github.io.doordashlite.Network.Repository;
import adamhurwitz.github.io.doordashlite.UI.MainActivity;
import adamhurwitz.github.io.doordashlite.dbflow.Restaurant;
import adamhurwitz.github.io.doordashlite.dbflow.Restaurant_Table;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ahurwitz on 7/9/17.
 */

public class MainViewModel {

    @Inject
    protected Repository repository;

    private static final String LOG_TAG = MainViewModel.class.getSimpleName();
    private static final String PATH = "restaurant";
    private static final double LAT = 37.422740;
    private static final double LNG = -122.139956;

    private MainView mainView;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public MainViewModel(@NonNull MainView mainView) {
        this.mainView = mainView;
        DoorDashLiteApplication.getApp().getDataComponent().inject(this);
    }

    public interface MainView {

        void addItems(List<Restaurant> restaurants);

        void setProgressBar(boolean status);

        void setSwipeRefresh(boolean status);
    }

    public void initData() {
        repository.initData(PATH, LAT, LNG);
        addItemsEvent();
        progressBarEvent();
    }

    private void addItemsEvent() {
        compositeSubscription.add(repository.restaurantLoadedEvent()
                .compose(RxHelpers.MainAndMainSchedulers())
                .subscribe(restaurants -> {
                    mainView.addItems(restaurants);
                }, throwable -> Log.e(LOG_TAG, "Error: " + throwable.toString())));
    }

    private void progressBarEvent() {
        compositeSubscription.add(repository.progressBarEvent()
                .compose(RxHelpers.IOAndMainThreadSchedulers())
                .subscribe(activated -> {
                    mainView.setProgressBar(activated);
                }, throwable -> Log.e(LOG_TAG, "Error: " + throwable.toString())));
    }

    public static boolean isFavorite(int id) {
        return SQLite.select(Restaurant_Table.isFavorite)
                .from(Restaurant.class)
                .where(Restaurant_Table.id.is(id))
                .querySingle()
                .isFavorite();
    }

    public void getRestauarantsFromDb() {
        compositeSubscription.add(repository.getRestaurantsFromDb()
                .compose(RxHelpers.IOAndMainThreadSchedulers())
                .doOnCompleted(() -> {
                    mainView.setSwipeRefresh(false);
                })
                .subscribe(restaurants -> mainView.addItems(restaurants),
                        throwable -> Log.e(MainActivity.class.getSimpleName(), throwable.toString())));
    }

    public void clearSubscriptions() {
        compositeSubscription.clear();
        repository.clearSubscriptions();
    }

}
