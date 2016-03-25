package cz.knihaplnaaktivit.kpa_mobile;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import cz.knihaplnaaktivit.kpa_mobile.connectors.ApiConnector;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Utils;

public class KPAApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        synchronize(this);
    }

    public static class WifiConnectedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utils.isWifiConnected(context)){
                synchronize(context);
            }
        }
    }

    private static void synchronize(final Context ctx) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ApiConnector.synchronize(ctx);
                return null;
            }
        }.execute();
    }
}
