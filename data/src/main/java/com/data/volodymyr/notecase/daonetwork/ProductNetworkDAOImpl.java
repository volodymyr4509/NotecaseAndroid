package com.data.volodymyr.notecase.daonetwork;

import android.content.Context;
import android.util.Log;

import com.data.volodymyr.notecase.entity.Product;
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
 * Created by volodymyr on 06.02.16.
 */
public class ProductNetworkDAOImpl implements ProductNetworkDAO {
    private static final String TAG = "ProductNetworkDAOImpl";
    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private Gson gson = new GsonBuilder().setDateFormat(TIMESTAMP_PATTERN).create();
    private RequestLoader requestLoader;

    public ProductNetworkDAOImpl(Context context) {
        this.requestLoader = new RequestLoaderImpl(context);
    }

    @Override
    public Product getProduct(int id) {
        Product product = null;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/get/" + id;
        try {
            String response = requestLoader.makeGet(url);
            product = gson.fromJson(response, Product.class);
            Log.i(TAG, "Product uploaded: " + product);
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Cannot upload Product from url: " + url, e);
        }
        return product;
    }

    @Override
    public boolean updateProduct(Product product)  {
        boolean success;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/update";
        String productString = gson.toJson(product);
        try {
            String response = requestLoader.makePut(url, productString.getBytes());
            success = Boolean.valueOf(response);
            Log.i(TAG, "Product updated, url: " + url);
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage());
        } catch (Exception e) {
            success = false;
            Log.e(TAG, "Cannot update Product from url: " + url + ", Product: " + product, e);
        }
        return success;
    }

    public boolean addProduct(Product product)  {
        boolean success;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/add";
        String productString = gson.toJson(product);
        try {
            String response = requestLoader.makePost(url, productString.getBytes());
            success = Boolean.valueOf(response);
            Log.e(TAG, "Product uploaded with url: " + url + ", Product: " + product);
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage());
        } catch (Exception e) {
            success = false;
            Log.e(TAG, "Cannot add Product with url: " + url, e);
        }
        return success;
    }

    @Override
    public boolean deleteProduct(int id)  {
        boolean success;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/delete/" + id;
        try {
            String response = requestLoader.makeDelete(url);
            success = Boolean.valueOf(response);
            Log.i(TAG, "Cannot delete Product with url: " + url);
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage());
        } catch (Exception e) {
            success = false;
            Log.e(TAG, "Cannot delete Product with url: " + url, e);
        }
        return success;
    }

    public List<Product> getProductsSinceUpdateTimestamp(Timestamp lastUpdateTimestamp)  {
        List<Product> productList = null;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/getupdated/" + lastUpdateTimestamp.getTime();
        try {
            String response = requestLoader.makeGet(url);
            productList = gson.fromJson(response, new TypeToken<List<Product>>() {
            }.getType());
            Log.i(TAG, "Product list uploaded from url: " + url);
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Cannot upload Product list from url: " + url, e);
        }
        return productList;
    }

}
