package cz.knihaplnaaktivit.kpa_mobile;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.kobakei.ratethisapp.RateThisApp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.knihaplnaaktivit.kpa_mobile.connectors.ApiConnector;
import cz.knihaplnaaktivit.kpa_mobile.model.Product;
import cz.knihaplnaaktivit.kpa_mobile.repository.ProductRepository;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Utils;

public class KPA100Dashboard extends AppCompatActivity {

    private static final String REMOTE_CONFIG_SHOW_INFO = "remote_config_show_info";
    private static final String REMOTE_CONFIG_INFO_MESSAGE_TITLE = "remote_config_info_message_title";
    private static final String REMOTE_CONFIG_INFO_MESSAGE = "remote_config_info_message";
    private static final String REMOTE_CONFIG_INFO_LINK = "remote_config_info_link";
    private static final String REMOTE_CONFIG_INFO_LINK_DATA = "remote_config_info_link_data";
    private static final String REMOTE_CONFIG_INFO_MESSAGE_COLOR_BACKGROUND = "remote_config_info_message_color_background";
    private static final String REMOTE_CONFIG_INFO_MESSAGE_COLOR_TITLE = "remote_config_info_message_color_title";
    private static final String REMOTE_CONFIG_INFO_MESSAGE_COLOR = "remote_config_info_message_color";

    private final Map<String, Object> remoteConfigLinks = new HashMap<String, Object>() {{
        put("productList", KPA200Summary.class);
        put("productDetail", new Runnable() {
            @Override
            public void run() {
                long productId = FirebaseRemoteConfig.getInstance().getLong(REMOTE_CONFIG_INFO_LINK_DATA);
                Intent intent = new Intent(KPA100Dashboard.this, KPA201ProductDetail.class);
                intent.putExtra(KPA201ProductDetail.ITEM_ID_KEY, (int) productId);
                KPA100Dashboard.this.startActivity(intent);
                overridePendingTransition(R.anim.slide_left, R.anim.stay);
            }
        });
        put("contactUs", KPA300ContactUs.class);
        put("sharePhoto", KPA400PhotoShare.class);
        put("about", KPA500About.class);
        put("facebook", new Runnable() {
            @Override
            public void run() {
                onBtnAboutClicked(null);
            }
        });
        put("share", new Runnable() {
            @Override
            public void run() {
                onShareWithFriendsClicked(null);
            }
        });
    }};
    
    private static boolean isAlreadySynchronized = false;

    RelativeLayout mSyncWrapper;
    ScrollView mDashboardWrapper;
    ImageView mSyncIcon;

    private GoogleApiClient client;

