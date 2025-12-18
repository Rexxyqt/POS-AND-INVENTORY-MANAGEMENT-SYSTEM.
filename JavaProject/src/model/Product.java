package src.model;

import java.time.LocalDate;

public class Product {

    // FIELDS
    private int id;
    private String name;
    private int categoryId;
    private int brandId;
    private int supplierId;
    private String unit;
    private double costPrice;
    private double markupPercentage;
    private double sellingPrice;
    private int quantity;
    private int stockThreshold;
    private LocalDate dateAdded;

    // CONSTRUCTOR
    public Product() {
    }

    public Product(int id, String name, int categoryId, int brandId, int supplierId,
            String unit, double costPrice, double markupPercentage, double sellingPrice,
            int quantity, LocalDate dateAdded) {
        this.name = name;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.supplierId = supplierId;
        this.unit = unit;
        this.costPrice = costPrice;
        this.markupPercentage = markupPercentage;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.dateAdded = dateAdded;
    }

    // METHODS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
        calculateSellingPrice();
    }

    public double getMarkupPercentage() {
        return markupPercentage;
    }

    public void setMarkupPercentage(double markupPercentage) {
        this.markupPercentage = markupPercentage;
        calculateSellingPrice();
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    private void calculateSellingPrice() {
        if (costPrice > 0 && markupPercentage >= 0) {
            this.sellingPrice = costPrice + (costPrice * markupPercentage / 100.0);
        }
    }

    public double getPrice() {
        return sellingPrice;
    }

    public void setPrice(double price) {
        this.sellingPrice = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStockThreshold() {
        return stockThreshold;
    }

    public void setStockThreshold(int stockThreshold) {
        this.stockThreshold = stockThreshold;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public String toString() {
        return name;
    }
}
