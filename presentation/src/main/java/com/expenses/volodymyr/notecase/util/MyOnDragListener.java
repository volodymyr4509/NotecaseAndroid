package com.expenses.volodymyr.notecase.util;

import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.data.volodymyr.notecase.entity.Product;
import com.expenses.volodymyr.notecase.request.GsonRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by volodymyr on 15.11.15.
 */
public class MyOnDragListener implements View.OnDragListener {
    private static final String TAG = "MyOnDragListener";

    private EditText name;
    private EditText price;
    private int categoryId;
    private Context applicationContext;
    private final String EMPTY_STRING = "";

    public MyOnDragListener(EditText name, EditText price, int category, Context applicationContext) {
        this.name = name;
        this.price = price;
        this.categoryId = category;
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DROP:
                DBHandler dbHandler = DBHandler.getDbHandler(applicationContext);
                String productName = null;
                double productPrice = 0;
                if (price.getText().toString().length() > 0) {
                    try {
                        productPrice = Double.parseDouble(price.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                if (name.getText().toString().trim().length() > 2) {
                    productName = name.getText().toString().trim();
                }
                if (productName == null || productPrice <= 0) {
                    Toast.makeText(applicationContext, "Error: wrong input", Toast.LENGTH_LONG).show();
                    return false;
                }

                final Product product = new Product(categoryId, 1, productName, productPrice);
                product.setDirty(true);
                int id = dbHandler.addProduct(product);
                product.setId(id);

                String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/add";

                Log.i(TAG, "Send product to url: " + url + ", request body: " + product);
                Map<String, String> headers = new HashMap<>();
                GsonRequest<Product> gsonRequest = new GsonRequest<>(Request.Method.POST, url, Product.class, headers,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object o) {
                                product.setDirty(false);
                                DBHandler.getDbHandler(applicationContext).updateProduct(product);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Toast.makeText(applicationContext, "Product update failed", Toast.LENGTH_LONG).show();

                            }
                        }, product);
                gsonRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(applicationContext).addToRequestQueue(gsonRequest);

                //clean input fields after save
                name.setText(EMPTY_STRING);
                price.setText(EMPTY_STRING);

                Toast.makeText(applicationContext, "Product: " + product.getName() + " was saved...", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    public EditText getName() {
        return name;
    }

    public void setName(EditText name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "MyOnDragListener{" +
                "name=" + name +
                ", price=" + price +
                ", categoryId=" + categoryId +
                ", applicationContext=" + applicationContext +
                '}';
    }
}
