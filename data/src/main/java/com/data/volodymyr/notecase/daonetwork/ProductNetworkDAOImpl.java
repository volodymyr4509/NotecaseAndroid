package com.data.volodymyr.notecase.daonetwork;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.data.volodymyr.notecase.entity.Product;
import com.data.volodymyr.notecase.request.GsonRequest;
import com.data.volodymyr.notecase.util.AppProperties;
import com.data.volodymyr.notecase.util.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vkret on 01.02.16.
 */
public class ProductNetworkDAOImpl implements ProductNetworkDAO {
    private static final String TAG = "ProductNetworkDAOImpl";

    private Context context;

    public ProductNetworkDAOImpl(Context context) {
        this.context = context;
    }

    @Override
    public void getProduct(int id, Response.Listener success, Response.ErrorListener error) {
        Product product = null;
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/get/" + id;
        Log.i(TAG, "Get product, url: " + url + ", product: " + id);
        Map<String, String> headers = new HashMap<>();
        GsonRequest<Product> gsonRequest = new GsonRequest<>(Request.Method.PUT, url, Product.class, headers, success, error, product);

        Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show();

        VolleySingleton.getInstance(context).addToRequestQueue(gsonRequest);
    }

    @Override
    public void updateProduct(Product product, Response.Listener success, Response.ErrorListener error) {
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/update";
        Log.i(TAG, "Update product, url: " + url + ", product: " + product);
        Map<String, String> headers = new HashMap<>();
        GsonRequest<Product> gsonRequest = new GsonRequest<>(Request.Method.PUT, url, Product.class, headers, success, error, product);

        Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show();

        VolleySingleton.getInstance(context).addToRequestQueue(gsonRequest);
    }

    @Override
    public void saveProduct(Product product, Response.Listener success, Response.ErrorListener error) {
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/add";

        Log.i(TAG, "Send product to url: " + url + ", requestLoader body: " + product);
        Map<String, String> headers = new HashMap<>();
        GsonRequest<Product> gsonRequest = new GsonRequest<>(Request.Method.POST, url, Product.class, headers, success, error, product);
        VolleySingleton.getInstance(context).addToRequestQueue(gsonRequest);
    }

    @Override
    public void deleteProduct(int id, Response.Listener success, Response.ErrorListener error) {
        String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/delete/" + id;
        GsonRequest<Product> gsonRequest = new GsonRequest<>(Request.Method.DELETE, url, Product.class, null, success, error, null);
        VolleySingleton.getInstance(context).addToRequestQueue(gsonRequest);
    }
}
