package com.expenses.volodymyr.notecase.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.entity.Product;
import com.domain.volodymyr.notecase.manager.CategoryManager;
import com.domain.volodymyr.notecase.manager.CategoryManagerImpl;
import com.domain.volodymyr.notecase.manager.ProductManager;
import com.domain.volodymyr.notecase.manager.ProductManagerImpl;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.fragment.TabViewExpenses;

/**
 * Created by volodymyr on 01.01.16.
 */
public class ViewExpenseActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ViewExpenseActivity";
    private TextView name, price, dateTime, categoryName;
    private ImageView categryImage, navigationArrow, logo, editButton, delete;
    private Product product;
    private Toolbar toolbar;

    private ProductManager productManager;
    private CategoryManager categoryManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);

        name = (TextView) findViewById(R.id.view_expense_name);
        price = (TextView) findViewById(R.id.view_expense_price);
        navigationArrow = (ImageView) findViewById(R.id.navigation_arrow);
        logo = (ImageView) findViewById(R.id.logo);
        dateTime = (TextView) findViewById(R.id.view_expense_datetime);
        categryImage = (ImageView) findViewById(R.id.view_expense_category_image);
        categoryName = (TextView) findViewById(R.id.view_expense_category_name);
        editButton = (ImageView) findViewById(R.id.action_item);
        delete = (ImageView) findViewById(R.id.action_item_delete);

        productManager = new ProductManagerImpl(getApplicationContext());
        categoryManager = new CategoryManagerImpl(getApplicationContext());
        initFields();

        editButton.setOnClickListener(this);
        delete.setOnClickListener(this);
        navigationArrow.setOnClickListener(this);
        logo.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        initFields();
        super.onResume();
    }

    public void initFields() {
        int productId = getIntent().getIntExtra(TabViewExpenses.PRODUCT_ID_KEY, -1);
        product = productManager.getProductById(productId);
        Category category = categoryManager.getCategoryById(product.getCategoryId());
        if (category == null) {
            return;
        }
        name.setText(product.getName());
        price.setText(String.valueOf(product.getPrice()));
        dateTime.setText(product.getCreated().toString());
        categoryName.setText(category.getName());
        categryImage.setBackgroundColor(category.getColor());
        categryImage.setImageResource(category.getImage());
        editButton.setImageResource(R.drawable.ic_mode_edit_white_24dp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_item:
                Intent editExpense = new Intent(this, EditExpenseActivity.class);
                editExpense.putExtra(TabViewExpenses.PRODUCT_ID_KEY, product.getId());
                startActivity(editExpense);
                break;
            case R.id.action_item_delete:
                new AlertDialog.Builder(ViewExpenseActivity.this)
                        .setTitle("Delete product")
                        .setMessage("Are you sure you want to delete this product?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new AsyncTask<Product, Void, Boolean>() {
                                    @Override
                                    protected Boolean doInBackground(Product... params) {
                                        Product product = params[0];
                                        return productManager.deleteProductById(product.getId());
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean success) {
                                        if (success) {
                                            Log.i(TAG, "Product with Id = " + product.getId() + ", Name = " + product.getName() + " deleted.");
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Product with id = " + product.getId() + " deleted", Toast.LENGTH_LONG);
                                            Log.e(TAG, "Product delete failed. Id = " + product.getId());
                                        }
                                    }
                                }.execute(product);

                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.alert)
                        .show();
                break;
            case R.id.logo:
            case R.id.navigation_arrow:
                finish();
                break;
        }
    }
}
