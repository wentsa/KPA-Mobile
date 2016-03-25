package cz.knihaplnaaktivit.kpa_mobile.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import cz.knihaplnaaktivit.kpa_mobile.utilities.Constants;

public class KPADatabase extends SQLiteOpenHelper {

    public KPADatabase(Context ctx) {
        super(ctx, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    public static abstract class ProductColumns implements BaseColumns {
        public static final String TABLE_NAME = "Product";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_URL = "url";
    }

    public static abstract class ProductImageColumns implements BaseColumns {
        public static final String TABLE_NAME = "ProductImage";

        public static final String COLUMN_NAME_PRODUCT_ID = "product_id";
        public static final String COLUMN_NAME_IMAGE = "image";
    }

    public static abstract class ConstantColumns implements BaseColumns {
        public static final String TABLE_NAME = "Constants";

        public static final String COLUMN_NAME_KEY = "key";
        public static final String COLUMN_NAME_VALUE = "value";
    }

    private static final String CREATE_PRODUCT_TABLE =
            "CREATE TABLE " + ProductColumns.TABLE_NAME + " ("
            + ProductColumns._ID + " INT, "
            + ProductColumns.COLUMN_NAME_NAME + " TEXT, "
            + ProductColumns.COLUMN_NAME_DESCRIPTION + " TEXT, "
            + ProductColumns.COLUMN_NAME_PRICE + " INT, "
            + ProductColumns.COLUMN_NAME_URL + " TEXT);";

    private static final String CREATE_PRODUCT_IMAGE_TABLE =
            "CREATE TABLE " + ProductImageColumns.TABLE_NAME + " ("
                    + ProductImageColumns.COLUMN_NAME_PRODUCT_ID + " INT, "
                    + ProductImageColumns.COLUMN_NAME_IMAGE + " BLOB);";

    private static final String CREATE_CONSTANTS_TABLE =
            "CREATE TABLE " + ConstantColumns.TABLE_NAME + " ("
                    + ConstantColumns.COLUMN_NAME_KEY + " TEXT PRIMARY KEY, "
                    + ConstantColumns.COLUMN_NAME_VALUE + " TEXT);";

    private static final String DELETE_PRODUCT_TABLE = "DROP TABLE IF EXISTS " + ProductColumns.TABLE_NAME;
    private static final String DELETE_PRODUCT_IMAGE_TABLE = "DROP TABLE IF EXISTS " + ProductImageColumns.TABLE_NAME;
    private static final String DELETE_CONSTANTS_TABLE = "DROP TABLE IF EXISTS " + ConstantColumns.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_PRODUCT_IMAGE_TABLE);
        db.execSQL(CREATE_CONSTANTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_PRODUCT_TABLE);
        db.execSQL(DELETE_PRODUCT_IMAGE_TABLE);
        db.execSQL(DELETE_CONSTANTS_TABLE);
        onCreate(db);
    }

}
