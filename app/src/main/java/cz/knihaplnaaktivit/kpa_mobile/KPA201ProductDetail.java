package cz.knihaplnaaktivit.kpa_mobile;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cz.knihaplnaaktivit.kpa_mobile.connectors.ApiConnector;
import cz.knihaplnaaktivit.kpa_mobile.model.Product;
import cz.knihaplnaaktivit.kpa_mobile.repository.ProductImageRepository;
import cz.knihaplnaaktivit.kpa_mobile.repository.ProductRepository;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Utils;

public class KPA201ProductDetail extends AppCompatActivity {

    public static final String ITEM_ID_KEY = "itemId";
    static final String PRODUCT_NAME = "productName";

    LinearLayout mImageWrapper;
    View mImageScrollWrapperHorizontal;
    View mImageScrollWrapper;
    TextView mDescription;
    TextView mPrice;

    private int mProductId;
    private Product mProduct;

    public static final String PRODUCT_IMAGE_UPDATED_FILTER = "productImageUpdated";

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int productId = intent.getIntExtra("id", -1);
            if(productId == mProductId) {
                loadProductDetail();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpa201_product_detail);

        mImageWrapper = (LinearLayout) findViewById(R.id.image_prev_wrapper);
        mDescription = (TextView) findViewById(R.id.description);
        mPrice = (TextView) findViewById(R.id.price);

        mImageScrollWrapperHorizontal = findViewById(R.id.image_prev_scroll_wrapper);
        mImageScrollWrapper = findViewById(R.id.image_prev_scroll_wrapper_land);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(PRODUCT_IMAGE_UPDATED_FILTER));

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mProductId = extras.getInt(ITEM_ID_KEY);
            loadProductDetail();
        }

        if(Utils.isOnline(this)) {
            AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    ApiConnector.updateProductsImages(KPA201ProductDetail.this, mProductId);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    loadProductDetail();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private void loadProductDetail() {
        mProduct = ProductRepository.getProduct(this, mProductId);
        if(mProduct != null) {
            setTitle(mProduct.getName());
            mDescription.setText(mProduct.getDescription());
            mPrice.setText(Utils.getCurrencyFormat(mProduct.getPrice(), getString(R.string.currency)));

            mImageWrapper.removeAllViews();
            List<Bitmap> images = ProductImageRepository.getImages(this, mProduct.getId());
            if(images.isEmpty()) {
                if(mImageScrollWrapper != null) {
                    mImageScrollWrapper.setVisibility(View.GONE);
                }
                if(mImageScrollWrapperHorizontal != null) {
                    mImageScrollWrapperHorizontal.setVisibility(View.GONE);
                }
            } else {
                if(mImageScrollWrapper != null) {
                    mImageScrollWrapper.setVisibility(View.VISIBLE);
                }
                if(mImageScrollWrapperHorizontal != null) {
                    mImageScrollWrapperHorizontal.setVisibility(View.VISIBLE);
                }
                for(final Bitmap b : images) {
                    ImageView iv = new ImageView(this);
                    iv.setImageBitmap(b);
                    iv.setAdjustViewBounds(true);
                    iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    LinearLayout.LayoutParams lp;
                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    } else {
                        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }

                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageDialog(b);
                        }
                    });
                    mImageWrapper.addView(iv, lp);
                }
            }
        }
    }

    private void showImageDialog(Bitmap bitmap) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_kpa201_image_detail);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView iv = (ImageView) dialog.findViewById(R.id.image);
        iv.setImageBitmap(bitmap);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.show();

        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_kpa201, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.web: {
                if(mProduct != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mProduct.getWebUrl()));
                    if(intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
                return true;
            }
            case R.id.shop: {
                if(mProduct != null) {
                    Intent intent = new Intent(this, KPA300ContactUs.class);
                    intent.putExtra(PRODUCT_NAME, mProduct.getName());
                    startActivity(intent);
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_right);
    }

}
