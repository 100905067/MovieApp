package www.amriparitam.in.movieapp.Controller;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import www.amriparitam.in.movieapp.Model.MoviesResponse;
import www.amriparitam.in.movieapp.Model.VideoResponse;

/**
 * Created by Amrita Pritam on 4/14/2017.
 */

public interface MovieApi {
    @GET("movie/now_playing")
    Observable<MoviesResponse> getNowPlayingMovies(@Query("api_key") String apiKey);
    @GET("movie/popular")
    Observable<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("page") int pageIndex);
    @GET("movie/top_rated")
    Observable<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);
    @GET("movie/upcoming")
    Observable<MoviesResponse> getUpcomingMovies(@Query("api_key") String apiKey);
    @GET("movie/{movie_id}/videos")
    Observable<VideoResponse> getVideoUrl(@Path("movie_id") String movieId, @Query("api_key") String apiKey);
}
