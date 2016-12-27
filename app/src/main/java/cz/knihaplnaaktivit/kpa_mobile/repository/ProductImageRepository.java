package cz.knihaplnaaktivit.kpa_mobile.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

public class ProductImageRepository {


    public static List<Bitmap> getImages(Context ctx, int mId) {
        synchronized (KPADatabase.DB_LOCK) {
            SQLiteDatabase db = new KPADatabase(ctx).getReadableDatabase();

            String[] projection = {
                    KPADatabase.ProductImageColumns.COLUMN_NAME_IMAGE
            };

            Cursor c = db.query(
                    KPADatabase.ProductImageColumns.TABLE_NAME,
                    projection,
                    KPADatabase.ProductImageColumns.COLUMN_NAME_PRODUCT_ID + "=" + mId,
                    null,
                    null,
                    null,
                    KPADatabase.ProductImageColumns.COLUMN_NAME_IMAGE_ORDER
            );

            List<Bitmap> list = new ArrayList<>();

            if (c.moveToFirst()) {
                do {
                    byte[] imgBytes = c.getBlob(c.getColumnIndex(KPADatabase.ProductImageColumns.COLUMN_NAME_IMAGE));
                    Bitmap image = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);

                    list.add(image);

                } while (c.moveToNext());
            }

            c.close();

            db.close();

            return list;
        }
    }
}
