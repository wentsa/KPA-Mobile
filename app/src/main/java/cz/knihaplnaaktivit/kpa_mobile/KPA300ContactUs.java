package cz.knihaplnaaktivit.kpa_mobile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.knihaplnaaktivit.kpa_mobile.connectors.ApiConnector;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Utils;

public class KPA300ContactUs extends AppCompatActivity {

    @Bind(R.id.input_name)
    EditText mName;

    @Bind(R.id.input_email)
    EditText mEmail;

    @Bind(R.id.input_subject)
    EditText mSubject;

    @Bind(R.id.input_message)
    EditText mMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpa300_contact_us);

        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_kpa300, menu);
        return true;
    }

    //--------------- MENU BUTTONS ---------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send: {
                send();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * API SEND
     */
    private void send() {
        // validations
        final String name = mName.getText().toString();
        if(TextUtils.isEmpty(name)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.name_empty_title))
                    .setMessage(getString(R.string.name_empty_message))
                    .setNeutralButton(R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert) // TODO red icon
                    .show();
            return;
        }
        final String mail = mEmail.getText().toString();
        if(!Utils.isValidEmail(mail)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.email_invalid_title))
                    .setMessage(getString(R.string.email_invalid_message))
                    .setNeutralButton(R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert) // TODO red icon
                    .show();
            return;
        }
        final String subject = mSubject.getText().toString();
        if(TextUtils.isEmpty(subject)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.subject_empty_title))
                    .setMessage(getString(R.string.subject_empty_message))
                    .setNeutralButton(R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert) // TODO red icon
                    .show();
            return;
        }
        final String message = mMessage.getText().toString();
        if(TextUtils.isEmpty(message)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.message_empty_title))
                    .setMessage(getString(R.string.message_empty_message))
                    .setNeutralButton(R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert) // TODO red icon
                    .show();
            return;
        }

        // connection check
        if(Utils.isOnline(this)) {
            // image send
            ApiConnector.sendMessage(this, name, mail, subject, message);
            Toast.makeText(KPA300ContactUs.this, R.string.send_message_ok, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.no_internet_title))
                    .setMessage(getString(R.string.no_internet_message_message))
                    .setNeutralButton(R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert) // TODO red icon
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_up);
    }

}
