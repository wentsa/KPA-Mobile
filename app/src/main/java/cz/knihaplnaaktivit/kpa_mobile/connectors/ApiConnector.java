package cz.knihaplnaaktivit.kpa_mobile.connectors;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.knihaplnaaktivit.kpa_mobile.KPA201ProductDetail;
import cz.knihaplnaaktivit.kpa_mobile.connectors.services.ServiceSendImage;
import cz.knihaplnaaktivit.kpa_mobile.connectors.services.ServiceSendMessage;
import cz.knihaplnaaktivit.kpa_mobile.model.Product;
import cz.knihaplnaaktivit.kpa_mobile.repository.DatabaseUtils;
import cz.knihaplnaaktivit.kpa_mobile.repository.KPADatabase;
import cz.knihaplnaaktivit.kpa_mobile.repository.ProductRepository;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Network;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Utils;

public class ApiConnector {

    public static void sendImage(Context ctx, String name, String mail, String description, String imagePath) {
        Intent intent = new Intent(ctx, ServiceSendImage.class);
        intent.putExtra(ServiceSendImage.NAME, name);
        intent.putExtra(ServiceSendImage.EMAIL, mail);
        intent.putExtra(ServiceSendImage.DESCRIPTION, description);
        intent.putExtra(ServiceSendImage.IMAGE, imagePath);

        ctx.startService(intent);
    }

    public static void sendMessage(Context ctx, String name, String mail, String subject, String message) {
        Intent intent = new Intent(ctx, ServiceSendMessage.class);
        intent.putExtra(ServiceSendMessage.NAME, name);
        intent.putExtra(ServiceSendMessage.EMAIL, mail);
        intent.putExtra(ServiceSendMessage.SUBJECT, subject);
        intent.putExtra(ServiceSendMessage.MESSAGE, message);

        ctx.startService(intent);
    }

    /**
     * Checks API for newer version of product info. If newer version exists
     * records in DB are replaced by new version and returned from this method.
     * Null is returned otherwise
     * @param ctx context
     */
    public static void updateProductsInfo(Context ctx) {
        int actualVersion;
        synchronized (KPADatabase.DB_LOCK) {
            SQLiteDatabase db = new KPADatabase(ctx).getReadableDatabase();
            actualVersion = DatabaseUtils.getProductVersionInfo(db);
            db.close();
        }

        try {
            Map<String, String> params = new HashMap<>();
            params.put("version", String.valueOf(actualVersion));

            String response = Network.doGet("get_product_info.php", params);
            if(response != null) {
                JSONObject json = new JSONObject(response);

                boolean isUpToDate = json.getBoolean("isUpToDate");
                if(isUpToDate) {
                    return;
                }

                int version = json.getInt("version");

                JSONArray products = json.getJSONArray("products");
                List<Product> productsNew = new ArrayList<>();
                for (int i = 0; i < products.length(); i++) {
                    JSONObject product = products.getJSONObject(i);

                    int id = product.getInt("id");
                    String name = product.getString("name");
                    String description = product.getString("description");
                    int price = product.getInt("price");
                    String url = product.getString("url");

                    productsNew.add(new Product(id, name, description, price, url));
                }

                replaceProductsInDb(productsNew, version, ctx);
            }

        } catch (IOException | JSONException e) {
            Crashlytics.logException(e);
        }
    }

    private static void replaceProductsInDb(List<Product> productsNew, int version, Context ctx) {
        synchronized(KPADatabase.DB_LOCK) {
            SQLiteDatabase db = new KPADatabase(ctx).getWritableDatabase();

            db.delete(KPADatabase.ProductColumns.TABLE_NAME, null, null);

            Set<Integer> idSet = new HashSet<>();

            for (Product p : productsNew) {
                idSet.add(p.getId());

                ContentValues values = new ContentValues();
                values.put(KPADatabase.ProductColumns._ID, p.getId());
                values.put(KPADatabase.ProductColumns.COLUMN_NAME_NAME, p.getName());
                values.put(KPADatabase.ProductColumns.COLUMN_NAME_DESCRIPTION, p.getDescription());
                values.put(KPADatabase.ProductColumns.COLUMN_NAME_PRICE, p.getPrice());
                values.put(KPADatabase.ProductColumns.COLUMN_NAME_URL, p.getWebUrl());

                db.insert(KPADatabase.ProductColumns.TABLE_NAME, null, values);
            }

            DatabaseUtils.setProductVersionInfo(db, version);

            removeUnnecesaryImagesFromDb(db, idSet);

            db.close();
        }
    }

