package com.data.volodymyr.notecase.entity;

/**
 * Created by volodymyr on 15.11.15.
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String idToken;
    private String authToken;
    private boolean owner;
    private boolean dirty = true;

    public User() {}

    public User(int id, String name, String email, boolean owner) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.owner = owner;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", idToken='" + idToken + '\'' +
                ", authToken='" + authToken + '\'' +
                ", owner=" + owner +
                ", dirty=" + dirty +
                '}';
    }
}
