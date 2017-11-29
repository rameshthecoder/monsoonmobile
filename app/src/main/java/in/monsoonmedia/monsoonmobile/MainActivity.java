package in.monsoonmedia.monsoonmobile;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends FragmentActivity
        implements EasyPermissions.PermissionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 123;
    GoogleAccountCredential mCredential;
    private Button mCallApiButton;
    ProgressDialog mProgress;
    ListView listViewCategories;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call YouTube Data API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {YouTubeScopes.YOUTUBE_FORCE_SSL};
    private YouTubeHelper youTubeHelper;
    private SearchResult recentVideo;
    private List<Playlist> categoryPlaylistsList;
    private List<Video> trendingVideosList;
    ProgressDialog progressDialog;
    ViewPager viewPagerContent;
    VideoPageAdapter videoPageAdapter;
    List<Video> videosList;
    List<PlaylistItem> playlistItemsList;
    GridView gridViewCategories;
    ListView listViewTrendingVideos;
    private GoogleApiClient googleApiClient;

    /**
     * Create the main activity.
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        listViewCategories = (ListView) findViewById(R.id.listViewCategories);
//        startService(new Intent(this, NotificationService.class));
        gridViewCategories = (GridView) findViewById(R.id.gridViewCategories);
        listViewTrendingVideos = (ListView) findViewById(R.id.listViewTrendingVideos);
//        gridViewCategories.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return event.getAction() == MotionEvent.ACTION_MOVE;
//            }
//        });
        viewPagerContent = (ViewPager) findViewById(R.id.viewPagerContent);
        viewPagerContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        mCallApiButton = (Button) findViewById(R.id.buttonCallApi);
//        mCallApiButton.setText(BUTTON_TEXT);
//        mCallApiButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCallApiButton.setEnabled(false);
//                getResultsFromApi();
//                mCallApiButton.setEnabled(true);
//            }
//        });
        init();
        signIn();
//        new LoadCategoryThumbnailsTask().execute();
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling YouTube Data API ...");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        getResultsFromApi();
//        listViewVideos = (ListView) findViewById(R.id.videoList);

    }

    public void createNotification(String videoId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("videoId", videoId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.header_icon, "Action!", pendingIntent).build();
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Notification Title!")
                .setContentText("Text").setSmallIcon(R.drawable.header_icon)
                .setContentIntent(pendingIntent)
                .addAction(action)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class LoadCategoryThumbnailsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                categoryPlaylistsList = youTubeHelper.getCategoryPlaylistsList();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            final CategoryListAdapter categoryListAdapter = new CategoryListAdapter(MainActivity.this, R.layout.item_category, categoryPlaylistsList);
            gridViewCategories.setAdapter(categoryListAdapter);
            Helper.setGridViewSize(gridViewCategories);
            gridViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (Helper.isConnectedToInternet(MainActivity.this)) {
                        String categoryPlaylistId = categoryPlaylistsList.get(position).getId();
                        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                        intent.putExtra("playlistId", categoryPlaylistId);
                        intent.putExtra("playlistTitle", categoryListAdapter.getCategoryTitle(position));
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private class LoadTrendingThumbnailsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                trendingVideosList = youTubeHelper.getTrendingVideosList();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            listViewTrendingVideos.setAdapter(new VideosListAdapter(MainActivity.this, R.layout.item_video, trendingVideosList));
            Helper.setListViewSize(listViewTrendingVideos);
            listViewTrendingVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (Helper.isConnectedToInternet(MainActivity.this)) {
                        Video currentVideo = trendingVideosList.get(position);
                        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                        intent.putExtra("playlistId", YouTubeHelper.TRENDING);
                        intent.putExtra("playlistTitle", "TRENDING VIDEOS");
                        intent.putExtra("videoId", currentVideo.getId());
                        intent.putExtra("videoTitle", currentVideo.getSnippet().getTitle());
                        intent.putExtra("videoDescription", currentVideo.getSnippet().getDescription());
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void init() {
        mCredential = GoogleAccountCredential.usingOAuth2(
                this, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
//        youTubeHelper = YouTubeHelper.getInstance(mCredential);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestScopes(new Scope("https://www.googleapis.com/auth/youtube.force-ssl")).build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        //Select account.
        mCredential.setSelectedAccount(googleSignInOptions.getAccount());
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
        } else {
//            new MakeRequestTask().execute();
//            new GetVideosListTask().execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
//                startActivityForResult(
//                        mCredential.newChooseAccountIntent(),
//                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
//                    mOutputText.setText(
//                         int   "This app requires Google Play Services. Please install " +
//                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;

            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
//                    Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                    GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
                    mCredential.setSelectedAccount(googleSignInAccount.getAccount());
                    mCredential.setSelectedAccountName(googleSignInAccount.getEmail());
                    youTubeHelper = YouTubeHelper.getInstance(mCredential);

                    new GetVideosListTask().execute();
                    new LoadCategoryThumbnailsTask().execute();
                    new LoadTrendingThumbnailsTask().execute();
                    SharedPreferences preferences = this.getSharedPreferences("monsoon_mobile_settings", Context.MODE_PRIVATE);
                    String recentVideoId = preferences.getString("recentVideoId", null);
                    if (recentVideoId == null) {
                        startService(new Intent(MainActivity.this, NotificationService.class));
                    }
//                    new AsyncTask<Void, Void, String>() {
//                        @Override
//                        protected String doInBackground(Void... params) {
//                            String recentVideoId = null;
//                            try {
//                                recentVideoId = YouTubeHelper.getInstance().getRecentVideoId();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            return recentVideoId;
//                        }
//
//                        @Override
//                        protected void onPostExecute(String result) {
//                            Log.d("Recent: ", result);
//                        }
//                    }.execute();
                }


        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }


    public class GetVideosListTask extends AsyncTask<Void, Void, List<PlaylistItem>> {

        @Override
        protected List<PlaylistItem> doInBackground(Void... params) {
//            videosList = null;
            try {
                playlistItemsList = youTubeHelper.getPlaylistItemsList(YouTubeHelper.UPLOADS);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return playlistItemsList;
        }

        @Override
        protected void onPostExecute(List<PlaylistItem> playlistItemsList) {
            super.onPostExecute(playlistItemsList);
            videoPageAdapter = new VideoPageAdapter(MainActivity.this, getFragmentManager(), playlistItemsList, viewPagerContent);
//            Toast.makeText(MainActivity.this, "Size: " + videosList.size(), Toast.LENGTH_SHORT).show();
            viewPagerContent.setAdapter(videoPageAdapter);
        }
    }

    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Object, Object, List<PlaylistItem>> {
        private Exception mLastError = null;


        /**
         * Background task to call YouTube Data API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<PlaylistItem> doInBackground(Object... params) {
            try {
                Log.d("Message: ", "Reached onPostExecute()");
                return getDataFromApi();
            } catch (Exception e) {
                Log.d("Exception: ", e.getMessage());
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<PlaylistItem> playlistItemsList) {
            mProgress.hide();
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
//                    mOutputText.setText("The following error occurred:\n"
//                            + mLastError.getMessage());
                }
            } else {
//                mOutputText.setText("Request cancelled.");
            }
        }

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         *
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private List<PlaylistItem> getDataFromApi() throws IOException {
//            return youTubeHelper.getUploadsItemsList();
            return null;
        }
    }
}