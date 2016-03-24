package cz.knihaplnaaktivit.kpa_mobile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.knihaplnaaktivit.kpa_mobile.adapters.KPA200ExpendableListAdapter;
import cz.knihaplnaaktivit.kpa_mobile.model.Product;
import cz.knihaplnaaktivit.kpa_mobile.repository.ProductRepository;

public class KPA200Summary extends AppCompatActivity {

    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;

    @Bind(R.id.list)
    ExpandableListView mList;

    private int mPrevExpanded = -1;

    private KPA200ExpendableListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpa200_summary);

        ButterKnife.bind(this);

        mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);

        try {
            List<Product> products = ProductRepository.getInstance(this).getProducts();

            mAdapter = new KPA200ExpendableListAdapter(this, products);
            mList.setAdapter(mAdapter);

            mList.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        } catch (ProductRepository.NotInitializedException e) {
            mList.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);

            new InitializeProductsTask().execute();
        }
        // only one expanded at time
        mList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(mPrevExpanded != -1 && mPrevExpanded != groupPosition) {
                    mList.collapseGroup(mPrevExpanded);
                }
                mPrevExpanded = groupPosition;
            }
        });

        // detail click
        mList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Product selectedItem = (Product) mAdapter.getGroup(groupPosition);

                Intent intent = new Intent(KPA200Summary.this, KPA201ProductDetail.class);
                intent.putExtra(KPA201ProductDetail.ITEM_ID_KEY, selectedItem.getId());
                KPA200Summary.this.startActivity(intent);
                overridePendingTransition(R.anim.slide_left, R.anim.stay);
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_up);
    }

    private class InitializeProductsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ProductRepository.getInstance(KPA200Summary.this).initialize();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                List<Product> products = ProductRepository.getInstance(KPA200Summary.this).getProducts();

                mAdapter = new KPA200ExpendableListAdapter(KPA200Summary.this, products);
                mList.setAdapter(mAdapter);

                mList.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            } catch (ProductRepository.NotInitializedException ignored) {}
        }
    }
}
