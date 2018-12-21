package adamhurwitz.github.io.doordashlite.UI;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;

import java.util.List;

import adamhurwitz.github.io.doordashlite.MainViewModel;
import adamhurwitz.github.io.doordashlite.R;
import adamhurwitz.github.io.doordashlite.RxHelpers;
import adamhurwitz.github.io.doordashlite.dbflow.Restaurant;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity implements MainViewModel.MainView, SwipeRefreshLayout.OnRefreshListener {

    private MainViewModel mainView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private Adapter adapter;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainView = new MainViewModel(this);
        mainView.initData();
        initViews();
        onFavoriteEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.clear();
        mainView.clearSubscriptions();
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        progressBar = findViewById(R.id.pbLoading);
        recyclerView = findViewById(R.id.recyclerView);

        initSwipeToRefresh();
        initRecyclerViewAdapter();
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    private void initSwipeToRefresh() {
        swipeRefreshLayout.setColorSchemeResources((R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(() -> mainView.getRestauarantsFromDb());
    }

    private void initRecyclerViewAdapter() {
        adapter = new Adapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void onFavoriteEvent() {
        compositeSubscription.add(adapter.onFavoriteEvent()
                .compose(RxHelpers.IOAndMainThreadSchedulers())
                .subscribe(pair -> {
                    pair.second.setFavorite(mainView.isFavorite(pair.second.getId()) ? false : true);
                    pair.second.save();
                    adapter.notifyItemChanged(pair.first);
                }, throwable -> Log.e(MainActivity.class.getName(), throwable.toString())));
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void setProgressBar(boolean status) {
        progressBar.setVisibility(status ? ProgressBar.VISIBLE : ProgressBar.INVISIBLE);
    }

    @Override
    public void setSwipeRefresh(boolean status) {
        swipeRefreshLayout.setRefreshing(status);
    }

    @Override
    public void addItems(List<Restaurant> restaurants) {
        adapter.addItems(restaurants);
        adapter.notifyDataSetChanged();
    }
}
