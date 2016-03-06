package cz.knihaplnaaktivit.kpa_mobile;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cz.knihaplnaaktivit.kpa_mobile.utilities.Utils;

public class KPA100Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpa100_dashboard);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View button = findViewById(R.id.btn_share_photo);
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    public void onBtnSummaryClicked(View v) {
        navigate(KPA200Summary.class);
    }

    public void onBtnContactUsClicked(View v) {
        navigate(KPA300ContactUs.class);
    }

    public void onBtnSharePhotoClicked(View v) {
        navigate(KPA400PhotoShare.class);
    }

    public void onBtnAboutClicked(View v) {
        navigate(KPA500About.class);
    }

    public void onBtnFacebookClicked(View v) {
        Utils.visitFacebook(this, "www.knihaplnaaktivit.cz", "1552685675006861", true);
    }

    private void navigate(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_down, R.anim.stay);
    }
}
