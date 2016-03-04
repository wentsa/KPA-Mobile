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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import cz.knihaplnaaktivit.kpa_mobile.KPA100Dashboard;
import cz.knihaplnaaktivit.kpa_mobile.R;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Network;

public class ServiceSendImage extends IntentService {

    public static final String EMAIL = "email";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";

    private static final int NOTIFICATION_ID = 1;

    public ServiceSendImage() {
        super("KPA_ServiceSendImage");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Bundle extras = intent.getExtras();
            if(extras != null) {
                String mail = extras.getString(EMAIL);
                String description = extras.getString(DESCRIPTION);
                String imagePath = extras.getString(IMAGE);

                // Image load
                File image;
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    image = new File(imagePath);
                    if(!image.exists()) {
                        Toast.makeText(ServiceSendImage.this, getString(R.string.upload_fail_not_found), Toast.LENGTH_SHORT).show();
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
                    OutputStream out = new Base64OutputStream(o, Base64.DEFAULT);
                    IOUtils.copy(is, out);
                    is.close();
                    out.close();
                    imageEncoded = o.toString("UTF-8");
                    o.close();
                } catch (IOException e) {
                    Toast.makeText(ServiceSendImage.this, getString(R.string.send_image_not_ok2), Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, String> params = new HashMap<>();

                params.put("email", mail);
                params.put("description", description);
                params.put("image", imageEncoded);

                Network.doPost("send_photo.php", params);
            }
        } catch (Exception e) {
            Toast.makeText(ServiceSendImage.this, getString(R.string.send_image_not_ok2), Toast.LENGTH_SHORT).show();
        }
    }

    private void failPermission() {
        Toast.makeText(ServiceSendImage.this, getString(R.string.upload_fail_permission_not_granted), Toast.LENGTH_SHORT).show();
    }

    private void createNotification() {
        Intent notificationIntent = new Intent(this, KPA100Dashboard.class); // activitu
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
}
