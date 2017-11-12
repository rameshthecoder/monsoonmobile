package in.monsoonmedia.monsoonmobile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.Video;

import java.io.IOException;
import java.util.List;

public class CategoryActivity extends FragmentActivity {

    private String categoryPlaylistId;
    private ViewPager viewPagerCategoryContent;
    private VideoPageAdapter videoPageAdapter;
    YouTubeHelper youTubeHelper;
    private List<Video> categoryVideosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryPlaylistId = getIntent().getStringExtra("categoryPlaylistId");
        viewPagerCategoryContent = (ViewPager) findViewById(R.id.viewPagerCategoryContent);
        youTubeHelper = YouTubeHelper.getInstance();
        Toast.makeText(CategoryActivity.this, "Category playlist id: " + categoryPlaylistId, Toast.LENGTH_SHORT).show();
        new GetCategoryVideosListTask().execute();
    }

    public class GetCategoryVideosListTask extends AsyncTask<Void, Void, List<PlaylistItem>> {

        @Override
        protected List<PlaylistItem> doInBackground(Void... params) {
//            videosList = null;
            List<PlaylistItem> categoryPlaylistItemsList = null;
            try {
                categoryPlaylistItemsList = youTubeHelper.getPlaylistItemsList(categoryPlaylistId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return categoryPlaylistItemsList;
        }

        @Override
        protected void onPostExecute(List<PlaylistItem> categoryPlaylistItemsList) {
            super.onPostExecute(categoryPlaylistItemsList);
            videoPageAdapter = new VideoPageAdapter(CategoryActivity.this, CategoryActivity.this.getFragmentManager(), categoryPlaylistItemsList, viewPagerCategoryContent);
//            Toast.makeText(CategoryActivity.this, "Size: " + categoryVideosList.size(), Toast.LENGTH_SHORT).show();
            viewPagerCategoryContent.setAdapter(videoPageAdapter);
        }
    }

}
