package in.monsoonmedia.monsoonmobile;


import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ramesh on 25/7/17.
 */

public class YouTubeHelper {
    private GoogleAccountCredential mCredential;
    private YouTube mService;
    public static final String API_KEY = "AIzaSyAhPbII6mdo79M69BEeI69yD0gW2tiI9CY";
    //    private static final String OAUTH_TOKEN = "418285204811-idpv7n1hpparfjjhd82ft4cm4mkhkko2.apps.googleusercontent.com";
    private static final String OAUTH_TOKEN = "418285204811-ntghe83v2qvqi8ub2l16ib4eco8poppg.apps.googleusercontent.com";
    public static final int MAX_RESULTS = 10;
    public static final String CHANNEL_ID = "UCKLN9wGq6WfD2JgJk6P7ODQ";
    public static final String UPLOADS = "UUKLN9wGq6WfD2JgJk6P7ODQ";
    public static final String TRENDING = "PLngLkrCDoLukOOjr5bzs_u7Z8IkQYd5td";
    public static final String ID_MOVIES = "PLngLkrCDoLumAsvgjK1Bw8rTaMMGiGTDV";
    public static final String ID_ART_AND_LITERATURE = "PLngLkrCDoLukezJ8sOMqOFHKbP5ik6cqp";
    public static final String ID_INTERVIEWS = "PLngLkrCDoLukSwLCgi1jzc6M9O8iDB5b5";
    public static final String ID_LIFE_AND_GUIDANCE = "PLngLkrCDoLul-RRJ1k5JbQDwRNRiwbKFR";
    public static final String ID_TALK_SERIOUS = "PLngLkrCDoLun6W2D6zDPiYJ0KQ6f6qEvQ";
    public static final String ID_FUN_FOOD_AND_TRAVEL = "PLngLkrCDoLukUNpZEnb_DDruKOFSWQ4WW";
    static final String LIKE = "like";
    static final String DISLIKE = "dislike";
    private static YouTubeHelper youTubeHelper;


    private YouTubeHelper(GoogleAccountCredential credential) {
        this.mCredential = credential;
        youTubeHelper = this;
        init();
    }

    public static YouTubeHelper getInstance(GoogleAccountCredential credential) {
        if (youTubeHelper == null) {
            return new YouTubeHelper(credential);
        } else {
            return youTubeHelper;
        }
    }

    public static YouTubeHelper getInstance() {
        return youTubeHelper;
    }

    private void init() {
        // Initialize credentials and service object.
//        mCredential = GoogleAccountCredential.usingOAuth2(
//                context, Arrays.asList(SCOPES))
//                .setBackOff(new ExponentialBackOff());

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.youtube.YouTube.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("MonsoonMobile")
                .build();
    }

    public List<CommentThread> getCommentThreadsList(String videoId) throws IOException {
        YouTube.CommentThreads.List commentThreadsListRequest = mService.commentThreads().list("snippet");
        commentThreadsListRequest.setVideoId(videoId);
        commentThreadsListRequest.setOrder("relevance");
        commentThreadsListRequest.setKey(API_KEY);
        commentThreadsListRequest.setMaxResults(5l);
        List<CommentThread> result = commentThreadsListRequest.execute().getItems();
        return result;
    }

    public List<Comment> getCommentsList(String videoId) throws IOException {
        YouTube.Comments.List commentsListRequest = mService.comments().list("snippet");
        commentsListRequest.setId(videoId);
        commentsListRequest.setKey(API_KEY);
        commentsListRequest.setMaxResults(5l);
        List<Comment> commentsList = commentsListRequest.execute().getItems();
        return commentsList;
    }

    public List<Playlist> getCategoryPlaylistsList() throws IOException {
        ArrayList<String> playlistIdsList = new ArrayList<String>();
        playlistIdsList.add(0, ID_MOVIES);
        playlistIdsList.add(1, ID_ART_AND_LITERATURE);
        playlistIdsList.add(2, ID_INTERVIEWS);
        playlistIdsList.add(3, ID_LIFE_AND_GUIDANCE);
        playlistIdsList.add(4, ID_TALK_SERIOUS);
        playlistIdsList.add(5, ID_FUN_FOOD_AND_TRAVEL);

        YouTube.Playlists.List playlistsListRequest = mService.playlists().list("snippet");
        playlistsListRequest.setKey(API_KEY);
        String playlistIds = org.apache.commons.lang3.StringUtils.join(playlistIdsList.toArray(), ",");
        playlistsListRequest.setId(playlistIds);
        return playlistsListRequest.execute().getItems();
    }

