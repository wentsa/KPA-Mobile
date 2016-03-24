package cz.knihaplnaaktivit.kpa_mobile.connectors;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.knihaplnaaktivit.kpa_mobile.KPA300ContactUs;
import cz.knihaplnaaktivit.kpa_mobile.connectors.services.ServiceSendImage;
import cz.knihaplnaaktivit.kpa_mobile.connectors.services.ServiceSendMessage;
import cz.knihaplnaaktivit.kpa_mobile.model.Product;
import cz.knihaplnaaktivit.kpa_mobile.repository.DatabaseUtils;
import cz.knihaplnaaktivit.kpa_mobile.repository.KPADatabase;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Network;

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
     * @param actualVersion current stored version of product info
     * @return updated products or null
     */
    @Nullable
    public static List<Product> fetchProducts(Context ctx, int actualVersion) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("version", String.valueOf(actualVersion));

            String response = Network.doGet("get_product_info.php", params);
            if(response != null) {
                JSONObject json = new JSONObject(response);

                boolean isUpToDate = json.getBoolean("isUpToDate");
                if(isUpToDate) {
                    return null;
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
                return productsNew;
            }

        } catch (IOException | JSONException e) {}
        return null;
    }

    private static void replaceProductsInDb(List<Product> productsNew, int version, Context ctx) {
        SQLiteDatabase db = new KPADatabase(ctx).getWritableDatabase();

        db.delete(KPADatabase.ProductColumns.TABLE_NAME, null, null);

        for (Product p : productsNew) {
            ContentValues values = new ContentValues();
            values.put(KPADatabase.ProductColumns._ID, p.getId());
            values.put(KPADatabase.ProductColumns.COLUMN_NAME_NAME, p.getName());
            values.put(KPADatabase.ProductColumns.COLUMN_NAME_DESCRIPTION, p.getDescription());
            values.put(KPADatabase.ProductColumns.COLUMN_NAME_PRICE, p.getPrice());
            values.put(KPADatabase.ProductColumns.COLUMN_NAME_URL, p.getWebUrl());

            db.insert(KPADatabase.ProductColumns.TABLE_NAME, null, values);
        }

        DatabaseUtils.setProductVersionInfo(db, version);
    }

    public static void synchronize(Context ctx) {
        SQLiteDatabase db = new KPADatabase(ctx).getWritableDatabase();
        int actualVersion = DatabaseUtils.getProductVersionInfo(db);
        fetchProducts(ctx, actualVersion);
    }
}
