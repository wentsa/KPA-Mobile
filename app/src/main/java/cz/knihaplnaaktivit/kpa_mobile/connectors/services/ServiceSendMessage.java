package cz.knihaplnaaktivit.kpa_mobile.connectors.services;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import cz.knihaplnaaktivit.kpa_mobile.KPA100Dashboard;
import cz.knihaplnaaktivit.kpa_mobile.R;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Network;

public class ServiceSendMessage extends IntentService {

    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String SUBJECT = "subject";
    public static final String MESSAGE = "message";

    private static final int NOTIFICATION_ID = 2;

    private final Handler mHandler;

    public ServiceSendMessage() {
        super("KPA_ServiceSendMessage");
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Bundle extras = intent.getExtras();
            if(extras != null) {
                String name = extras.getString(NAME);
                String mail = extras.getString(EMAIL);
                String subject = extras.getString(SUBJECT);
                String message = extras.getString(MESSAGE);

                createNotification();

                Map<String, String> params = new HashMap<>();

                params.put("name", name);
                params.put("email", mail);
                params.put("subject", subject);
                params.put("message", message);

                Network.doPost("send_message.php", params);
            }
        } catch (Exception e) {
            toast(getString(R.string.send_message_not_ok));
        }
    }

    private void createNotification() {
        Intent notificationIntent = new Intent(this, KPA100Dashboard.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.message_upload))
                .setWhen(System.currentTimeMillis())
                .setTicker(getString(R.string.message_upload))
                .setContentText(getString(R.string.message_upload_description))
                .setSmallIcon(R.drawable.ic_upload_black_24dp)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void toast(String msg) {
        mHandler.post(new DisplayToast(msg));
    }

    private class DisplayToast implements Runnable {
        private final String mText;

        public DisplayToast(String text){
            mText = text;
        }

        public void run(){
            Toast.makeText(ServiceSendMessage.this, mText, Toast.LENGTH_SHORT).show();
        }
    }
}