    public List<Video> getCategoryVideosList(String categoryPlaylistId) throws IOException {
        List<PlaylistItem> playlistItemsList = getPlaylistItemsList(categoryPlaylistId);
        ArrayList<String> categoryVideoIdsList = new ArrayList<String>();
        for (PlaylistItem playlistItem : playlistItemsList) {
            categoryVideoIdsList.add(playlistItem.getSnippet().getResourceId().getVideoId());
        }

        YouTube.Videos.List videosListRequest = mService.videos().list("snippet,contentDetails,statistics");
        String categoryVideoIds = org.apache.commons.lang3.StringUtils.join(categoryVideoIdsList, ",");
        videosListRequest.setKey(API_KEY);
        videosListRequest.setId(categoryVideoIds);
        List<Video> categoryVideosList = videosListRequest.execute().getItems();
        Log.d("Ids: ", "" + categoryVideoIds);
        return categoryVideosList;
    }
//
//    public String getPlaylistThumbnailUrl(String playlistId) throws IOException {
//        String thumbnailUrl;
//        YouTube.Playlists.List playlistListRequest = mService.playlists().list("snippet");
//        playlistListRequest.setKey(API_KEY);
//        playlistListRequest.setId(playlistId);
//        thumbnailUrl = playlistListRequest.execute().getItems().get(0).getSnippet().getThumbnails().getHigh().getUrl();
//        return thumbnailUrl;
//    }

    public void rateVideo(String videoId, String rating) throws IOException {
        YouTube.Videos.Rate videoRateRequest = mService.videos().rate(videoId, rating);
        videoRateRequest.setKey(API_KEY);
        videoRateRequest.setOauthToken("418285204811-idpv7n1hpparfjjhd82ft4cm4mkhkko2.apps.googleusercontent.com");
        videoRateRequest.execute();
    }

