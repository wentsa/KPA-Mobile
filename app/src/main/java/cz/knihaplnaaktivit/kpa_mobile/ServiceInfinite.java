package cz.knihaplnaaktivit.kpa_mobile;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

public class ServiceInfinite extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(Long.MAX_VALUE);
            }
        }).start();
        return START_STICKY;
    }
}
