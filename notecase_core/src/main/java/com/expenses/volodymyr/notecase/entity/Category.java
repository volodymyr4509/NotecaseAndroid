package com.expenses.volodymyr.notecase.entity;

import java.util.Objects;

/**
 * Created by volodymyr on 15.11.15.
 */
public class Category {

    private int id;
    private String name;
    private int color;

    public Category() {
    }

    public Category(String name, int color) {
        this.name = name;
        this.color = color;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (color != category.color) return false;
        return !(name != null ? !name.equals(category.name) : category.name != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + color;
        return result;
    }
}
