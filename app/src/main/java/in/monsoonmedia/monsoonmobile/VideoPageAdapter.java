package in.monsoonmedia.monsoonmobile;



//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.google.api.services.youtube.model.Video;

import java.util.List;

/**
 * Created by ramesh on 27/9/17.
 */

public class VideoPageAdapter extends android.support.v13.app.FragmentPagerAdapter {


    private List<Video> videosList;

    public VideoPageAdapter(FragmentManager fm, List<Video> videosList) {
        super(fm);
        this.videosList = videosList;
    }

    @Override
    public Fragment getItem(int position) {
        ContentFragment contentFragment = ContentFragment.getInstance(videosList.get(position));
        return contentFragment;
    }

    @Override
    public int getCount() {
        return videosList.size();
    }
}
