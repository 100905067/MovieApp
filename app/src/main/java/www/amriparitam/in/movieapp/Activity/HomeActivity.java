package www.amriparitam.in.movieapp.Activity;

import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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
import www.amriparitam.in.movieapp.Model.Movie;
import www.amriparitam.in.movieapp.Model.MoviesResponse;
import www.amriparitam.in.movieapp.Adapter.MovieAdapter;
import www.amriparitam.in.movieapp.R;

public class HomeActivity extends AppCompatActivity implements MovieAdapter.MovieItemListener {

    private List<Movie> movieList = new ArrayList<>();
    private MovieAdapter movieAdapter;
    private MovieController controller;
    private String TAG = HomeActivity.class.getSimpleName();
    private int currentPage = 1;
    private int totalPage = 0;
    private LinearLayoutManager layoutManager;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private ListView mDrawerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        RecyclerView movieListView = (RecyclerView) findViewById(R.id.movieListView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navList);

        setupDrawer();
        addDrawerItems();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        layoutManager = new LinearLayoutManager(this);
        movieListView.setLayoutManager(layoutManager);

        mActivityTitle = getTitle().toString();

        controller = MovieController.getInstance();

        movieAdapter = new MovieAdapter(getBaseContext(), movieList, this, R.layout.view_cell);
        movieListView.setAdapter(movieAdapter);

        populatePopularMovies(currentPage);

        /*movieListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(layoutManager.findLastVisibleItemPosition() == (movieList.size()-1)) {
                    if(currentPage==totalPage) {
                        Toast.makeText(getBaseContext(), R.string.endOfList, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    populatePopularMovies(++currentPage);
                } else if(layoutManager.findFirstVisibleItemPosition() == 0) {
                    if(currentPage==0) {
                        resetAdapter();
                        return;
                    }
                    populatePopularMovies(--currentPage);
                }
            }
        });*/
    }

    private void addDrawerItems() {
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
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
        return true;
    }

    /*private void resetAdapter() {
        currentPage = 1;
        totalPage = 0;
        movieList.clear();
        populatePopularMovies(currentPage);
    }*/

    private void populatePopularMovies(int pageIndex) {
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
                        movieList.addAll(value.getResults());
                        movieAdapter.notifyDataSetChanged();
                        currentPage = value.getPage();
                        totalPage =  value.getTotalPages();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.e(TAG, e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onItemSelected(Movie movie, int position) {
        Toast.makeText(getBaseContext(), "Movie clicked at position :"+position, Toast.LENGTH_SHORT).show();
    }

    public Scheduler getScheduler() {
        return BackgroundWorker.get().getScheduler();
    }
}
