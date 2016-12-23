package cz.knihaplnaaktivit.kpa_mobile;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.messaging.FirebaseMessaging;

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

    private Tracker mTracker;
    private GoogleApiClient client;

    private static final int REQUEST_INVITE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpa100_dashboard);

        mSyncWrapper = (RelativeLayout) findViewById(R.id.sync_wrapper);
        mDashboardWrapper = (ScrollView) findViewById(R.id.dashboard_wrapper);
        mSyncIcon = (ImageView) findViewById(R.id.sync_icon);

        KPAApplication application = (KPAApplication) getApplication();
        mTracker = application.getDefaultTracker();

        if (!isAlreadySynchronized) {
            synchronize();
        } else {
            mSyncWrapper.setVisibility(View.GONE);
            mDashboardWrapper.setVisibility(View.VISIBLE);
        }
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void synchronize() {
        animateSyncIcon();

        if (Utils.isOnline(this)) {
            // updates product info, after finish user is not blocked anymore and image sync in background is started
            AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
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
                    AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            List<Product> products = ProductRepository.getProducts(KPA100Dashboard.this);
                            for (Product p : products) {
                                ApiConnector.updateProductsImages(KPA100Dashboard.this, p.getId());
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            isAlreadySynchronized = true;
                        }
                    });
                }
            });
        } else {
            Toast.makeText(KPA100Dashboard.this, R.string.without_connection, Toast.LENGTH_SHORT).show();
            mSyncWrapper.setVisibility(View.GONE);
            mDashboardWrapper.setVisibility(View.VISIBLE);
            mSyncIcon.setAnimation(null);
        }
    }

    private void animateSyncIcon() {
        RotateAnimation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(1200);
        mSyncIcon.startAnimation(anim);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View button = findViewById(R.id.share_photo_wrapper);
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }

        mTracker.setScreenName("KPA100Dashboard");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Dashboard")
                .setAction("Facebook")
                .build());

        Utils.visitFacebook(this, "www.knihaplnaaktivit.cz", "1552685675006861", true);
    }

    // firebase invite
    public void onShareWithFriendsClicked(View view) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Dashboard")
                .setAction("Invite")
                .build());

        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.app_name))
                .setMessage(getString(R.string.share_app_text))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, getString(R.string.invite_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void navigate(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_down, R.anim.stay);
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName(getString(R.string.app_name))
                .setUrl(Uri.parse("http://www.knihaplnaaktivit.cz"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


}
