package cz.knihaplnaaktivit.kpa_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

import cz.knihaplnaaktivit.kpa_mobile.adapters.KPA200ExpendableListAdapter;
import cz.knihaplnaaktivit.kpa_mobile.model.Product;
import cz.knihaplnaaktivit.kpa_mobile.repository.ProductRepository;

public class KPA200Summary extends AppCompatActivity {

    ExpandableListView mList;
    TextView mPlaceholder;

    private int mPrevExpanded = -1;

    private KPA200ExpendableListAdapter mAdapter;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpa200_summary);

        mList = (ExpandableListView) findViewById(R.id.list);
        mPlaceholder = (TextView) findViewById(R.id.placeholder_summary);

        KPAApplication application = (KPAApplication) getApplication();
        mTracker = application.getDefaultTracker();

        List<Product> products = ProductRepository.getProducts(this);
        if(products.isEmpty()) {
            mPlaceholder.setVisibility(View.VISIBLE);
            mList.setVisibility(View.GONE);
        } else {
            mPlaceholder.setVisibility(View.GONE);
            mList.setVisibility(View.VISIBLE);
        }

        mAdapter = new KPA200ExpendableListAdapter(this, products);
        mList.setAdapter(mAdapter);

        mList.setVisibility(View.VISIBLE);

        // only one expanded at time
        mList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (mPrevExpanded != -1 && mPrevExpanded != groupPosition) {
                    mList.collapseGroup(mPrevExpanded);
                }
                mPrevExpanded = groupPosition;

                Product selectedItem = (Product) mAdapter.getGroup(groupPosition);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Summary")
                        .setAction("Group expand")
                        .setCustomDimension(1, selectedItem.getName())
                        .build());
            }
        });

        // detail click
        mList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Product selectedItem = (Product) mAdapter.getGroup(groupPosition);

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Summary")
                        .setAction("Child click")
                        .setCustomDimension(1, selectedItem.getName())
                        .build());

                Intent intent = new Intent(KPA200Summary.this, KPA201ProductDetail.class);
                intent.putExtra(KPA201ProductDetail.ITEM_ID_KEY, selectedItem.getId());
                KPA200Summary.this.startActivity(intent);
                overridePendingTransition(R.anim.slide_left, R.anim.stay);
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("KPA200Summary");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_up);
    }
}
