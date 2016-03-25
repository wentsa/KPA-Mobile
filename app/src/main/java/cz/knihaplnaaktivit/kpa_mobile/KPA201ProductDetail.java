package cz.knihaplnaaktivit.kpa_mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.knihaplnaaktivit.kpa_mobile.model.Product;
import cz.knihaplnaaktivit.kpa_mobile.repository.ProductImageRepository;
import cz.knihaplnaaktivit.kpa_mobile.repository.ProductRepository;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Utils;

public class KPA201ProductDetail extends AppCompatActivity {

    public static final String ITEM_ID_KEY = "itemId";

    @Bind(R.id.image_prev_wrapper)
    LinearLayout mImageWrapper;

    @Bind(R.id.image_prev_scroll_wrapper)
    HorizontalScrollView mImageScrollWrapper;

    @Bind(R.id.description)
    TextView mDescription;

    @Bind(R.id.price)
    TextView mPrice;

    private int mProductId;
    private Product mProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpa201_product_detail);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mProductId = extras.getInt(ITEM_ID_KEY);
            loadProductDetail();
        }
    }

    private void loadProductDetail() {
        mProduct = ProductRepository.getProduct(this, mProductId);
        if(mProduct != null) {
            setTitle(mProduct.getName());
            mDescription.setText(mProduct.getDescription());
            mPrice.setText(Utils.getCurrencyFormat(mProduct.getPrice(), getString(R.string.currency)));

            List<Bitmap> images = ProductImageRepository.getImages(this, mProduct.getId());
            if(images.isEmpty()) {
                mImageScrollWrapper.setVisibility(View.GONE);
            } else {
                mImageScrollWrapper.setVisibility(View.VISIBLE);
                for(Bitmap b : images) {
                    ImageView iv = new ImageView(this);
                    iv.setImageBitmap(b);
                    iv.setAdjustViewBounds(true);
                    iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    mImageWrapper.addView(iv, lp);
                }
            }
            }
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
