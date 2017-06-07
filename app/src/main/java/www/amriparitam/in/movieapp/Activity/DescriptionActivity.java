package www.amriparitam.in.movieapp.Activity;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import www.amriparitam.in.movieapp.Controller.MovieController;
import www.amriparitam.in.movieapp.Core.BackgroundWorker;
import www.amriparitam.in.movieapp.Model.Movie;
import www.amriparitam.in.movieapp.Model.MoviesResponse;
import www.amriparitam.in.movieapp.Model.Video;
import www.amriparitam.in.movieapp.Model.VideoResponse;
import www.amriparitam.in.movieapp.R;

public class DescriptionActivity extends AppCompatActivity {
    private static final String TAG = DescriptionActivity.class.getSimpleName();
    ImageView backImage;
    ImageView frontImage;
    TextView title;
    TextView overview;
    ImageButton play_button;

    private MovieController controller;

    private String base_url = "https://image.tmdb.org/t/p/w500";
    private String base_url_front = "https://image.tmdb.org/t/p/w150";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        backImage = (ImageView) findViewById(R.id.backImage);
        frontImage = (ImageView) findViewById(R.id.frontImage);
        title = (TextView) findViewById(R.id.originalTitle);
        overview = (TextView) findViewById(R.id.overview);
        play_button = (ImageButton) findViewById(R.id.playButton);

        Intent intent = getIntent();
        final Movie movie = intent.getParcelableExtra("movie_detail");

        Glide.with(getBaseContext()).load(base_url_front + movie.getPosterPath()).into(frontImage);
        title.setText(movie.getOriginalTitle());
        overview.setText(movie.getOverview());
        Glide.with(getBaseContext()).load(base_url + movie.getBackdropPath()).into(backImage);

        controller = MovieController.getInstance(getBaseContext());

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "movieId :"+movie.getId());
                playVideo(movie.getId());
            }
        });
    }

    public void playVideo(int id) {
        controller.getVideoUrl(Integer.toString(id))
                .subscribeOn(getScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VideoResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(VideoResponse value) {
                        Log.e(TAG, "Results :"+value.toString());
                        List<Video> video = value.getResults();
                        String video_url = video.get(0).getKey();
                        watchVideo(video_url);
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

    public void watchVideo(String url) {
        Log.e(TAG, "Amrita url :"+url);
        Intent intent = new Intent(this, VideoPlay.class);
        intent.putExtra("video_path", url);
        startActivity(intent);
    }

    public Scheduler getScheduler() {
        return BackgroundWorker.get().getScheduler();
    }
}
