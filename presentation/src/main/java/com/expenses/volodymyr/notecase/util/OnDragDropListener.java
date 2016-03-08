package com.expenses.volodymyr.notecase.util;

import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.data.volodymyr.notecase.entity.Product;
import com.data.volodymyr.notecase.util.AuthenticationException;
import com.domain.volodymyr.notecase.manager.ProductManager;
import com.domain.volodymyr.notecase.manager.ProductManagerImpl;


/**
 * Created by volodymyr on 15.11.15.
 */
public class OnDragDropListener implements View.OnDragListener {
    private static final String TAG = "OnDragDropListener";
    private final String EMPTY_STRING = "";

    private EditText name;
    private EditText price;
    private int categoryId;
    private Context context;

    private ProductManager productManager;

    public OnDragDropListener(EditText name, EditText price, int category, Context context) {
        this.name = name;
        this.price = price;
        this.categoryId = category;
        this.context = context;
        productManager = new ProductManagerImpl(context);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DROP:
                String productName = null;
                double productPrice = 0;
                if (price.getText().toString().length() > 0) {
                    try {
                        productPrice = Double.parseDouble(price.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                if (name.getText().toString().trim().length() > 2) {
                    productName = name.getText().toString().trim();
                }
                if (productName == null || productPrice <= 0) {
                    Toast.makeText(context, "Error: wrong input", Toast.LENGTH_LONG).show();
                    return false;
                }

                //user owner always 1 user
                final Product product = new Product(categoryId, 1, productName, productPrice);

                new SafeAsyncTask<Product, Void, Boolean>(context){
                    @Override
                    public Boolean doInBackgroundSafe() throws AuthenticationException {
                        return productManager.addProduct(product);
                    }
                }.execute(product);
                //clean input fields after save
                name.setText(EMPTY_STRING);
                price.setText(EMPTY_STRING);

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
        return "OnDragDropListener{" +
                "name=" + name +
                ", price=" + price +
                ", categoryId=" + categoryId +
                ", context=" + context +
                '}';
    }
}
