package in.monsoonmedia.monsoonmobile;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by ramesh on 5/12/17.
 */

public class TestIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
//     * @param name Used to name the worker thread, important only for debugging.
     */

    public TestIntentService() {
        super("TestIntentService");
    }

    public TestIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (Helper.isConnectedToInternet(getBaseContext())) {
            Handler handler = new Handler(getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(getApplicationContext(), "onStartCommand!", Toast.LENGTH_SHORT).show();
                }
            });
            Log.d("onStartCommand","");
            String recentVideoId = null;
            try {
                recentVideoId = YouTubeHelper.getInstance().getRecentVideoId();
            } catch (IOException e) {
                e.printStackTrace();
            }
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String recentVideoIdPreference = settings.getString("recentVideoId", null);
            if (recentVideoIdPreference == null) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("recentVideoId", recentVideoId);
                editor.commit();
//                Toast.makeText(getApplicationContext(), "Recent: " + recentVideoId, Toast.LENGTH_SHORT).show();
            }
            Log.d("Recent", recentVideoId);
            showNotification();
        }
    }

    public void showNotification() {
        Log.d("At:", "showNotification() method");
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(TestIntentService.this, (int) System.currentTimeMillis(), intent, 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("New Video Uploaded!")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.header_icon)
                .setContentIntent(pendingIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
