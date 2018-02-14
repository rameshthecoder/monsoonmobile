package in.monsoonmedia.monsoonmobile;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    new CheckInternetConnectionTask(SplashScreenActivity.this).execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private class CheckInternetConnectionTask extends AsyncTask<Void, Void, Boolean> {

        private Context context;

        CheckInternetConnectionTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isConnected = Helper.isConnectedToInternet(context);
            return isConnected;
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            super.onPostExecute(isConnected);
            if (isConnected) {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                new CheckInternetConnectionTask(context).execute();
            }
        }
    }
}
