package com.expenses.volodymyr.notecase.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.adapter.CategoryAdapter;
import com.expenses.volodymyr.notecase.entity.Category;
import com.expenses.volodymyr.notecase.entity.Product;
import com.expenses.volodymyr.notecase.fragment.TabViewExpenses;
import com.expenses.volodymyr.notecase.request.GsonRequest;
import com.expenses.volodymyr.notecase.util.AppProperties;
import com.expenses.volodymyr.notecase.util.DBHandler;
import com.expenses.volodymyr.notecase.util.VolleySingleton;

import java.util.List;

/**
 * Created by volodymyr on 01.01.16.
 */
public class ViewExpenseActivity extends Activity implements View.OnClickListener {
    private TextView name, price, dateTime, categoryName;
    private ImageView categryImage, navigationArrow, logo, editButton, delete;
    private Product product;
    private DBHandler dbHandler;
    private Toolbar toolbar;


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

    public void initFields(){
        int productId = getIntent().getIntExtra(TabViewExpenses.PRODUCT_ID_KEY, -1);
        dbHandler = DBHandler.getDbHandler(this);
        product = dbHandler.getProductById(productId);
        Category category = dbHandler.getCategoryById(product.getCategoryId());

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
        switch (v.getId()){
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
                                product.setDirty(true);
                                dbHandler.deleteProductById(product.getId());
                                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();

                                String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/delete/" + product.getId();
                                GsonRequest<Product> gsonRequest = new GsonRequest<>(Request.Method.DELETE, url, Product.class, null,
                                        new Response.Listener() {
                                            @Override
                                            public void onResponse(Object o) {
                                                product.setDirty(false);
                                                DBHandler.getDbHandler(getApplicationContext()).deleteProductById(product.getId());
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                Toast.makeText(getApplicationContext(), "Product deleting failed", Toast.LENGTH_LONG).show();
                                            }
                                        }, null);
                                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(gsonRequest);
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
