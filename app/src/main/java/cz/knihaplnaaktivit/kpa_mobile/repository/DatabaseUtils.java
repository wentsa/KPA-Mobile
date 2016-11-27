package cz.knihaplnaaktivit.kpa_mobile.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseUtils {

    private static final String PRODUCT_INFO_VERSION_KEY = "product_info_version";
    private static final String PRODUCT_IMAGE_VERSION_KEY_PREFIX = "product_image_version_";

    public static int getProductVersionInfo(SQLiteDatabase db) {
        String[] projection = {
                KPADatabase.ConstantColumns.COLUMN_NAME_VALUE
        };

        Cursor c = db.query(
                KPADatabase.ConstantColumns.TABLE_NAME,
                projection,
                KPADatabase.ConstantColumns.COLUMN_NAME_KEY + "='" + PRODUCT_INFO_VERSION_KEY + "'",
                null,
                null,
                null,
                null
        );

        if(c.getCount() == 0) {
            return 0;
        }
        c.moveToFirst();
        int res = Integer.valueOf(c.getString(c.getColumnIndex(KPADatabase.ConstantColumns.COLUMN_NAME_VALUE)));

        c.close();

        return res;
    }

    public static void setProductVersionInfo(SQLiteDatabase db, int version) {
        String query =
                "INSERT OR REPLACE INTO " + KPADatabase.ConstantColumns.TABLE_NAME + " (" + KPADatabase.ConstantColumns.COLUMN_NAME_KEY + "," + KPADatabase.ConstantColumns.COLUMN_NAME_VALUE + ") " +
                "VALUES ( '" + PRODUCT_INFO_VERSION_KEY + "'," +
                    "'" + version + "'" +
                ");";
        db.execSQL(query);
    }

    public static int getProductImageVersionInfo(SQLiteDatabase db, int productId) {
        String[] projection = {
                KPADatabase.ConstantColumns.COLUMN_NAME_VALUE
        };

        Cursor c = db.query(
                KPADatabase.ConstantColumns.TABLE_NAME,
                projection,
                KPADatabase.ConstantColumns.COLUMN_NAME_KEY + "='" + PRODUCT_IMAGE_VERSION_KEY_PREFIX + productId + "'",
                null,
                null,
                null,
                null
        );

        if(c.getCount() == 0) {
            return 0;
        }
        c.moveToFirst();
        int res = Integer.valueOf(c.getString(c.getColumnIndex(KPADatabase.ConstantColumns.COLUMN_NAME_VALUE)));

        c.close();

        return res;
    }

    public static void setProductImageVersionInfo(SQLiteDatabase db, int version, int productId) {
        String query =
                "INSERT OR REPLACE INTO " + KPADatabase.ConstantColumns.TABLE_NAME + " (" + KPADatabase.ConstantColumns.COLUMN_NAME_KEY + "," + KPADatabase.ConstantColumns.COLUMN_NAME_VALUE + ") " +
                        "VALUES ( '" + PRODUCT_IMAGE_VERSION_KEY_PREFIX + productId + "'," +
                        "'" + version + "'" +
                        ");";
        db.execSQL(query);
    }

}
