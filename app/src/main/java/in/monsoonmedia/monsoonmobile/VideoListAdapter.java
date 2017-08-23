package in.monsoonmedia.monsoonmobile;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.api.services.youtube.model.PlaylistItem;

import java.util.List;

/**
 * Created by ramesh on 29/7/17.
 */

public class VideoListAdapter extends ArrayAdapter {

    private final Context context;
    private int resource;
    private List<PlaylistItem> playlistItems;

    public VideoListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<PlaylistItem> playlistItems) {
        super(context, resource, playlistItems);
        this.context = context;
        this.resource = resource;
        this.playlistItems = playlistItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_video, parent, false);
        }

        ImageView imageViewThumbnail = (ImageView) convertView.findViewById(R.id.imageViewThumbnail);
        TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);

        PlaylistItem currentItem = playlistItems.get(position);
        String url = currentItem.getSnippet().getThumbnails().getDefault().getUrl();

        textViewTitle.setText(currentItem.getSnippet().getTitle());
        Glide.with(context).load(url).into(imageViewThumbnail);
        return convertView;
    }

}
