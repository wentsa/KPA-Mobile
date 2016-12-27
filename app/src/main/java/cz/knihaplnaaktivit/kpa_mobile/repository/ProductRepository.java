package cz.knihaplnaaktivit.kpa_mobile.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import cz.knihaplnaaktivit.kpa_mobile.model.Product;

public class ProductRepository {

    public static List<Product> getProducts(Context ctx) {
        synchronized (KPADatabase.DB_LOCK) {
            SQLiteDatabase db = new KPADatabase(ctx).getReadableDatabase();

            String[] projection = {KPADatabase.ProductColumns._ID,
                    KPADatabase.ProductColumns.COLUMN_NAME_NAME,
                    KPADatabase.ProductColumns.COLUMN_NAME_DESCRIPTION,
                    KPADatabase.ProductColumns.COLUMN_NAME_PRICE,
                    KPADatabase.ProductColumns.COLUMN_NAME_URL
            };

            Cursor c = db.query(
                    KPADatabase.ProductColumns.TABLE_NAME,  // The table to query
                    projection,                             // The columns to return
                    null,                                   // The columns for the WHERE clause
                    null,                                   // The values for the WHERE clause
                    null,                                   // don't group the rows
                    null,                                   // don't filter by row groups
                    null                                    // The sort order
            );

            List<Product> res = new ArrayList<>();

            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(c.getColumnIndex(KPADatabase.ProductColumns._ID));
                    String name = c.getString(c.getColumnIndex(KPADatabase.ProductColumns.COLUMN_NAME_NAME));
                    String description = c.getString(c.getColumnIndex(KPADatabase.ProductColumns.COLUMN_NAME_DESCRIPTION));
                    int price = c.getInt(c.getColumnIndex(KPADatabase.ProductColumns.COLUMN_NAME_PRICE));
                    String url = c.getString(c.getColumnIndex(KPADatabase.ProductColumns.COLUMN_NAME_URL));

                    res.add(new Product(id, name, description, price, url));
                } while (c.moveToNext());
            }

            c.close();

            db.close();

            return res;
        }
    }

    public static @Nullable Product getProduct(Context ctx, int productId) {
        synchronized (KPADatabase.DB_LOCK) {
            SQLiteDatabase db = new KPADatabase(ctx).getReadableDatabase();

            String[] projection = {KPADatabase.ProductColumns._ID,
                    KPADatabase.ProductColumns.COLUMN_NAME_NAME,
                    KPADatabase.ProductColumns.COLUMN_NAME_DESCRIPTION,
                    KPADatabase.ProductColumns.COLUMN_NAME_PRICE,
                    KPADatabase.ProductColumns.COLUMN_NAME_URL
            };

            Cursor c = db.query(
                    KPADatabase.ProductColumns.TABLE_NAME,
                    projection,
                    KPADatabase.ProductColumns._ID + "=" + productId,
                    null,
                    null,
                    null,
                    null
            );

            Product res = null;

            if (c.moveToFirst()) {
                int id = c.getInt(c.getColumnIndex(KPADatabase.ProductColumns._ID));
                String name = c.getString(c.getColumnIndex(KPADatabase.ProductColumns.COLUMN_NAME_NAME));
                String description = c.getString(c.getColumnIndex(KPADatabase.ProductColumns.COLUMN_NAME_DESCRIPTION));
                int price = c.getInt(c.getColumnIndex(KPADatabase.ProductColumns.COLUMN_NAME_PRICE));
                String url = c.getString(c.getColumnIndex(KPADatabase.ProductColumns.COLUMN_NAME_URL));

                res = new Product(id, name, description, price, url);
            }

            c.close();

            db.close();

            return res;
        }
    }
}
