package cz.knihaplnaaktivit.kpa_mobile.connectors.services;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import cz.knihaplnaaktivit.kpa_mobile.KPA100Dashboard;
import cz.knihaplnaaktivit.kpa_mobile.R;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Network;

public class ServiceSendImage extends IntentService {

    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";

    private static final int NOTIFICATION_ID = 1;

    private final Handler mHandler;

    public ServiceSendImage() {
        super("KPA_ServiceSendImage");
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Bundle extras = intent.getExtras();
            if(extras != null) {
                String name = extras.getString(NAME);
                String mail = extras.getString(EMAIL);
                String description = extras.getString(DESCRIPTION);
                String imagePath = extras.getString(IMAGE);

                // Image load
                File image;
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    image = new File(imagePath);
                    if(!image.exists()) {
                        toast(getString(R.string.upload_fail_not_found));
                    }
                } else {
                    failPermission();
                    return;
                }

                createNotification();

                String imageEncoded;

                try {
                    ByteArrayOutputStream o = new ByteArrayOutputStream();
                    InputStream is = new FileInputStream(image);

                    // must be URL safe and then decoded properly on server side
                    OutputStream out = new Base64OutputStream(o, Base64.URL_SAFE);
                    IOUtils.copy(is, out);
                    is.close();
                    out.close();
                    imageEncoded = new String(o.toByteArray(), "UTF-8");
                    o.close();
                } catch (IOException e) {
                    toast(getString(R.string.send_image_not_ok2));
                    return;
                }

                Map<String, String> params = new HashMap<>();

                params.put("name", name);
                params.put("email", mail);
                params.put("description", description);
                params.put("image", imageEncoded);
                params.put("filename", image.getName());

                Network.doPost("send_photo.php", params);
            }
        } catch (Exception e) {
            toast(getString(R.string.send_image_not_ok2));
        }
    }

    private void failPermission() {
        toast(getString(R.string.upload_fail_permission_not_granted));
    }

    private void createNotification() {
        Intent notificationIntent = new Intent(this, KPA100Dashboard.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.photo_upload))
                .setWhen(System.currentTimeMillis())
                .setTicker(getString(R.string.photo_upload))
                .setContentText(getString(R.string.photo_upload_description))
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
            Toast.makeText(ServiceSendImage.this, mText, Toast.LENGTH_SHORT).show();
        }
    }
}
