package in.monsoonmedia.monsoonmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
//    private static final String API_KEY = "AIzaSyD3eGOHMJu8fDU1hq78DDegW48htrUhb-8";
    private static final String API_KEY = "";
    private YouTubePlayerView youTubePlayerView;
    private String videoId;
    private String description;
    private String title;
    private TextView textViewTitle;
    private TextView textViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        videoId = getIntent().getStringExtra("videoId");
        title = getIntent().getStringExtra("videoTitle");
        description = getIntent().getStringExtra("videoDescription");
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textViewTitle.setText(title);
        textViewDescription.setText(description);
        youTubePlayerView.initialize(API_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if(!wasRestored) {
            youTubePlayer.cueVideo(videoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if(youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST);
        } else {
            String error = youTubeInitializationResult.toString();
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RECOVERY_REQUEST) {
            getYouTubePlayerProvider().initialize(API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubePlayerView;
    }
}
