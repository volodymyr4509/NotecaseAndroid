package com.data.volodymyr.notecase.daonetwork;

import android.util.Log;

import com.data.volodymyr.notecase.entity.User;
import com.data.volodymyr.notecase.request.RequestLoader;
import com.data.volodymyr.notecase.request.RequestLoaderImpl;
import com.data.volodymyr.notecase.util.AppProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by volodymyr on 12.02.16.
 */
public class UserNetworkDAOImpl implements UserNetworkDAO {
    private static final String TAG = "UserNetworkDAOImpl";
    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private Gson gson = new GsonBuilder().setDateFormat(TIMESTAMP_PATTERN).create();
    private RequestLoader requestLoader = new RequestLoaderImpl();

    @Override
    public boolean updateUser(User user) {
        boolean success;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/user/update";

        String userString = gson.toJson(url);
        try {
            String response = requestLoader.makePut(url, userString.getBytes());
            success = Boolean.valueOf(response);
        } catch (Exception e) {
            Log.e(TAG, "Cannot update User: " + url, e);
            success = false;
        }
        return success;
    }

    @Override
    public String addUser(User user) {
        String authToken = null;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/user/add";
        String userString = gson.toJson(user);
        try {
            authToken = requestLoader.makePost(url, userString.getBytes());
            Log.i(TAG, "User added with url: " + url + ", User: " + user);
        } catch (Exception e) {
            Log.e(TAG, "Cannot add user: " + user, e);
        }
        return authToken;
    }

    @Override
    public String authenticateOwnerUser(String idToken) {
        String authToken = null;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/user/authenticate";
        try {
            authToken = requestLoader.makePost(url, idToken.getBytes());
            Log.i(TAG, "User authentication: " + url + ", retrieved authToken: " + authToken);
        } catch (Exception e) {
            Log.e(TAG, "Cannot authenticate user", e);
        }

        return authToken;
    }

    @Override
    public boolean deleteUser(int id) {
        boolean success;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/user/delete/" + id;
        try {
            String response = requestLoader.makeDelete(url);
            success = Boolean.valueOf(response);
            Log.i(TAG, "User deleted with url: " + url);
        } catch (Exception e) {
            Log.e(TAG, "Cannot delete user with url: " + url, e);
            success = false;
        }
        return success;
    }

    @Override
    public List<User> getAllTrustedUsers(int userId) {
        List<User> userList = null;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/user/getall/" + userId;
        try {
            String response = requestLoader.makeGet(url);
            userList = gson.fromJson(response, new TypeToken<List<User>>() {
            }.getType());
            Log.i(TAG, "User list uploaded with url: " + url);
        } catch (Exception e) {
            Log.e(TAG, "Cannot upload user list with url: " + url, e);
        }
        return userList;
    }

    @Override
    public boolean sendIdToken(String idToken) {
        boolean success;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/user/registeridtoken";
        try {
            String response = requestLoader.makePost(url, idToken.getBytes());
            success = Boolean.valueOf(response);
            Log.i(TAG, "User id token was sent");
        } catch (Exception e) {
            success = false;
            Log.e(TAG, "Cannot send idToken");
        }
        return success;
    }

}