    public HashMap<String, String> getCounts(String videoId) throws IOException {
        HashMap<String, String> counts = new HashMap<String, String>();
        YouTube.Videos.List videoListRequest = mService.videos().list("snippet, statistics");
        videoListRequest.setKey(API_KEY);
        videoListRequest.setId(videoId);
        Video item = videoListRequest.execute().getItems().get(0);
        String likeCount = item.getStatistics().getLikeCount().toString();
        String dislikeCount = item.getStatistics().getDislikeCount().toString();
        String viewCount = item.getStatistics().getViewCount().toString();
        DateTime dateTimePublishedAt = item.getSnippet().getPublishedAt();


        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");
        String dateString = dateTimePublishedAt.toString().substring(0, 10);
        Date date = null;
        String publishedAt = null;
        try {
            date = inputFormat.parse(dateString);
            publishedAt = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        counts.put("likeCount", likeCount);
        counts.put("dislikeCount", dislikeCount);
        counts.put("viewCount", viewCount);
        counts.put("publishedAt", publishedAt);
        return counts;
    }

    public String getRating(String videoID) throws IOException {
        YouTube.Videos.GetRating getRatingRequest = mService.videos().getRating(videoID);
        getRatingRequest.setKey(API_KEY);
        getRatingRequest.setOauthToken("418285204811-idpv7n1hpparfjjhd82ft4cm4mkhkko2.apps.googleusercontent.com");
        return getRatingRequest.execute().getItems().get(0).getRating();
    }

    public List<PlaylistItem> getPlaylistItemsList(String playlistId) throws IOException {
        YouTube.PlaylistItems.List playlistItemsListRequest = mService.playlistItems().list("snippet");
        playlistItemsListRequest.setKey(API_KEY);
        playlistItemsListRequest.setPlaylistId(playlistId);
        playlistItemsListRequest.setMaxResults((long) 10l);
        return playlistItemsListRequest.execute().getItems();
    }

    public List<PlaylistItem> getPlaylistItemsList(String playlistId, long maxResults) throws IOException {
        YouTube.PlaylistItems.List playListItemsListRequest = mService.playlistItems().list("snippet");
        playListItemsListRequest.setPlaylistId(playlistId);
        playListItemsListRequest.setKey(API_KEY);
        playListItemsListRequest.setMaxResults(maxResults);
        return playListItemsListRequest.execute().getItems();
    }

    public List<Video> getVideosList() throws IOException {
        YouTube.Videos.List videosListRequest = mService.videos().list("snippet,contentDetails,statistics");
        ArrayList<String> videoIDsList = getVideoIDsList(UPLOADS, MAX_RESULTS);
        String videoIds = org.apache.commons.lang3.StringUtils.join(videoIDsList, ",");
        videosListRequest.setKey(API_KEY);
        videosListRequest.setId(videoIds);
        List<Video> videosList = videosListRequest.execute().getItems();
        return videosList;
    }

    public List<Video> getRemainingVideosList(String playlistId, String currentVideoId) throws IOException {
        YouTube.Videos.List videosListRequest = mService.videos().list("snippet,contentDetails,statistics");
        ArrayList<String> videoIDsList = getVideoIDsList(playlistId, 6);
        videoIDsList.remove(currentVideoId);
        String videoIds = org.apache.commons.lang3.StringUtils.join(videoIDsList, ",");
        videosListRequest.setKey(API_KEY);
        videosListRequest.setId(videoIds);
        List<Video> videosList = videosListRequest.execute().getItems();
        return videosList;
    }

    public List<Video> getTrendingVideosList() throws IOException {
        YouTube.Videos.List videosListRequest = mService.videos().list("snippet,contentDetails,statistics");
        ArrayList<String> videoIDsList = getVideoIDsList(TRENDING, 3);
        String videoIds = org.apache.commons.lang3.StringUtils.join(videoIDsList, ",");
        videosListRequest.setKey(API_KEY);
        videosListRequest.setId(videoIds);
        List<Video> videosList = videosListRequest.execute().getItems();
        return videosList;
    }

    public List<PlaylistItem> getUploadsItemsList() throws IOException {
//        return getPlaylistItemsList("PLJvP8jNh41cgNyKbY43DrwERzCJeDZwtB", 5l);
//        return getPlaylistItemsList("UU1Z7TQ9jXXiX-k3YanWLUUg");
        return getPlaylistItemsList("UUKLN9wGq6WfD2JgJk6P7ODQ");
    }

//    public List<PlaylistItem> getUploadsItemsList(long limit) throws IOException {
////        return getPlaylistItemsList("PLJvP8jNh41cgNyKbY43DrwERzCJeDZwtB", 5l);
//        return getPlaylistItemsList("UU1Z7TQ9jXXiX-k3YanWLUUg", limit);
//    }

    public ArrayList<String> getVideoIDsList(String playlistId, int maxResults) throws IOException {
        ArrayList<String> videoIdsList = new ArrayList<String>();
        List<PlaylistItem> videosList = getPlaylistItemsList(playlistId, maxResults);
        for (PlaylistItem video : videosList) {
            videoIdsList.add(video.getSnippet().getResourceId().getVideoId());
        }
        return videoIdsList;
    }

    public SearchResult getRecentVideo() throws IOException {
        YouTube.Search.List searchListRequest = mService.search().list("snippet");
        searchListRequest.setChannelId(CHANNEL_ID);
        searchListRequest.setKey(API_KEY);
        searchListRequest.setOrder("date");
        searchListRequest.setMaxResults(1l);
        return searchListRequest.execute().getItems().get(0);
    }

    public String getRecentVideoId() throws IOException {
        YouTube.Search.List searchListRequest = mService.search().list("snippet");
        searchListRequest.setChannelId(CHANNEL_ID);
        searchListRequest.setKey(API_KEY);
        searchListRequest.setOrder("date");
        searchListRequest.setMaxResults(1l);
        return searchListRequest.execute().getItems().get(0).getId().getVideoId();
    }

    public List<PlaylistItem> getLatestVideos() throws IOException {
        return getPlaylistItemsList("UU1Z7TQ9jXXiX-k3YanWLUUg", 15l);
    }

    public List<Playlist> getPlaylistsList() throws IOException {
        YouTube.Playlists.List playlistsListRequest = mService.playlists().list("snippet");
        playlistsListRequest.setChannelId("UC1Z7TQ9jXXiX-k3YanWLUUg");
        playlistsListRequest.setMaxResults((long) 10);
        return playlistsListRequest.execute().getItems();
    }
}