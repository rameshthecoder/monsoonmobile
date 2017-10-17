package in.monsoonmedia.monsoonmobile;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by ramesh on 3/10/17.
 */

public class GetImageTask extends AsyncTask<Void, Void, Void> {

    Context context;
    ImageView imageViewThumbnail;
    String thumbnailUrl;

    GetImageTask(Context context, ImageView imageViewThumbnail, String thumbnailUrl) {
        this.context = context;
        this.imageViewThumbnail = imageViewThumbnail;
        this.thumbnailUrl = thumbnailUrl;
        Glide.with(context).load(thumbnailUrl).into(imageViewThumbnail);
        Log.d("Thumbnail URL: ", thumbnailUrl);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

}