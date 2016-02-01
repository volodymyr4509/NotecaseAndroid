package com.data.volodymyr.notecase.daonetwork;

import com.android.volley.Response;
import com.data.volodymyr.notecase.entity.Product;

/**
 * Created by vkret on 01.02.16.
 */
public interface UserNetworkDAO {
    void getProduct(int id, Response.Listener success, Response.ErrorListener error);
    void updateProduct(Product product, Response.Listener success, Response.ErrorListener error);
    void saveProduct(Product product, Response.Listener success, Response.ErrorListener error);
    void deleteProduct(int id, Response.Listener success, Response.ErrorListener error);
}
