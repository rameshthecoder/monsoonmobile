package in.monsoonmedia.monsoonmobile;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by ramesh on 1/11/17.
 */

public class Helper  {
    public static void setGridViewSize(GridView gridView) {
        ListAdapter myListAdapter = gridView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size+=2) {
            View listItem = myListAdapter.getView(size, null, gridView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + (gridView.getMeasuredHeight() * (myListAdapter.getCount() - 1));
        gridView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }

    public static void setListViewSize(ListView listView) {
        ListAdapter myListAdapter = listView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getMeasuredHeight() * (myListAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }
}
