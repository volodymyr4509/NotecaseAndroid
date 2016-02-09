package com.data.volodymyr.notecase.daonetwork;

import android.util.Log;

import com.data.volodymyr.notecase.entity.Product;
import com.data.volodymyr.notecase.request.RequestLoader;
import com.data.volodymyr.notecase.request.RequestLoaderImpl;
import com.data.volodymyr.notecase.util.AppProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.List;

/**
 * Created by volodymyr on 06.02.16.
 */
public class ProductNetworkDAOImpl implements ProductNetworkDAO {
    private static final String TAG = "ProductNetworkDAOImpl";
    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private Gson gson = new GsonBuilder().setDateFormat(TIMESTAMP_PATTERN).create();
    private RequestLoader requestLoader = new RequestLoaderImpl();

    @Override
    public Product getProduct(int id) {
        Product product = null;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/get/" + id;
        Log.i(TAG, "Get product, url: " + url + ", product: " + id);

        try {
            String response = requestLoader.makeGet(url);
            product = gson.fromJson(response, Product.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    @Override
    public boolean updateProduct(Product product) {
        boolean success;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/update";
        Log.i(TAG, "Update product, url: " + url + ", product: " + product);

        String productString = gson.toJson(product);
        try {
            String response = requestLoader.makePut(url, productString.getBytes());
            success = Boolean.valueOf(response);
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }
        return success;
    }

    public boolean addProduct(Product product) {
        boolean success;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/add";
        Log.i(TAG, "Add product, url: " + url + ", product: " + product);


        String productString = gson.toJson(product);
        try {
            String response = requestLoader.makePost(url, productString.getBytes());
            success = Boolean.valueOf(response);
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }
        return success;
    }

    @Override
    public boolean deleteProduct(int id) {
        boolean success;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/delete/" + id;
        Log.i(TAG, "Delete product, url: " + url + ", product id: " + id);
        try {
            String response = requestLoader.makeDelete(url);
            success = Boolean.valueOf(response);
        }catch (Exception e){
            success = false;
            e.printStackTrace();
        }
        return success;
    }

    public List<Product> getProductsSinceUpdateTimestamp(Timestamp lastUpdateTimestamp){
        List<Product> productList = null;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/getupdated/" + lastUpdateTimestamp.getTime();
        Log.i(TAG, "Upload product since" + lastUpdateTimestamp + ", url: " + url);
        try {
            String response = requestLoader.makeGet(url);

            productList = gson.fromJson(response, new TypeToken<List<Product>>(){}.getType());
        }catch (Exception e){
            e.printStackTrace();
        }
        return productList;
    }

}
