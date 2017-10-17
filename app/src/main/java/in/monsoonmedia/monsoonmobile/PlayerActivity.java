package in.monsoonmedia.monsoonmobile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.IOException;
import java.util.HashMap;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener{

    private static final int RECOVERY_REQUEST = 1;
    private static final String API_KEY = "AIzaSyAhPbII6mdo79M69BEeI69yD0gW2tiI9CY";
    private YouTubePlayerView youTubePlayerView;
    private String videoId;
    private String description;
    private String title;
    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewLikesCount;
    private TextView textViewDislikesCount;
    private ImageButton imageButtonLike;
    private ImageButton imageButtonDislike;
    private ImageButton imageButtonShare;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        progressDialog = new ProgressDialog(this);
        videoId = getIntent().getStringExtra("videoId");
        title = getIntent().getStringExtra("videoTitle");
        description = getIntent().getStringExtra("videoDescription");
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textViewLikesCount = (TextView) findViewById(R.id.textViewDislikesCount);
        textViewDislikesCount = (TextView) findViewById(R.id.textViewDislikesCount);
        textViewTitle.setText(title);
        textViewDescription.setText(description);
        youTubePlayerView.initialize(API_KEY, this);
        imageButtonLike = (ImageButton) findViewById(R.id.imageButtonLike);
        imageButtonDislike = (ImageButton) findViewById(R.id.imageButtonDislike);
        imageButtonShare = (ImageButton) findViewById(R.id.imageButtonShare);

        imageButtonLike.setOnClickListener(this);
        imageButtonDislike.setOnClickListener(this);
        imageButtonShare.setOnClickListener(this);

        init();
    }

    private void init() {
        new GetRatingTask().execute(videoId);
        try {
            HashMap<String, String> counts = YouTubeHelper.getInstance().getCounts(videoId);
            textViewLikesCount.setText(counts.get("likeCount"));
            textViewDislikesCount.setText(counts.get("dislikeCount"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imageButtonLike) {
            new RateTask(videoId, YouTubeHelper.LIKE).execute();
        } else if(v.getId() == R.id.imageButtonDislike) {
            new RateTask(videoId, YouTubeHelper.DISLIKE).execute();
        } else if(v.getId() == R.id.imageButtonShare) {
        }
    }

    private class RateTask extends AsyncTask<Void, Void, Void> {

        String videoId;
        String rating;

        RateTask(String videoId, String rating) {
            this.videoId = videoId;
            this.rating = rating;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                YouTubeHelper.getInstance().rateVideo(videoId, rating);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new GetRatingTask().execute(videoId);
        }
    }

    private class GetRatingTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String videoId = params[0];
            try {
                return YouTubeHelper.getInstance().getRating(videoId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String rating) {
            if(rating != null) {
                if (rating.equals(YouTubeHelper.LIKE)) {
                    Toast.makeText(PlayerActivity.this, "Liked!", Toast.LENGTH_SHORT).show();
                    imageButtonLike.setBackgroundResource(R.drawable.icon_like_active);
                    imageButtonDislike.setBackgroundResource(R.drawable.icon_dislike_inactive);
                } else if (rating.equals(YouTubeHelper.DISLIKE)) {
                    Toast.makeText(PlayerActivity.this, "Disliked!", Toast.LENGTH_SHORT).show();
                    imageButtonDislike.setBackgroundResource(R.drawable.icon_dislike_active);
                    imageButtonLike.setBackgroundResource(R.drawable.icon_like_inactive);
                }
            }

            progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
        }
    }
}
