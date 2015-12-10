package com.expenses.volodymyr.notecase.util;

import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.expenses.volodymyr.notecase.entity.Product;


/**
 * Created by volodymyr on 15.11.15.
 */
public class MyOnDragListener implements View.OnDragListener {

    private EditText name;
    private EditText price;
    private int category;
    private Context applicationContext;
    private final String EMPTY_STRING = "";

    public MyOnDragListener(EditText name, EditText price, int category, Context applicationContext) {
        this.name = name;
        this.price = price;
        this.category = category;
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
                    }catch (NumberFormatException e){
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

                Product product = new Product(category, 1, productName, productPrice);
                dbHandler.addProduct(product);

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
                ", category=" + category +
                ", applicationContext=" + applicationContext +
                '}';
    }
}
