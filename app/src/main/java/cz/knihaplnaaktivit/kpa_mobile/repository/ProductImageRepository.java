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

public class ProductImageRepository {

    private static ProductImageRepository sInstance;

    public static synchronized ProductImageRepository getInstance(Context ctx) {
        if(sInstance == null) {
            sInstance = new ProductImageRepository(ctx);
        }
        return sInstance;
    }

    private final Map<Integer, List<Bitmap>> mData = new HashMap<>();

    private ProductImageRepository(Context ctx) {
        List<Bitmap> kpaBitmaps = new ArrayList<>();
        kpaBitmaps.add(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.kpa1_small));
        kpaBitmaps.add(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.kpa2_small));
        kpaBitmaps.add(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.kpa3_small));
        kpaBitmaps.add(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.kpa4_small));
        kpaBitmaps.add(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.kpa5_small));
        kpaBitmaps.add(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.kpa6_small));
        mData.put(1, kpaBitmaps);
    }

    public @Nullable List<Bitmap> getImages(int mId) {
        return mData.get(mId);
    }
}
