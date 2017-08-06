package adamhurwitz.github.io.doordashlite;

import java.util.ArrayList;
import java.util.List;

import adamhurwitz.github.io.doordashlite.Network.Repository;
import adamhurwitz.github.io.doordashlite.Network.Service;
import adamhurwitz.github.io.doordashlite.dbflow.Restaurant;
import rx.Observable;

/**
 * Created by ahurwitz on 7/10/17.
 */

public class RepositoryMock extends Repository {

    public RepositoryMock(Service service) {
        super(service);
    }


    @Override
    public Observable<List<Restaurant>> getRestaurantsFromNetwork(String path,
                                                                  double lat, double lng) {

        List<Restaurant> restaurantList = new ArrayList<>();

        Restaurant adamsRestaurant = new Restaurant();
        adamsRestaurant.setId(1);
        adamsRestaurant.setName("Adam's Cafe");
        adamsRestaurant.setDescription("Healthy and tasty food");
        adamsRestaurant.setCover_img_url("http://www.hardrock.com/cafes/amsterdam/files/2308/LegendsRoom.jpg");
        adamsRestaurant.setStatus("9 mins");
        adamsRestaurant.setDelivery_fee(0);
        adamsRestaurant.setFavorite(false);
        restaurantList.add(adamsRestaurant);

        Restaurant ozsRestaurant = new Restaurant();
        ozsRestaurant.setId(2);
        ozsRestaurant.setName("Oz's Cafe");
        ozsRestaurant.setDescription("Israeli cuisine");
        ozsRestaurant.setCover_img_url("http://www.langeasy.com/hebrew7/images-tlv1/cafe.jpg");
        ozsRestaurant.setStatus("Pre-order for tomorrow");
        ozsRestaurant.setDelivery_fee(0);
        ozsRestaurant.setFavorite(true);
        restaurantList.add(adamsRestaurant);

        return Observable.just(restaurantList);
    }
}
