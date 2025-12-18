package src.model;

import java.time.LocalDateTime;
import java.util.List;

public class Sale {

    // FIELDS
    private int id;
    private LocalDateTime saleDate;
    private double subtotal;
    private double tax;
    private double total;
    private List<SaleItem> items;

    // CONSTRUCTOR
    public Sale() {
        this.saleDate = LocalDateTime.now();
    }

    public Sale(int id, LocalDateTime saleDate, double subtotal, double tax, double total) {
        this.id = id;
        this.saleDate = saleDate;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
    }

    // METHODS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items = items;
    }

    public void calculateTotal() {
        subtotal = 0;
        if (items != null) {
            for (SaleItem item : items) {
                subtotal += item.getSubtotal();
            }
        }
        total = subtotal + tax;
    }
}
