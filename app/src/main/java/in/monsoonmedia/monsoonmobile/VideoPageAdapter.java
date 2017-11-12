package in.monsoonmedia.monsoonmobile;


//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;


import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.Video;

import java.util.List;

/**
 * Created by ramesh on 27/9/17.
 */

public class VideoPageAdapter extends android.support.v13.app.FragmentPagerAdapter {


    private List<PlaylistItem> playlistItemsList;
    private ContentFragment contentFragment;
    private ViewPager viewPager;
    private Context context;

    public VideoPageAdapter(Context context, FragmentManager fm, List<PlaylistItem> playlistItemsList, ViewPager viewPager) {
        super(fm);
        this.playlistItemsList = playlistItemsList;
        this.viewPager = viewPager;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        contentFragment = ContentFragment.getInstance(context, playlistItemsList.get(position), position, playlistItemsList.size() - 1, viewPager);
        return contentFragment;
    }

    @Override
    public int getCount() {
        return playlistItemsList.size();
    }
}
