package com.expenses.volodymyr.notecase.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by volodymyr on 25.10.15.
 */
public class Product implements Serializable{

    private int id;
    private int categoryId;
    private int userId;
    private String name;
    private double price;
    private Timestamp created;
    private boolean dirty = true;

    public Product(){}

    public Product(int categoryId, int userId, String name, double price) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.name = name;
        this.price = price;
        this.created = new Timestamp(System.currentTimeMillis());
    }

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", created=" + created +
                ", dirty=" + dirty +
                '}';
    }
}
