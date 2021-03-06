package in.monsoonmedia.monsoonmobile;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ramesh on 17/11/17.
 */

public class NotificationService extends Service {

    private final int INTERVAL = 10 * 1000;
    private Timer timer = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (Helper.isConnectedToInternet(getBaseContext())) {
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
                                Toast.makeText(getBaseContext(), "Recent: " + recentVideoId, Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(getBaseContext(), "Recent: " + recentVideoId, Toast.LENGTH_SHORT).show();
                        }
                    }.execute();
                }
            }
        }, 0, INTERVAL);
        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceTask = new Intent(getApplicationContext(), this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10000, restartPendingIntent);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }
}
