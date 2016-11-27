package cz.knihaplnaaktivit.kpa_mobile.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.List;

import cz.knihaplnaaktivit.kpa_mobile.repository.ProductImageRepository;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Utils;

public class Product {

    private final int mId;
    private final String mName;
    private final String mDescription;
    private final int mPrice;
    private final String mWebUrl;

    public Product(int id, String name, String description, int price, String webUrl) {
        mId = id;
        mName = name;
        mDescription = description;
        mPrice = price;
        mWebUrl = webUrl;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getPrice() {
        return mPrice;
    }

    public int getId() {
        return mId;
    }

    public String getWebUrl() {
        return mWebUrl;
    }
}
