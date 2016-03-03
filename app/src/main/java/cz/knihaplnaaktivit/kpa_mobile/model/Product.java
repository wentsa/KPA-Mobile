package cz.knihaplnaaktivit.kpa_mobile.model;

public class Product {

    private final int mId;
    private final String mName;
    private final String mDescription;
    private final int mPrice;

    public Product(int id, String name, String description, int price) {
        mId = id;
        mName = name;
        mDescription = description;
        mPrice = price;
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
}
