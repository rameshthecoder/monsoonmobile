package in.monsoonmedia.monsoonmobile;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.Video;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ramesh on 1/11/17.
 */

public class TrendingVideosAdapter extends ArrayAdapter {

    Context context;
    int resource;
    List<Video> videosList;

    public TrendingVideosAdapter(@NonNull Context context, @LayoutRes int resource, List<Video> videosList) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.videosList = videosList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Video video = videosList.get(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }
        ImageView imageViewThumbnail = (ImageView) convertView.findViewById(R.id.imageViewThumbnail);
        TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
        TextView textViewViewsCount = (TextView) convertView.findViewById(R.id.textViewViewsCount);
        TextView textViewPublishedAt = (TextView) convertView.findViewById(R.id.textViewPublishedAt);
        textViewTitle.setText(video.getSnippet().getTitle());
        textViewViewsCount.setText(video.getStatistics().getViewCount().toString());

        new GetImageTask(context, imageViewThumbnail, video.getSnippet().getThumbnails().getHigh().getUrl());

        DateTime dateTimePublishedAt = video.getSnippet().getPublishedAt();


        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");
        String dateString = dateTimePublishedAt.toString().substring(0, 10);
        Date date = null;
        String publishedAt = null;
        try {
            date = inputFormat.parse(dateString);
            publishedAt = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        textViewPublishedAt.setText(publishedAt);

        return convertView;
    }

    @Override
    public int getCount() {
        return videosList.size();
    }
}
