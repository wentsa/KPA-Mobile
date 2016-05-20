package cz.knihaplnaaktivit.kpa_mobile;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.List;

import cz.knihaplnaaktivit.kpa_mobile.connectors.ApiConnector;
import cz.knihaplnaaktivit.kpa_mobile.model.Product;
import cz.knihaplnaaktivit.kpa_mobile.repository.ProductRepository;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Utils;

public class KPA100Dashboard extends AppCompatActivity {

    private static boolean isAlreadySynchronized = false;

    RelativeLayout mSyncWrapper;

    ScrollView mDashboardWrapper;

    ImageView mSyncIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpa100_dashboard);

        mSyncWrapper = (RelativeLayout) findViewById(R.id.sync_wrapper);
        mDashboardWrapper = (ScrollView) findViewById(R.id.dashboard_wrapper);
        mSyncIcon = (ImageView) findViewById(R.id.sync_icon);

        if(!isAlreadySynchronized) {
            synchronize();
        } else {
            mSyncWrapper.setVisibility(View.GONE);
            mDashboardWrapper.setVisibility(View.VISIBLE);
        }
    }

    private void synchronize() {
        animateSyncIcon();

        if(Utils.isOnline(this)) {
            // updates product info, after finish user is not blocked anymore and image sync in background is started
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    ApiConnector.synchronize(KPA100Dashboard.this, false);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    mSyncWrapper.setVisibility(View.GONE);
                    mDashboardWrapper.setVisibility(View.VISIBLE);
                    mSyncIcon.setAnimation(null);

                    // updates images
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            List<Product> products = ProductRepository.getProducts(KPA100Dashboard.this);
                            for(Product p : products) {
                                ApiConnector.updateProductsImages(KPA100Dashboard.this, p.getId());
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            isAlreadySynchronized = true;
                        }
                    }.execute();
                }
            }.execute();

        } else {
            Toast.makeText(KPA100Dashboard.this, R.string.without_connection, Toast.LENGTH_SHORT).show();
            mSyncWrapper.setVisibility(View.GONE);
            mDashboardWrapper.setVisibility(View.VISIBLE);
            mSyncIcon.setAnimation(null);
        }
    }

    private void animateSyncIcon() {
        RotateAnimation anim = new RotateAnimation(0f, 360f,  Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(1200);
        mSyncIcon.startAnimation(anim);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View button = findViewById(R.id.share_photo_wrapper);
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
