package in.monsoonmedia.monsoonmobile;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.youtube.model.Playlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramesh on 3/10/17.
 */

public class CategoryListAdapter extends ArrayAdapter {

    private ArrayList<String> categoryTitlesList = new ArrayList<String>();
    List<Playlist> categoryPlaylistsList;
    ArrayList<String> colors = new ArrayList<String>();
    private int resource;
    private Context context;

    public CategoryListAdapter(@NonNull Context context, @LayoutRes int resource, List<Playlist> categoryPlaylistsList) {
        super(context, resource);
        this.context = context;
        this.categoryPlaylistsList = categoryPlaylistsList;
        this.resource = resource;
        categoryTitlesList.add("Movies");
        categoryTitlesList.add("Art & Literature");
        categoryTitlesList.add("Interviews");
        categoryTitlesList.add("Live & Guidance");
        categoryTitlesList.add("Talk Serious");
        categoryTitlesList.add("Fun, Food & Travel");

        colors.add("#CC3333");
        colors.add("#FF3366");
        colors.add("#6633CC");
        colors.add("#0099CC");
        colors.add("#339933");
        colors.add("#99CC33");
    }

    @Override
    public int getCount() {
        return categoryTitlesList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }
        TextView textViewCategoryTitle = (TextView) convertView.findViewById(R.id.textViewCategoryTitle);
        ImageView imageViewCategoryThumbnail = (ImageView) convertView.findViewById(R.id.imageViewCategoryThumbnail);

        textViewCategoryTitle.setText(categoryTitlesList.get(position));
        textViewCategoryTitle.setBackgroundColor(Color.parseColor(colors.get(position)));
        String url = categoryPlaylistsList.get(position).getSnippet().getThumbnails().getHigh().getUrl();
        new GetImageTask(context, imageViewCategoryThumbnail, url).execute();
        return convertView;
    }
}
