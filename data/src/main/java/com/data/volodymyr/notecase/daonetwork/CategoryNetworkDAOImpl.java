package com.data.volodymyr.notecase.daonetwork;

import android.content.Context;
import android.util.Log;

import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.request.RequestLoader;
import com.data.volodymyr.notecase.request.RequestLoaderImpl;
import com.data.volodymyr.notecase.util.AppProperties;
import com.data.volodymyr.notecase.util.AuthenticationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by volodymyr on 10.02.16.
 */
public class CategoryNetworkDAOImpl implements CategoryNetworkDAO {
    private static final String TAG = "ProductNetworkDAOImpl";
    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private Gson gson = new GsonBuilder().setDateFormat(TIMESTAMP_PATTERN).create();
    private RequestLoader requestLoader;

    public CategoryNetworkDAOImpl(Context context) {
        requestLoader = new RequestLoaderImpl(context);
    }

    @Override
    public Category getCategory(int id) throws AuthenticationException {
        Category category = null;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/category/get/" + id;

        try {
            String response = requestLoader.makeGet(url);
            category = gson.fromJson(response, Category.class);
            Log.i(TAG, "Category loaded successfully, url: " + url + ", category: " + category);
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Cannot load category by id: " + id, e);
        }
        return category;
    }

    @Override
    public boolean updateCategory(Category category) throws AuthenticationException {
        boolean success;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/category/update";

        String categoryString = gson.toJson(category);
        try {
            String response = requestLoader.makePut(url, categoryString.getBytes());
            success = Boolean.valueOf(response);
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Cannot update Category: " + category, e);
            success = false;
        }
        return success;
    }

    @Override
    public boolean addCategory(Category category) throws AuthenticationException {
        boolean success = false;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/category/add";
        String categoryString = gson.toJson(category);
        try {
            String response = requestLoader.makePost(url, categoryString.getBytes());
            success = Boolean.valueOf(response);
            Log.i(TAG, "Category added with url: " + url + ", Category: " + category);
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Cannot add Category: " + category, e);
        }
        return success;
    }

    @Override
    public boolean deleteCategory(int id) throws AuthenticationException {
        boolean success;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/category/delete/" + id;
        try {
            String response = requestLoader.makeDelete(url);
            success = Boolean.valueOf(response);
            Log.i(TAG, "Category deleted with url: " + url);
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Cannot delete category with url: " + url, e);
            success = false;
        }
        return success;
    }

    @Override
    public List<Category> getCategoriesSinceUpdateTimestamp(Timestamp lastUpdateTimestamp) {
        List<Category> categoryList = null;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/category/getupdated/" + lastUpdateTimestamp.getTime();
        try {
            String response = requestLoader.makeGet(url);
            categoryList = gson.fromJson(response, new TypeToken<List<Category>>() {}.getType());
            Log.i(TAG, "Category list uploaded with url: " + url);
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Cannot upload category list with url: " + url, e);
        }
        return categoryList;
    }

}