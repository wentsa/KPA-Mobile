package cz.knihaplnaaktivit.kpa_mobile;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class KPA100Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpa100_dashboard);
    }

    public void onBtnSummaryClicked(View v) {
        Intent intent = new Intent(this, KPA200Summary.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_down, R.anim.stay);
    }

    public void onBtnContactUsClicked(View v) {

    }

    public void onBtnSharePhotoClicked(View v) {

    }

    public void onBtnAboutClicked(View v) {

    }

    public void onBtnFacebookClicked(View v) {
        String facebookUrl = "https://www.facebook.com/www.knihaplnaaktivit.cz";
        String facebookId = "1552685675006861";
        try {
            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + facebookId)));
            }
        } catch (PackageManager.NameNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
        }
    }
}
