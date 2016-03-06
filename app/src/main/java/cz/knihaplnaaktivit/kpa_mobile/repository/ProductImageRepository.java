package cz.knihaplnaaktivit.kpa_mobile.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.knihaplnaaktivit.kpa_mobile.R;
import cz.knihaplnaaktivit.kpa_mobile.model.Product;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Utils;

public class ProductImageRepository {

    private static ProductImageRepository sInstance;

    public static synchronized ProductImageRepository getInstance(Context ctx) {
        if(sInstance == null) {
            sInstance = new ProductImageRepository(ctx);
        }
        return sInstance;
    }

    private final Map<Integer, List<Bitmap>> mData = new HashMap<>();
    private boolean mInitialized = false;
    private final Context mCtx;

    private ProductImageRepository(Context ctx) {
        mCtx = ctx;
    }

    public @Nullable List<Bitmap> getImages(int mId, int height) {
        if(!mInitialized) {
            initialize();
            /*List<Bitmap> kpaBitmaps = new ArrayList<>();
            kpaBitmaps.add(Utils.getScaledBitmap(mCtx, R.drawable.kpa1_small, 0, height));
            kpaBitmaps.add(Utils.getScaledBitmap(mCtx, R.drawable.kpa2_small, 0, height));
            kpaBitmaps.add(Utils.getScaledBitmap(mCtx, R.drawable.kpa3_small, 0, height));
            kpaBitmaps.add(Utils.getScaledBitmap(mCtx, R.drawable.kpa4_small, 0, height));
            kpaBitmaps.add(Utils.getScaledBitmap(mCtx, R.drawable.kpa5_small, 0, height));
            kpaBitmaps.add(Utils.getScaledBitmap(mCtx, R.drawable.kpa6_small, 0, height));
            mData.put(1, kpaBitmaps);
            mInitialized = true;*/
        }
        return mData.get(mId);
    }

    private void initialize() {
        SQLiteDatabase db = new KPADatabase(mCtx).getReadableDatabase();

        String[] projection = {
                KPADatabase.ProductImageColumns.COLUMN_NAME_PRODUCT_ID,
                KPADatabase.ProductImageColumns.COLUMN_NAME_IMAGE
        };

        Cursor c = db.query(
                KPADatabase.ProductImageColumns.TABLE_NAME,  // The table to query
                projection,                             // The columns to return
                null,                                   // The columns for the WHERE clause
                null,                                   // The values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                null                                    // The sort order
        );

        if(c.moveToFirst()) {
            do {
                int productId       = c.getInt(c.getColumnIndex(KPADatabase.ProductImageColumns.COLUMN_NAME_PRODUCT_ID));
                byte[] imgBytes     = c.getBlob(c.getColumnIndex(KPADatabase.ProductImageColumns.COLUMN_NAME_IMAGE));
                Bitmap image        = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);

                List<Bitmap> list;
                if(mData.containsKey(productId)) {
                    list = mData.get(productId);
                } else {
                    list = new ArrayList<>();
                    mData.put(productId, list);
                }
                list.add(image);

            } while (c.moveToNext());
        }

        c.close();

        mInitialized = true;
    }


}
