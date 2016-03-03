package cz.knihaplnaaktivit.kpa_mobile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;

public class KPA201ProductDetail extends AppCompatActivity {

    public static final String ITEM_ID_KEY = "itemId";

    private int mProductId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpa201_product_detail);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mProductId = extras.getInt(ITEM_ID_KEY);
            Toast.makeText(KPA201ProductDetail.this, "" + mProductId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_right);
    }


}
