package in.monsoonmedia.monsoonmobile;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.Video;

import java.io.IOException;
import java.util.List;

/**
 * Created by ramesh on 27/9/17.
 */

public class ContentFragment extends Fragment {

    private static final int RECOVERY_REQUEST = 1;
    private static final String API_KEY = "AIzaSyAhPbII6mdo79M69BEeI69yD0gW2tiI9CY";
    private PlaylistItem playlistItem;
    private ViewPager viewPager;
    private YouTubePlayerFragment youTubePlayerFragment;
    private ImageButton imageButtonPrevious;
    private ImageButton imageButtonNext;
    private ImageView imageViewThumbnail;
    private int currentPosition;
    private int lastPosition;
    private Context context;
    private static ContentFragment contentFragment;


    public static ContentFragment getInstance(Context context, PlaylistItem playlistItem, int currentPosition, int lastPosition, ViewPager viewPager) {
//        if (contentFragment == null) {
//            contentFragment = new ContentFragment();
//            contentFragment.init(playlistItem, currentPosition, lastPosition);
//        } else {
//            contentFragment.setPlaylistItem(playlistItem);
//        }
//        return contentFragment;
        ContentFragment contentFragment = new ContentFragment();
        contentFragment.init(context, playlistItem, currentPosition, lastPosition, viewPager);
        return contentFragment;
    }

    private void setPlaylistItem(PlaylistItem playlistItem) {
        this.playlistItem = playlistItem;
    }

    private void init(Context context, PlaylistItem playlistItem, int currentPosition, int lastPosition, ViewPager viewPager) {
        youTubePlayerFragment = YouTubePlayerFragment.newInstance();
        this.playlistItem = playlistItem;
        this.currentPosition = currentPosition;
        this.lastPosition = lastPosition;
        this.viewPager = viewPager;
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_fragment, container, false);
        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        TextView textViewDescription = (TextView) view.findViewById(R.id.textViewDescription);
        imageButtonPrevious = (ImageButton) view.findViewById(R.id.imageButtonPrevious);
        imageButtonNext = (ImageButton) view.findViewById(R.id.imageButtonNext);
        imageViewThumbnail = (ImageView) view.findViewById(R.id.imageViewThumbnail);
        imageViewThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isConnectedToInternet(context)) {
                    Intent intent = new Intent(getActivity().getBaseContext(), PlayerActivity.class);
                    intent.putExtra("playlistId", YouTubeHelper.UPLOADS);
                    intent.putExtra("playlistTitle", "Uploads");
                    intent.putExtra("videoId", playlistItem.getSnippet().getResourceId().getVideoId());
                    intent.putExtra("videoTitle", playlistItem.getSnippet().getTitle());
                    intent.putExtra("videoDescription", playlistItem.getSnippet().getDescription());
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                }
            }
        });

        imageButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() != YouTubeHelper.MAX_RESULTS) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });

        if (currentPosition == 0) {
            imageButtonPrevious.setVisibility(View.INVISIBLE);
        }

        if (currentPosition == lastPosition) {
            imageButtonNext.setVisibility(View.INVISIBLE);
        }

        textViewTitle.setText(playlistItem.getSnippet().getTitle());
        textViewDescription.setText(playlistItem.getSnippet().getDescription());
        String thumbnailUrl = playlistItem.getSnippet().getThumbnails().getMedium().getUrl().toString();
        new GetImageTask(this.getActivity().getBaseContext(), imageViewThumbnail, thumbnailUrl).execute();

        return view;
    }
}
