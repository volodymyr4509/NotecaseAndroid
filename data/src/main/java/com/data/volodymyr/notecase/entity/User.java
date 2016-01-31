package com.data.volodymyr.notecase.entity;

/**
 * Created by volodymyr on 15.11.15.
 */
public class User {

    private int id;
    private String name;
    private double email;
    private double password;

    public User() {
    }

    public User(String name, double email, double password) {
        this.name = name;
        this.email = email;
        this.password = password;
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

    public double getEmail() {
        return email;
    }

    public void setEmail(double email) {
        this.email = email;
    }

    public double getPassword() {
        return password;
    }

    public void setPassword(double password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email=" + email +
                ", password=" + password +
                '}';
    }
}