    /**
     * Removes unnecesary images from db (unbound to any product)
     * @param db database
     * @param idSet set of product ids that are used
     */
    private static void removeUnnecesaryImagesFromDb(SQLiteDatabase db, Set<Integer> idSet) {
        String[] projection = {
                KPADatabase.ProductImageColumns.COLUMN_NAME_PRODUCT_ID
        };

        Cursor c = db.query(
                true,
                KPADatabase.ProductImageColumns.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null,
                null
        );

        List<Integer> idToBeRemoved = new ArrayList<>();
        if(c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndex(KPADatabase.ProductImageColumns.COLUMN_NAME_PRODUCT_ID));
                // if table contains ID that is not used anymore, delete it
                if(!idSet.contains(id)) {
                    idToBeRemoved.add(id);
                }
            } while(c.moveToNext());
        }

        c.close();

        for(Integer id : idToBeRemoved) {
            db.delete(KPADatabase.ProductImageColumns.TABLE_NAME, KPADatabase.ProductImageColumns.COLUMN_NAME_PRODUCT_ID + "=" + id, null);
        }
    }

    /**
     * Checks API for newer version of product images. If newer version exists
     * records in DB are replaced by new version
     * @param ctx context
     * @param productId product id
     */
    public static void updateProductsImages(Context ctx, int productId) {
        int actualVersion;

        synchronized (KPADatabase.DB_LOCK) {
            SQLiteDatabase db = new KPADatabase(ctx).getReadableDatabase();
            actualVersion = DatabaseUtils.getProductImageVersionInfo(db, productId);
            db.close();
        }

        try {
            Map<String, String> params = new HashMap<>();
            params.put("version", String.valueOf(actualVersion));
            params.put("productId", String.valueOf(productId));

            String response = Network.doGet("get_product_images.php", params);
            if(response != null) {
                JSONObject json = new JSONObject(response);

                boolean isUpToDate = json.getBoolean("isUpToDate");
                if(isUpToDate) {
                    return;
                }

                int version = json.getInt("version");

                JSONArray images = json.getJSONArray("images");
                List<ImageHolder> imagesNew = new ArrayList<>();
                for (int i = 0; i < images.length(); i++) {
                    JSONObject imageWrap = images.getJSONObject(i);

                    String base64image = imageWrap.getString("image");
                    byte[] image = Base64.decode(base64image, Base64.URL_SAFE);

                    int order = imageWrap.getInt("order");

                    imagesNew.add(new ImageHolder(image, order));
                }

                replaceImagesInDb(imagesNew, version, productId, ctx);

                Intent intent = new Intent(KPA201ProductDetail.PRODUCT_IMAGE_UPDATED_FILTER);
                intent.putExtra("id", productId);
                LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
            }

        } catch (IOException | JSONException e) {
            Crashlytics.logException(e);
        }
    }

    private static void replaceImagesInDb(List<ImageHolder> imagesNew, int version, int productId, Context ctx) {
        synchronized (KPADatabase.DB_LOCK) {
            SQLiteDatabase db = new KPADatabase(ctx).getWritableDatabase();

            db.delete(KPADatabase.ProductImageColumns.TABLE_NAME, KPADatabase.ProductImageColumns.COLUMN_NAME_PRODUCT_ID + "=" + productId, null);

            for (ImageHolder img : imagesNew) {
                ContentValues values = new ContentValues();
                values.put(KPADatabase.ProductImageColumns.COLUMN_NAME_PRODUCT_ID, productId);
                values.put(KPADatabase.ProductImageColumns.COLUMN_NAME_IMAGE, img.image);
                values.put(KPADatabase.ProductImageColumns.COLUMN_NAME_IMAGE_ORDER, img.order);

                db.insert(KPADatabase.ProductImageColumns.TABLE_NAME, null, values);
            }

            DatabaseUtils.setProductImageVersionInfo(db, version, productId);

            db.close();
        }
    }

    public static void synchronize(Context ctx, boolean withImages) {
        updateProductsInfo(ctx);
        List<Product> products = ProductRepository.getProducts(ctx);
        if(withImages) {
            for (Product p : Utils.nullToEmpty(products)) {
                updateProductsImages(ctx, p.getId());
            }
        }
    }

    private static class ImageHolder {
        byte[] image;
        int order;

        private ImageHolder(byte[] image, int order) {
            this.image = image;
            this.order = order;
        }
    }
}
