package com.data.volodymyr.notecase.daonetwork;

import android.util.Log;

import com.data.volodymyr.notecase.entity.Product;
import com.data.volodymyr.notecase.request.RequestLoader;
import com.data.volodymyr.notecase.request.RequestLoaderImpl;
import com.data.volodymyr.notecase.util.AppProperties;
import com.data.volodymyr.notecase.util.RequestMethod;
import com.google.gson.Gson;

import java.io.IOException;

/**
 * Created by volodymyr on 06.02.16.
 */
public class ProductPiDAOImpl implements ProductPiDAO {
    private static final String TAG = "ProductNetworkDAOImpl";
    private Gson gson = new Gson();
    private RequestLoader requestLoader = new RequestLoaderImpl();

    @Override
    public Product getProduct(int id){
        Product product = null;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/get/" + id;
        Log.i(TAG, "Get product, url: " + url + ", product: " + id);

        try {
            String response = requestLoader.downloadUrl(url, RequestMethod.GET);
            product = gson.fromJson(response, Product.class);
        }catch (IOException e){
            e.printStackTrace();
        }

        return product;
    }

    @Override
    public boolean updateProduct(Product product){
        boolean success = false;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/update";
        Log.i(TAG, "Update product, url: " + url + ", product: " + product);
        try {
            String response = requestLoader.downloadUrl(url, RequestMethod.PUT);
            success = Boolean.valueOf(response);
        }catch (IOException e){
            e.printStackTrace();
        }
        return success;
    }

}
