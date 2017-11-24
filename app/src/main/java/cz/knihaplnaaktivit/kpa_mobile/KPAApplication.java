package cz.knihaplnaaktivit.kpa_mobile;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;

import cz.knihaplnaaktivit.kpa_mobile.connectors.ApiConnector;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Utils;

public class KPAApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, ServiceInfinite.class));
    }

    public static class ConnectedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            if (Utils.isOnline(context)){
                AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        ApiConnector.synchronize(context, true);
                        return null;
                    }
                });
            }
        }
    }
}
