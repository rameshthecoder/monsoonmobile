package in.monsoonmedia.monsoonmobile;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by ramesh on 5/12/17.
 */

public class TestService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Helper.isConnectedToInternet(getBaseContext())) {
            Toast.makeText(getApplicationContext(), "onStartCommand!", Toast.LENGTH_SHORT).show();
            Log.d("onStartCommand","");
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String recentVideoId = null;
                    try {
                        recentVideoId = YouTubeHelper.getInstance().getRecentVideoId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return recentVideoId;
                }

                @Override
                protected void onPostExecute(String result) {
                    Log.d("Recent: ", result);
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    String recentVideoId = settings.getString("recentVideoId", null);
                    if (recentVideoId == null) {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("recentVideoId", result);
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "Recent: " + recentVideoId, Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getApplicationContext(), "Recent: " + recentVideoId, Toast.LENGTH_SHORT).show();
                }
            }.execute();
        }
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
