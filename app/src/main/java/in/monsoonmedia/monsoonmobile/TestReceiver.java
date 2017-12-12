package in.monsoonmedia.monsoonmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ramesh on 5/12/17.
 */

public class TestReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "in.monsoonmedia.monsoonmobile.service";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, TestIntentService.class);
        context.startService(serviceIntent);
    }
}
