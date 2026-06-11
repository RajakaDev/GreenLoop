package model;

public class Product {
    private int productId;
    private String name;
    private String category;
    private double price;
    private int ecoRating;
    private int quantity;
    private int reorderLevel;

    public Product(int productId, String name, String category, double price, int ecoRating, int quantity, int reorderLevel) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.ecoRating = ecoRating;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
    }
    public Product(String name, String category,
                   double price, int ecoRating,
                   int quantity, int reorderLevel) {

        this.name = name;
        this.category = category;
        this.price = price;
        this.ecoRating = ecoRating;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
    }

    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getEcoRating() { return ecoRating; }
    public int getQuantity() { return quantity; }
    public int getReorderLevel() { return reorderLevel; }
}