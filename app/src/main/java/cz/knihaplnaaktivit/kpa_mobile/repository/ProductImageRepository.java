package cz.knihaplnaaktivit.kpa_mobile.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.knihaplnaaktivit.kpa_mobile.R;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Utils;

public class ProductImageRepository {

    private static ProductImageRepository sInstance;

    public static synchronized ProductImageRepository getInstance() {
        if(sInstance == null) {
            sInstance = new ProductImageRepository();
        }
        return sInstance;
    }

    private final Map<Integer, List<Bitmap>> mData = new HashMap<>();
    private boolean initialized = false;

    private ProductImageRepository() {}

    public @Nullable List<Bitmap> getImages(Context ctx, int mId, int height) {
        if(!initialized) {
            List<Bitmap> kpaBitmaps = new ArrayList<>();
            kpaBitmaps.add(Utils.getScaledBitmap(ctx, R.drawable.kpa1_small, 0, height));
            kpaBitmaps.add(Utils.getScaledBitmap(ctx, R.drawable.kpa2_small, 0, height));
            kpaBitmaps.add(Utils.getScaledBitmap(ctx, R.drawable.kpa3_small, 0, height));
            kpaBitmaps.add(Utils.getScaledBitmap(ctx, R.drawable.kpa4_small, 0, height));
            kpaBitmaps.add(Utils.getScaledBitmap(ctx, R.drawable.kpa5_small, 0, height));
            kpaBitmaps.add(Utils.getScaledBitmap(ctx, R.drawable.kpa6_small, 0, height));
            mData.put(1, kpaBitmaps);
            initialized = true;
        }
        return mData.get(mId);
    }


}
