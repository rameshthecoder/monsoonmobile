package in.monsoonmedia.monsoonmobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;

import java.io.IOException;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {

    private ListView listViewVideos;
    private String playlistId;
    private List<PlaylistItem> playlistItemsList;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        context = this;

        playlistId = getIntent().getStringExtra("playlistId");
        listViewVideos = (ListView) findViewById(R.id.listViewVideos);
        try {
            playlistItemsList = YouTubeHelper.getInstance().getPlaylistItemsList(playlistId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        listViewVideos.setAdapter(new VideoListAdapter(context, R.layout.item_video, playlistItemsList));
        listViewVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaylistItem currentItem = playlistItemsList.get(position);
                String videoId = currentItem.getId();
                String videoTitle = currentItem.getSnippet().getTitle();
                String videoDescription = currentItem.getSnippet().getDescription();
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("videoId", videoId);
                intent.putExtra("videoTitle", videoTitle);
                intent.putExtra("videoDescription", videoDescription);
                startActivity(intent);
            }
        });
    }

}
