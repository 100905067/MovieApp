package www.amriparitam.in.movieapp.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import www.amriparitam.in.movieapp.Controller.MovieController;
import www.amriparitam.in.movieapp.Core.BackgroundWorker;
import www.amriparitam.in.movieapp.Core.NoConnectivityException;
import www.amriparitam.in.movieapp.Model.Movie;
import www.amriparitam.in.movieapp.Model.MoviesResponse;
import www.amriparitam.in.movieapp.Adapter.MovieAdapter;
import www.amriparitam.in.movieapp.R;

public class HomeActivity extends AppCompatActivity implements MovieAdapter.MovieItemListener {

    private List<Movie> movieList = new ArrayList<>();
    private MovieAdapter movieAdapter;
    private RecyclerView movieListView;
    private SearchView searchView;
    private MovieController controller;
    private String TAG = HomeActivity.class.getSimpleName();
    private int currentPage = 1;
    private int totalPage = 0;
    private LinearLayoutManager layoutManager;
    private BroadcastReceiver networkMonitor = null;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private ListView mDrawerListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) findViewById(R.id.navList);

        initViews();

        setupDrawer();
        addDrawerItems();

        navigationView();

        controller = MovieController.getInstance(getBaseContext());

        movieListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (layoutManager.findLastVisibleItemPosition() == (movieList.size() - 1)) {
                    if (currentPage == totalPage) {
                        Toast.makeText(getBaseContext(), R.string.endOfList, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    populatePopularMovies(++currentPage);
                } else if(layoutManager.findFirstVisibleItemPosition() == 0) {
                    if(currentPage==0) {
                        return;
                    }
                    populatePopularMovies(--currentPage);
                }
            }
        });

        networkMonitor = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isInitialStickyBroadcast()) {
                    // Ignore this call to onReceive, as this is the sticky broadcast
                } else {
                    onRefresh();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "Amrita in resume method");
        onRefresh();
        registerReceiver(networkMonitor, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    protected void onRefresh() {
        movieList.clear();
        movieAdapter.notifyDataSetChanged();
        populatePopularMovies(currentPage);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkMonitor);
    }

    public void initViews() {
        movieListView = (RecyclerView) findViewById(R.id.movieListView);
        movieListView.setHasFixedSize(true); //to increase performance
        layoutManager = new LinearLayoutManager(this);
        movieListView.setLayoutManager(layoutManager);
        movieAdapter = new MovieAdapter(getBaseContext(), movieList, this, R.layout.view_cell);
        movieListView.setAdapter(movieAdapter);
    }

    public void navigationView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mActivityTitle = getTitle().toString();
    }

    private void addDrawerItems() {
    }

    private void setupDrawer() {
        List<String> mDrawerList = new ArrayList<String>();
        mDrawerList.add("About Me");
        mDrawerListView.setAdapter(new ArrayAdapter<String>(this,
               android.R.layout.simple_list_item_1, mDrawerList));

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) {
                    Intent intent = new Intent(HomeActivity.this, AboutMe.class);
                    startActivity(intent);
                }
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Home");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //without this Navigation Drawer just show back arrow no hamburger
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);

        MenuItem search = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG, "Amrita in searchview");
                movieAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }


    private void populatePopularMovies(int pageIndex) {
        Log.e(TAG, "Amrita :" + pageIndex);
        controller.getPopularMovies(pageIndex)
                .subscribeOn(getScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MoviesResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(MoviesResponse value) {
                        movieList.clear();
                        List<Movie> results = value.getResults();
                        for(int i=0;i<results.size();i++) {
                            movieList.add(results.get(i));
                        }
                        movieAdapter.notifyDataSetChanged();
                        currentPage = value.getPage();
                        totalPage = value.getTotalPages();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (e instanceof NoConnectivityException) {
                            Toast.makeText(getBaseContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onItemSelected(Movie movie, int position, ImageView imageView) {
        Intent intent = new Intent(this, DescriptionActivity.class);
        intent.putExtra("movie_detail", movie);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, imageView, "posterImage");
        startActivity(intent);

    }

    public Scheduler getScheduler() {
        return BackgroundWorker.get().getScheduler();
    }

    public void strictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                // alternatively .detectAll() for all detectable problems
                .penaltyLog()
                .penaltyDeath()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                // alternatively .detectAll() for all detectable problems
                .penaltyLog()
                .penaltyDeath()
                .build());
    }
}
