package in.monsoonmedia.monsoonmobile;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.List;

/**
 * Created by ramesh on 25/7/17.
 */

public class YouTubeHelper {
    private GoogleAccountCredential mCredential;
    private YouTube mService;
//    private static YouTubeHelper youTubeHelper;


    public YouTubeHelper(GoogleAccountCredential credential) {
        init();
        this.mCredential = credential;
//        youTubeHelper = this;
    }

//    public static YouTubeHelper getInstance(GoogleAccountCredential credential) {
//        if(youTubeHelper == null) {
//            return new YouTubeHelper(credential);
//        } else {
//            return youTubeHelper;
//        }
//    }
//
//    public static YouTubeHelper getInstance() {
//        return youTubeHelper;
//    }

    private void init() {
        // Initialize credentials and service object.
//        mCredential = GoogleAccountCredential.usingOAuth2(
//                context, Arrays.asList(SCOPES))
//                .setBackOff(new ExponentialBackOff());

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.youtube.YouTube.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("YouTube Data API Android Quickstart")
                .build();
    }

    public List<PlaylistItem> getPlaylistItemsList(String playlistId) throws IOException {
        YouTube.PlaylistItems.List playListItemsListRequest = mService.playlistItems().list("snippet");
        playListItemsListRequest.setPlaylistId(playlistId);
        playListItemsListRequest.setMaxResults((long)10);
        return playListItemsListRequest.execute().getItems();
    }

    public List<PlaylistItem> getPlaylistItemsList(String playlistId, long maxResults) throws IOException {
        YouTube.PlaylistItems.List playListItemsListRequest = mService.playlistItems().list("snippet");
        playListItemsListRequest.setPlaylistId(playlistId);
        playListItemsListRequest.setMaxResults(maxResults);
        return playListItemsListRequest.execute().getItems();
    }

    public List<PlaylistItem> getUploadsItemsList() throws IOException {
//        return getPlaylistItemsList("PLJvP8jNh41cgNyKbY43DrwERzCJeDZwtB", 5l);
        return getPlaylistItemsList("UU1Z7TQ9jXXiX-k3YanWLUUg", 5l);
    }

    public SearchResult getRecentVideo() throws IOException {
        YouTube.Search.List searchListRequest = mService.search().list("snippet");
        searchListRequest.setChannelId("UC1Z7TQ9jXXiX-k3YanWLUUg");
        searchListRequest.setOrder("date");
        searchListRequest.setMaxResults(1l);
        return searchListRequest.execute().getItems().get(0);
    }

    public List<PlaylistItem> getLatestVideos() throws IOException {
        return getPlaylistItemsList("UU1Z7TQ9jXXiX-k3YanWLUUg", 15l);
    }

    public List<Playlist> getPlaylistsList() throws IOException {
        YouTube.Playlists.List playlistsListRequest = mService.playlists().list("snippet");
        playlistsListRequest.setChannelId("UC1Z7TQ9jXXiX-k3YanWLUUg");
        playlistsListRequest.setMaxResults((long)10);
        return playlistsListRequest.execute().getItems();
    }
}