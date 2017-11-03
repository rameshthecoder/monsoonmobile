package in.monsoonmedia.monsoonmobile;


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

public class ContentFragment extends Fragment implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private static final String API_KEY = "AIzaSyAhPbII6mdo79M69BEeI69yD0gW2tiI9CY";
    private Video video;
    private PlaylistItem playlistItem;
    private ViewPager viewPager;
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerFragment youTubePlayerFragment;

//    public static ContentFragment getInstance(Video video, ViewPager viewPager) {
//        ContentFragment contentFragment = new ContentFragment();
//        contentFragment.init();
//        contentFragment.setVideo(video);
//        contentFragment.setViewPager(viewPager);
////        Bundle args = new Bundle();
////        args.putInt("position", position);
////        contentFragment.setArguments(args);
//        return contentFragment;
//    }

    public static ContentFragment getInstance(PlaylistItem playlistItem, ViewPager viewPager) {
        ContentFragment contentFragment = new ContentFragment();
        contentFragment.init();
        contentFragment.setPlaylistItem(playlistItem);
        contentFragment.setViewPager(viewPager);
//        Bundle args = new Bundle();
//        args.putInt("position", position);
//        contentFragment.setArguments(args);
        return contentFragment;
    }

    private void init() {
        youTubePlayerFragment = YouTubePlayerFragment.newInstance();
    }

    private void setVideo(Video video) {
        this.video = video;
    }

    private void setPlaylistItem(PlaylistItem playlistItem) {
        this.playlistItem = playlistItem;
    }
    private void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        position = getArguments().getInt("position");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_fragment, container, false);
        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        TextView textViewDescription = (TextView) view.findViewById(R.id.textViewDescription);
        ImageButton imageButtonPrevious = (ImageButton) view.findViewById(R.id.imageButtonPrevious);
        ImageButton imageButtonNext = (ImageButton) view.findViewById(R.id.imageButtonNext);
//        TextView textViewLikesCount = (TextView) view.findViewById(R.id.textViewLikesCount);
//        TextView textViewDislikesCount = (TextView) view.findViewById(R.id.textViewDislikesCount);
        final ImageView imageViewThumbnail = (ImageView) view.findViewById(R.id.imageViewThumbnail);
        imageViewThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getBaseContext(), PlayerActivity.class);
                intent.putExtra("videoId", playlistItem.getSnippet().getResourceId().getVideoId());
                intent.putExtra("videoTitle", playlistItem.getSnippet().getTitle());
                intent.putExtra("videoDescription", playlistItem.getSnippet().getDescription());
                startActivity(intent);
            }
        });

        imageButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                }
            }
        });

        imageButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != YouTubeHelper.MAX_RESULTS) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });
//        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) view.findViewById(R.id.youtube_view);
        textViewTitle.setText(playlistItem.getSnippet().getTitle());
        textViewDescription.setText(playlistItem.getSnippet().getDescription());
//        textViewLikesCount.setText(video.getStatistics().getLikeCount().toString());
//        textViewDislikesCount.setText(video.getStatistics().getDislikeCount().toString());
        String thumbnailUrl = playlistItem.getSnippet().getThumbnails().getHigh().getUrl().toString();
        new GetImageTask(this.getActivity().getBaseContext(), imageViewThumbnail, thumbnailUrl).execute();
//
//        android.app.FragmentManager fragmentManager = getFragmentManager();
//
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_fragment, youTubePlayerFragment);
//        fragmentTransaction.commit();
        return view;
    }


//    public void initializeYouTubeFragment(){
//        youTubePlayerFragment.initialize(API_KEY, this);
//    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(!b) {
            this.youTubePlayer = youTubePlayer;
            youTubePlayer.cueVideo(video.getId());
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if(youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(), RECOVERY_REQUEST);
        } else {
            String error = youTubeInitializationResult.toString();
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }
    }

//    private class GetImageTask extends AsyncTask<Void, Void, String> {
//
//        Context context;
//        ImageView imageViewThumbnail;
//
//        GetImageTask(Context context, ImageView imageViewThumbnail) {
//            this.context = context;
//            this.imageViewThumbnail = imageViewThumbnail;
//        }
//
//        @Override
//        protected void onPreExecute() {
//
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            String thumbnailUrl = null;
//            thumbnailUrl = video.getSnippet().getThumbnails().getHigh().getUrl();
//            Log.d("Thumbnail URL: ", thumbnailUrl);
//            return thumbnailUrl;
//        }
//
//        @Override
//        protected void onPostExecute(String thumbnailUrl) {
//            Glide.with(context).load(thumbnailUrl).into(imageViewThumbnail);
//
//        }
//    }
}
