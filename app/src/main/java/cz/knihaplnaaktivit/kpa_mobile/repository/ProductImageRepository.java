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
            kpaBitmaps.add(getScaledBitmap(ctx, R.drawable.kpa1_small, height));
            kpaBitmaps.add(getScaledBitmap(ctx, R.drawable.kpa2_small, height));
            kpaBitmaps.add(getScaledBitmap(ctx, R.drawable.kpa3_small, height));
            kpaBitmaps.add(getScaledBitmap(ctx, R.drawable.kpa4_small, height));
            kpaBitmaps.add(getScaledBitmap(ctx, R.drawable.kpa5_small, height));
            kpaBitmaps.add(getScaledBitmap(ctx, R.drawable.kpa6_small, height));
            mData.put(1, kpaBitmaps);
            initialized = true;
        }
        return mData.get(mId);
    }

    private Bitmap getScaledBitmap(Context ctx, int id, int height) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(ctx.getResources(), id, bmOptions);
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor;
        if(height == 0) {
            scaleFactor = 1;
        } else {
            scaleFactor = photoH/height;
        }

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeResource(ctx.getResources(), id, bmOptions);
    }
}
