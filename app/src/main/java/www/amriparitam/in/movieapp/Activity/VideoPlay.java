package www.amriparitam.in.movieapp.Activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import www.amriparitam.in.movieapp.R;

public class VideoPlay extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private String developerKey = "AIzaSyCWDBznkE5d-UFhUItqSwfTkDAo-Om552s";
    private String videoUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        Intent videoPlay = getIntent();
        videoUrl = videoPlay.getStringExtra("video_path");

        YouTubePlayerView youTubeView = (YouTubePlayerView)
                findViewById(R.id.videoView);
        youTubeView.initialize(developerKey, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            youTubePlayer.cueVideo(videoUrl);
            youTubePlayer.setFullscreen(true);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
    }
}
