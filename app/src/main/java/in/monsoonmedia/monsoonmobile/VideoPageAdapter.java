package in.monsoonmedia.monsoonmobile;



//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
import android.app.Fragment;
import android.app.FragmentManager;
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
    private ViewPager viewPager;

    public VideoPageAdapter(FragmentManager fm, List<PlaylistItem> playlistItemsList, ViewPager viewPager) {
        super(fm);
        this.playlistItemsList = playlistItemsList;
        this.viewPager = viewPager;
    }

    @Override
    public Fragment getItem(int position) {
        ContentFragment contentFragment = ContentFragment.getInstance(playlistItemsList.get(position), viewPager);
        return contentFragment;
    }

    @Override
    public int getCount() {
        return playlistItemsList.size();
    }
}
