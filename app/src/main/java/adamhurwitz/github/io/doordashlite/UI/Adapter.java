package adamhurwitz.github.io.doordashlite.UI;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import adamhurwitz.github.io.doordashlite.MainViewModel;
import adamhurwitz.github.io.doordashlite.R;
import adamhurwitz.github.io.doordashlite.dbflow.Restaurant;
import rx.Observable;
import rx.subjects.ReplaySubject;

/**
 * Created by ahurwitz on 7/7/17.
 */

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private Context context;
    private ViewHolder viewHolder;

    public Adapter(Context context){
        this.context = context;
    }
    private List<Restaurant> restaurants = new ArrayList<>();

    private ReplaySubject<Pair<Integer, Restaurant>> onFavoriteSubscriber = ReplaySubject.create();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_cell, parent, false);
        viewHolder = new ViewHolder(view, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bind(restaurants.get(position));
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void addItems(List<Restaurant> restaurants){
        this.restaurants.clear();
        this.restaurants.addAll(restaurants);
        notifyDataSetChanged();
    }

    public Observable<Pair<Integer, Restaurant>> onFavoriteEvent(){
        return onFavoriteSubscriber.asObservable();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fav:
                int position = (Integer) v.getTag(viewHolder.POSITION_TAG);
                Restaurant restaurant = (Restaurant) v.getTag(viewHolder.RESTAURANT_TAG);
                onFavoriteSubscriber.onNext(new Pair<>(position, restaurant));
                break;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
        public TextView title;
        public TextView description;
        public ImageView fav;
        public TextView status;
        int POSITION_TAG = 0;
        int RESTAURANT_TAG = 0;

        public ViewHolder(View itemView, View.OnClickListener onClickListener) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            fav = (ImageView) itemView.findViewById(R.id.fav);
            status = (TextView) itemView.findViewById(R.id.status);
            POSITION_TAG = itemView.getId();
            RESTAURANT_TAG = img.getId();
            fav.setOnClickListener(onClickListener);
        }

        public void bind(Restaurant restaurant){
            Glide.with(context)
                    .load(restaurant.getCover_img_url())
                    .into(img);
            title.setText(restaurant.getName());
            description.setText(restaurant.getDescription());
            status.setText(restaurant.getStatus());
            fav.setBackground(MainViewModel.isFavorite(restaurant.getId())
                    ? context.getDrawable(R.drawable.ic_favorite_black_24dp)
                    : context.getDrawable(R.drawable.ic_favorite_border_black_24dp));
            fav.setTag(POSITION_TAG, getLayoutPosition());
            fav.setTag(RESTAURANT_TAG, restaurant);
        }

    }
}
