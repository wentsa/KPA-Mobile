package cz.knihaplnaaktivit.kpa_mobile;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import cz.knihaplnaaktivit.kpa_mobile.connectors.ApiConnector;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Utils;

@ReportsCrashes(
        mailTo = "jozifeck6@gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_toast_text)
public class KPAApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }

    public static class WifiConnectedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            if (Utils.isWifiConnected(context)){
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        ApiConnector.synchronize(context, true);
                        return null;
                    }
                }.execute();
            }
        }
    }
}
