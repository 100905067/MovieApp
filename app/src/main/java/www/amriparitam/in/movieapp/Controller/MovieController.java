package www.amriparitam.in.movieapp.Controller;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import www.amriparitam.in.movieapp.Core.BackgroundWorker;
import www.amriparitam.in.movieapp.Core.MovieCore;
import www.amriparitam.in.movieapp.Model.MoviesResponse;

/**
 * Created by Amrita Pritam on 4/14/2017.
 */

public class MovieController {

    private static volatile MovieController movieController;
    private MovieApi movieApi;
    private String api_key = "69a5381a82a19e78a9c7a3ca2e0f0230";

    private MovieController() {
        movieApi = MovieCore.getRetrofit().create(MovieApi.class);
    }

    public synchronized static MovieController getInstance() {
        if(movieController == null) {
            movieController = new MovieController();
        }
        return movieController;
    }

    public synchronized Observable<MoviesResponse> getNowPlayingMovies() {
        Observable<MoviesResponse> moviesResponseObservable = movieApi.getNowPlayingMovies(api_key);
        return moviesResponseObservable;
    }

    public synchronized Observable<MoviesResponse> getPopularMovies(int pageIndex) {
        Observable<MoviesResponse> moviesResponseObservable = movieApi.getPopularMovies(api_key, pageIndex);
        return moviesResponseObservable;
    }

    public synchronized Observable<MoviesResponse> getTopRatedMovies() {
        Observable<MoviesResponse> moviesResponseObservable = movieApi.getTopRatedMovies(api_key);
        return moviesResponseObservable;
    }

    public synchronized Observable<MoviesResponse> getUpcomingMovies() {
        Observable<MoviesResponse> moviesResponseObservable = movieApi.getUpcomingMovies(api_key);
        return moviesResponseObservable;
    }
}