    private static final int REQUEST_INVITE = 10;
    private CardView mInfoView;
    private TextView mInfoViewTitle;
    private TextView mInfoViewText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpa100_dashboard);

        mSyncWrapper = (RelativeLayout) findViewById(R.id.sync_wrapper);
        mDashboardWrapper = (ScrollView) findViewById(R.id.dashboard_wrapper);
        mSyncIcon = (ImageView) findViewById(R.id.sync_icon);
        mInfoView = (CardView) findViewById(R.id.info_view);
        mInfoViewTitle = (TextView) findViewById(R.id.info_view_title);
        mInfoViewText = (TextView) findViewById(R.id.info_view_text);

        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("testOnly");

        if (!isAlreadySynchronized) {
            synchronize();
        } else {
            mSyncWrapper.setVisibility(View.GONE);
            mDashboardWrapper.setVisibility(View.VISIBLE);
        }
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        FirebaseRemoteConfig.getInstance().setConfigSettings(configSettings);

        FirebaseRemoteConfig.getInstance().setDefaults(new HashMap<String, Object>() {{
            put(REMOTE_CONFIG_SHOW_INFO, false);
            put(REMOTE_CONFIG_INFO_MESSAGE_TITLE, "");
            put(REMOTE_CONFIG_INFO_MESSAGE, "");
            put(REMOTE_CONFIG_INFO_LINK, "");
            put(REMOTE_CONFIG_INFO_LINK_DATA, "");
            put(REMOTE_CONFIG_INFO_MESSAGE_COLOR_BACKGROUND, "");
            put(REMOTE_CONFIG_INFO_MESSAGE_COLOR_TITLE, "");
            put(REMOTE_CONFIG_INFO_MESSAGE_COLOR, "");
        }});

        long remoteConfigCacheExpirySeconds = BuildConfig.DEBUG ? 0 : 3600;
        FirebaseRemoteConfig.getInstance().fetch(remoteConfigCacheExpirySeconds).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseRemoteConfig.getInstance().activateFetched();
                KPA100Dashboard.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reloadInfoView();
                    }
                });
            }
        });

        RateThisApp.Config rateThisAppConfig = new RateThisApp.Config(5, 6);
        RateThisApp.init(rateThisAppConfig);
        RateThisApp.onCreate(this);
        if (Utils.isOnline(this)) {
            RateThisApp.showRateDialogIfNeeded(this);
        }
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

        reloadInfoView();

        View button = findViewById(R.id.share_photo_wrapper);
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    private void reloadInfoView() {
        if (FirebaseRemoteConfig.getInstance().getBoolean(REMOTE_CONFIG_SHOW_INFO)) {
            String title = FirebaseRemoteConfig.getInstance().getString(REMOTE_CONFIG_INFO_MESSAGE_TITLE);
            String text = FirebaseRemoteConfig.getInstance().getString(REMOTE_CONFIG_INFO_MESSAGE);
            if (title != null && title.length() > 0) {
                mInfoViewTitle.setText(title);

                String colorTitle = FirebaseRemoteConfig.getInstance().getString(REMOTE_CONFIG_INFO_MESSAGE_COLOR_TITLE);
                if (colorTitle != null && colorTitle.length() > 0) {
                    try {
                        mInfoViewTitle.setTextColor(Color.parseColor(colorTitle));
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                }
                mInfoViewTitle.setVisibility(View.VISIBLE);
            } else {
                mInfoViewTitle.setVisibility(View.GONE);
            }
            if (text != null && text.length() > 0) {
                mInfoViewText.setText(text);
                String color = FirebaseRemoteConfig.getInstance().getString(REMOTE_CONFIG_INFO_MESSAGE_COLOR);
                if (color != null && color.length() > 0) {
                    try {
                        mInfoViewText.setTextColor(Color.parseColor(color));
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                }
                mInfoViewText.setVisibility(View.VISIBLE);
            } else {
                mInfoViewText.setVisibility(View.GONE);
            }

            String colorBackground = FirebaseRemoteConfig.getInstance().getString(REMOTE_CONFIG_INFO_MESSAGE_COLOR_BACKGROUND);

            if (colorBackground != null && colorBackground.length() > 0) {
                try {
                    mInfoView.setCardBackgroundColor(Color.parseColor(colorBackground));
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }

            mInfoView.setVisibility(View.VISIBLE);
        } else {
            mInfoView.setVisibility(View.GONE);
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
        FirebaseAnalytics.getInstance(this).logEvent("FACEBOOK_CLICKED", null);
        Utils.visitFacebook(this, "www.knihaplnaaktivit.cz", "1552685675006861", true);
    }

    // firebase invite
    public void onShareWithFriendsClicked(View view) {
        FirebaseAnalytics.getInstance(this).logEvent("INVITE_CLICKED", null);
        Intent intent = new AppInviteInvitation.IntentBuilder("KPA")
                .setMessage(getString(R.string.share_app_text))
                .setCallToActionText(getString(R.string.share_app_button_text))
                .setDeepLink(Uri.parse(getString(R.string.share_app_deep_link)))
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


    public void onInfoViewClicked(View view) {
        String link = FirebaseRemoteConfig.getInstance().getString(REMOTE_CONFIG_INFO_LINK);
        if (FirebaseRemoteConfig.getInstance().getBoolean(REMOTE_CONFIG_SHOW_INFO) && link != null && link.length() > 0) {
            FirebaseAnalytics.getInstance(this).logEvent("INFO_VIEW_CLICKED", null);
            if (remoteConfigLinks.containsKey(link)) {
                Object value = remoteConfigLinks.get(link);
                if (value instanceof Class) {
                    navigate((Class) value);
                } else if (value instanceof Runnable) {
                    ((Runnable) value).run();
                }
            } else {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(intent);
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }
        } else {
            FirebaseAnalytics.getInstance(this).logEvent("INFO_VIEW_CLICKED_NO_LINK", null);
        }
    }
}
