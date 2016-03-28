package com.data.volodymyr.notecase.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by volodymyr on 25.10.15.
 */
public class Product implements Serializable{

    private String uuid;
    private int categoryId;
    private int userId;
    private String name;
    private double price;
    private Timestamp created;
    private boolean enabled = true;
    private boolean dirty = true;

    public Product(){
        this.created = new Timestamp(System.currentTimeMillis());
        this.uuid = UUID.randomUUID().toString();
    }

    public Product(int categoryId, int userId, String name, double price) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.name = name;
        this.price = price;
        this.created = new Timestamp(System.currentTimeMillis());
        this.uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
                "uuid=" + uuid +
                ", categoryId=" + categoryId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", created=" + created +
                ", enabled=" + enabled +
                ", dirty=" + dirty +
                '}';
    }
}
