package www.amriparitam.in.movieapp.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        RecyclerView movieListView = (RecyclerView) findViewById(R.id.movieListView);
        layoutManager = new LinearLayoutManager(this);
        movieListView.setLayoutManager(layoutManager);

        controller = MovieController.getInstance();

        movieAdapter = new MovieAdapter(getBaseContext(), movieList, this, R.layout.view_cell);
        movieListView.setAdapter(movieAdapter);

        populatePopularMovies(currentPage);

        movieListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        });
    }

    private void resetAdapter() {
        currentPage = 1;
        totalPage = 0;
        movieList.clear();
        populatePopularMovies(currentPage);
    }

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
