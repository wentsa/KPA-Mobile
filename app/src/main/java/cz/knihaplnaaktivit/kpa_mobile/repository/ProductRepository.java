package cz.knihaplnaaktivit.kpa_mobile.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import cz.knihaplnaaktivit.kpa_mobile.model.Product;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Constants;

public class ProductRepository {

    private static final String FILE_NAME = "KPA_products";

    private final String JSON_ID            = "id";
    private final String JSON_NAME          = "name";
    private final String JSON_DESCRIPTION   = "description";
    private final String JSON_PRICE         = "price";
    private final String JSON_URL           = "url";

    private static ProductRepository sInstance;

    public static synchronized ProductRepository getInstance(Context ctx) {
        if(sInstance == null) {
            sInstance = new ProductRepository(ctx);
        }
        return sInstance;
    }

    private final List<Product> mData = new ArrayList<>();
    private final Context mCtx;
    private boolean mInitialized = false;

    private ProductRepository(Context ctx) {
        mCtx = ctx;
        /*mData.add(new Product(
                1,
                "Kniha plná aktivit",
                "Nabízíme vám ručně vyráběnou Knihu plnou aktivit, která je určena dětem od 3 let, avšak některé úkoly zvládají i mladší děti.\n" +
                        "\n" +
                        "Tato didaktická pomůcka vychází z principů strukturovaného učení a nese prvky používané v rámci Montessori pedagogiky. Je vhodná i pro děti se specifickými vzdělávacími potřebami.\n" +
                        "\n" +
                        "Kniha je tvořena 12 jednotlivými laminovanými pracovními listy, které obsahují úkoly zaměřené např. na rozvíjení jemné motoriky, poznávání a třídění obrázků (podle barev, tvarů apod.), tvorbu řad či rozřazení do skupin podle logických souvislostí.\n" +
                        "Listy jsou doplněny různými materiály pro zvýšení atraktivity pro děti, jakými jsou např. barevné kolíčky, pompony, stuhy. Řada z nich je opatřena obrázky se suchým zipem, který dětem přináší další možnosti práce s nimi. Jednotlivé stránky jsou řazeny v kroužkovém pořadači, což umožňuje snadnější manipulaci s nimi.\n" +
                        "\n" +
                        "Tuto Knihu plnou aktivit lze doplnit našimi dalšími pomůckami.\n" +
                        "\n" +
                        "Při objednání více kusů či různých druhů didaktických pomůcek se poštovné a balné odvíjí od tarifů České pošty.\n" +
                        "Použité materiály se mohou lišit od vyobrazených.",
                440,
                "http://knihaplnaaktivit.cz/kniha-plna-aktivit/"));
        mData.add(new Product(
                2,
                "Abeceda",
                "Abeceda je určena dětem od 3 let, tvoří ji 6 pracovních zalaminovaných listů velikosti A4 a 54 malých obrázkových kartiček. Děti rozřazují obrázkové kartičky podle počátečního písmena ke stejnému písmenu na pracovním listě pomocí suchého zipu.\n" +
                        "\n" +
                        " \n" +
                        "\n" +
                        "Při objednání více kusů či různých druhů didaktické pomůcek se poštovné a balné odvíjí od tarifů České pošty.",
                200,
                "http://knihaplnaaktivit.cz/abeceda/"));
        mData.add(new Product(3,
                "Velikonoce",
                "Řada pracovních listů zaměřených na období jara, kterými můžete doplnit svou Knihu plnou aktivit, či s nimi pracovat samostatně.\n" +
                        "\n" +
                        "Pomůcka je určena dětem od 3 let. Ručně vyráběné listy jsou zalaminovány a úkoly se plné zejména přiřazováním správných řešení pomocí suchých zipů. Zaměření této didaktické pomůcky je na rozvoj motoriky a hledání logických posloupností.\n" +
                        "\n" +
                        "Použité materiály se mohou lišit od vyobrazených.\n" +
                        "\n" +
                        "Při objednání více kusů či různých didaktických pomůcek se poštovné a balné odvíjí od tarifů České pošty.",
                350,
                "http://knihaplnaaktivit.cz/velikonoce/"));
        mData.add(new Product(4, "Pomucka 1", "Popis", 100, ""));
        mData.add(new Product(5, "Pomucka 2", "Popis", 100, ""));
        mData.add(new Product(6, "Pomucka 3", "Popis", 100, ""));
        mData.add(new Product(7, "Pomucka 4", "Popis", 100, ""));
        mData.add(new Product(8, "Pomucka 5", "Popis", 100, ""));*/
    }

    public List<Product> getProducts() {
        if(!mInitialized) {
            initialize();
        }
        return mData;
    }

    public @Nullable Product getProduct(int id) {
        if(!mInitialized) {
            initialize();
        }
        for(Product p : mData) {
            if(p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    private void initialize() {
        SQLiteDatabase db = new KPADatabase(mCtx).getReadableDatabase();

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

        if(c.moveToFirst()) {
            do {
                int id              = c.getInt(c.getColumnIndex(KPADatabase.ProductColumns._ID));
                String name         = c.getString(c.getColumnIndex(KPADatabase.ProductColumns.COLUMN_NAME_NAME));
                String description  = c.getString(c.getColumnIndex(KPADatabase.ProductColumns.COLUMN_NAME_DESCRIPTION));
                int price           = c.getInt(c.getColumnIndex(KPADatabase.ProductColumns.COLUMN_NAME_PRICE));
                String url          = c.getString(c.getColumnIndex(KPADatabase.ProductColumns.COLUMN_NAME_URL));

                mData.add(new Product(id, name, description, price, url));
            } while (c.moveToNext());
        }

        c.close();

        mInitialized = true;
    }


}
