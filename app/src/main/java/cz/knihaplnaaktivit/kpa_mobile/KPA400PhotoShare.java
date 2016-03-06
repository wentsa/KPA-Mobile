package cz.knihaplnaaktivit.kpa_mobile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class KPA400PhotoShare extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_BROWSE = 2;

    private enum PermissionRequestType {

        WRITE_EXTERNAL_STORAGE_TAKE(1), WRITE_EXTERNAL_STORAGE_BROWSE(2), WRITE_EXTERNAL_STORAGE_SEND(3);

        private final int mCode;

        PermissionRequestType(int code) {
            mCode = code;
        }

        protected int getCode() {
            return mCode;
        }
    }

    private String mImagePath;

    @Bind(R.id.thumbnail)
    ImageView mThumbnail;

    @Bind(R.id.input_name)
    EditText mName;

    @Bind(R.id.input_email)
    EditText mEmail;

    @Bind(R.id.input_description)
    EditText mDescription;

    @Bind(R.id.content_wrapper)
    ScrollView mContentWrapper;

    @Bind(R.id.placeholder_warning)
    TextView mPlaceholderWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpa400_photo_share);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        triggerContentVisibility();
    }

    private void triggerContentVisibility() {
        if(mImagePath == null) {
            mContentWrapper.setVisibility(View.GONE);
            mPlaceholderWarning.setVisibility(View.VISIBLE);
        } else {
            mContentWrapper.setVisibility(View.VISIBLE);
            mPlaceholderWarning.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_kpa400, menu);
        return true;
    }

    //--------------- MENU BUTTONS ---------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera: {
                takePhoto();
                return true;
            }
            case R.id.browse: {
                browse();
                return true;
            }
            case R.id.send: {
                send();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * CAMERA
     */
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // does camera app exist?
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // does app have permission?
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                File f = createImageFile();
                if (f != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } else {
                promptForPermission(PermissionRequestType.WRITE_EXTERNAL_STORAGE_TAKE);
            }
        }
    }

    /**
     * FILE EXPLORER
     */
    private void browse() {
        Intent browsePictureIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // does file browser app exist?
        if (browsePictureIntent.resolveActivity(getPackageManager()) != null) {
            // does app have permission?
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(browsePictureIntent, REQUEST_IMAGE_BROWSE);
            } else {
                promptForPermission(PermissionRequestType.WRITE_EXTERNAL_STORAGE_BROWSE);
            }
        }
    }

    /**
     * API SEND
     */
    private void send() {
        if(mImagePath != null) {
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
            final String description = mDescription.getText().toString();
            if(TextUtils.isEmpty(description)) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.description_empty_title))
                        .setMessage(getString(R.string.description_empty_message))
                        .setNeutralButton(R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert) // TODO red icon
                        .show();
                return;
            }

            // connection check
            if(Utils.isOnline(this)) {
                // does app have permission?
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // image send
                    if(!Utils.isWifiConnected(this)) {
                        new AlertDialog.Builder(this)
                                .setTitle(getString(R.string.no_wifi_title))
                                .setMessage(getString(R.string.no_wifi_message))
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ApiConnector.sendImage(KPA400PhotoShare.this, name, mail, description, mImagePath);
                                        Toast.makeText(KPA400PhotoShare.this, R.string.send_image_ok, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert) // TODO red icon
                                .show();
                    } else {
                        ApiConnector.sendImage(this, name, mail, description, mImagePath);
                        Toast.makeText(KPA400PhotoShare.this, R.string.send_image_ok, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    promptForPermission(PermissionRequestType.WRITE_EXTERNAL_STORAGE_SEND);
                }
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.no_internet_title))
                        .setMessage(getString(R.string.no_internet_message))
                        .setNeutralButton(R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert) // TODO red icon
                        .show();
            }
        }
    }

    /**
     * Creates prompt dialog for permission
     */
    private void promptForPermission(final PermissionRequestType type) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // permission explanation
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.permission_explanation_title))
                    .setMessage(getString(R.string.permission_explanation_message))
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermission(type);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info) // TODO red icon
                    .show();

        } else {
            requestPermission(type);
        }
    }

    private void requestPermission(PermissionRequestType type) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, type.getCode());
    }


    /**
     * Permission granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == PermissionRequestType.WRITE_EXTERNAL_STORAGE_TAKE.getCode()) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            }
        } else if(requestCode == PermissionRequestType.WRITE_EXTERNAL_STORAGE_BROWSE.getCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                browse();
            }
        } else if(requestCode == PermissionRequestType.WRITE_EXTERNAL_STORAGE_SEND.getCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                send();
            }
        }
    }

    /**
     * Photo taken or browsed
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && mImagePath != null) {
            // send broadcast to image scanners
            File f = new File(mImagePath);
            Uri contentUri = Uri.fromFile(f);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
            this.sendBroadcast(mediaScanIntent);

            fillForm();
        } else if(requestCode == REQUEST_IMAGE_BROWSE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if(cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mImagePath = cursor.getString(columnIndex);
                cursor.close();

                fillForm();
            }
        }
    }

    private void fillForm() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mThumbnail.setImageBitmap(Utils.getScaledBitmap(mImagePath, metrics.widthPixels, 0));

        triggerContentVisibility();
    }

    private @Nullable File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
        String imageFileName = "KPA_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            mImagePath = image.getAbsolutePath();
        } catch (IOException e) {
            mImagePath = null;
        }
        return image;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_up);
    }

}
