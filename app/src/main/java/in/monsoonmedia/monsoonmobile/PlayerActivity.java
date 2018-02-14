package in.monsoonmedia.monsoonmobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.Video;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener {

    private static final int RECOVERY_REQUEST = 1;
    private static final String API_KEY = "AIzaSyAhPbII6mdo79M69BEeI69yD0gW2tiI9CY";
    private YouTubePlayerView youTubePlayerView;
    private String videoId;
    private String playlistTitle;
    private String title;
    private String description;
    private String playlistId;
    private TextView textViewCurrentLocation;
    private TextView textViewPlaylistTitle;
    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewLikesCount;
    private TextView textViewDislikesCount;
    private TextView textViewViewsCount;
    private TextView textViewPublishedAt;
    private NonScrollListView listViewRemainingVideos;
    private NonScrollListView listViewComments;
    private ImageButton imageButtonLike;
    private ImageButton imageButtonDislike;
    private ImageButton imageButtonShare;
    ProgressDialog progressDialog;
    YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        progressDialog = new ProgressDialog(this);
        videoId = getIntent().getStringExtra("videoId");
        playlistTitle = getIntent().getStringExtra("playlistTitle");
        title = getIntent().getStringExtra("videoTitle");
        description = getIntent().getStringExtra("videoDescription");
        playlistId = getIntent().getStringExtra("playlistId");

        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        textViewCurrentLocation = (TextView) findViewById(R.id.textViewCurrentLocation);
        textViewPlaylistTitle = (TextView) findViewById(R.id.textViewPlaylistTitle);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textViewLikesCount = (TextView) findViewById(R.id.textViewLikesCount);
        textViewDislikesCount = (TextView) findViewById(R.id.textViewDislikesCount);
        textViewViewsCount = (TextView) findViewById(R.id.textViewViewsCount);
        textViewPublishedAt = (TextView) findViewById(R.id.textViewPublishedAt);
        textViewPlaylistTitle.setText(playlistTitle.toUpperCase());
        textViewTitle.setText(title);
        textViewDescription.setText(description);
        listViewRemainingVideos = (NonScrollListView) findViewById(R.id.listViewRemainingVideos);
        listViewComments = (NonScrollListView) findViewById(R.id.listViewCommentThreads);
        imageButtonLike = (ImageButton) findViewById(R.id.imageButtonLike);
        imageButtonDislike = (ImageButton) findViewById(R.id.imageButtonDislike);
        imageButtonShare = (ImageButton) findViewById(R.id.imageButtonShare);
        imageButtonLike.setOnClickListener(this);
        imageButtonDislike.setOnClickListener(this);
        imageButtonShare.setOnClickListener(this);

        String locationText = textViewCurrentLocation.getText() + "/" + playlistTitle;
        //Set current location
        textViewCurrentLocation.setText(locationText);
        if (videoId == null) {
            new LoadFirstVideo().execute(playlistId);
        } else {
            init();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (youTubePlayer == null) {
            getYouTubePlayerProvider();
        }
        else {
            youTubePlayer.cueVideo(videoId);
        }
    }

    private void init() {
        youTubePlayerView.initialize(YouTubeHelper.API_KEY, this);
        new GetRatingTask().execute(videoId);
        new GetStatisticsTask().execute(videoId);
        new LoadRemainingVideosTask().execute(playlistId, videoId);
        new LoadCommentsTask().execute(videoId);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean wasRestored) {

        this.youTubePlayer = youTubePlayer;

        if (!wasRestored) {
            youTubePlayer.cueVideo(videoId);
//            youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
//            youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST);
        } else {
            String error = youTubeInitializationResult.toString();
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            getYouTubePlayerProvider().initialize(API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubePlayerView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButtonLike) {
            new RateTask(videoId, YouTubeHelper.LIKE).execute();
        } else if (v.getId() == R.id.imageButtonDislike) {
            new RateTask(videoId, YouTubeHelper.DISLIKE).execute();
        } else if (v.getId() == R.id.imageButtonShare) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, "https://youtu.be/" + videoId);
            startActivity(Intent.createChooser(share, "Share Video"));
        }
    }

    private class LoadCommentsTask extends AsyncTask<String, Void, List<CommentThread>> {

        @Override
        protected List<CommentThread> doInBackground(String... params) {
            List<CommentThread> commentThreadList = null;
            try {
                commentThreadList = YouTubeHelper.getInstance().getCommentThreadsList(videoId);
                CommentThread item = commentThreadList.get(0);
                item.getSnippet().getTopLevelComment().getSnippet().getAuthorDisplayName();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return commentThreadList;
        }

        @Override
        protected void onPostExecute(List<CommentThread> commentThreadList) {
//            ArrayList<String> commentsList = new ArrayList();
//            for (CommentThread commentThread : commentThreadList) {
//                CommentSnippet snippet = commentThread.getSnippet().getTopLevelComment().getSnippet();
//                commentsList.add(snippet.getTextOriginal() + " : " + snippet.getAuthorDisplayName());
//            }
//            listViewComments.setAdapter(new ArrayAdapter(PlayerActivity.this, android.R.layout.simple_list_item_1, commentsList));
            listViewComments.setAdapter(new CommentsListAdapter(PlayerActivity.this, R.layout.item_comment, commentThreadList));
//            CommentsListAdapter commentsListAdapter = (CommentsListAdapter) listViewComments.getAdapter();
//            commentsListAdapter.testMethod();


//            Helper.setListViewSize(listViewComments);
        }
    }

    private class LoadFirstVideo extends AsyncTask<String, Void, PlaylistItem> {

        @Override
        protected PlaylistItem doInBackground(String... params) {
            String playlistId = params[0];
            PlaylistItem firstVideo = null;
            try {
                firstVideo = YouTubeHelper.getInstance().getPlaylistItemsList(playlistId, 1l).get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return firstVideo;
        }


        @Override
        protected void onPostExecute(PlaylistItem firstVideo) {
            videoId = firstVideo.getSnippet().getResourceId().getVideoId();
            title = firstVideo.getSnippet().getTitle();
            description = firstVideo.getSnippet().getDescription();
            textViewTitle.setText(title);
            textViewDescription.setText(description);
            init();
        }
    }


    private class LoadRemainingVideosTask extends AsyncTask<String, Void, List<Video>> {

        @Override
        protected List<Video> doInBackground(String... params) {
            String playlistId = params[0];
            String currentVideoId = params[1];
            List<Video> remainingVideosList = null;
            try {
                remainingVideosList = YouTubeHelper.getInstance().getRemainingVideosList(playlistId, currentVideoId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return remainingVideosList;
        }


        @Override
        protected void onPostExecute(final List<Video> remainingVideosList) {
            listViewRemainingVideos.setAdapter(new VideosListAdapter(PlayerActivity.this, R.layout.item_video, remainingVideosList));
//            Helper.setListViewSize(listViewRemainingVideos);
            listViewRemainingVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (Helper.isConnectedToInternet(PlayerActivity.this)) {
                        Video currentVideo = remainingVideosList.get(position);
                        Intent intent = new Intent(PlayerActivity.this, PlayerActivity.class);
                        intent.putExtra("playlistId", playlistId);
                        intent.putExtra("playlistTitle", playlistTitle);
                        intent.putExtra("videoId", currentVideo.getId());
                        intent.putExtra("videoTitle", currentVideo.getSnippet().getTitle());
                        intent.putExtra("videoDescription", currentVideo.getSnippet().getDescription());
                        startActivity(intent);
                    } else {
                        Toast.makeText(PlayerActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
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
            if (rating != null) {
                if (rating.equals(YouTubeHelper.LIKE)) {
                    imageButtonLike.setBackgroundResource(R.drawable.icon_like_active);
                    imageButtonDislike.setBackgroundResource(R.drawable.icon_dislike_inactive);
                } else if (rating.equals(YouTubeHelper.DISLIKE)) {
                    imageButtonDislike.setBackgroundResource(R.drawable.icon_dislike_active);
                    imageButtonLike.setBackgroundResource(R.drawable.icon_like_inactive);
                }
            }
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
            if (rating != null) {
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

    private class GetStatisticsTask extends AsyncTask<String, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... params) {
            String videoId = params[0];
            HashMap<String, String> result = null;
            try {
                result = YouTubeHelper.getInstance().getCounts(videoId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> statistics) {
            String likeCount = statistics.get("likeCount");
            String dislikeCount = statistics.get("dislikeCount");
            String viewCount = statistics.get("viewCount");
            String publishedAt = statistics.get("publishedAt");
            textViewLikesCount.setText(likeCount);
            textViewDislikesCount.setText(dislikeCount);
            textViewViewsCount.setText(viewCount + " views");
            textViewPublishedAt.setText(publishedAt);
        }
    }
}
