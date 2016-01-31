package com.expenses.volodymyr.notecase.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.entity.Product;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.adapter.CategoryAdapter;
import com.expenses.volodymyr.notecase.fragment.TabViewExpenses;
import com.expenses.volodymyr.notecase.request.GsonRequest;
import com.expenses.volodymyr.notecase.util.AppProperties;
import com.expenses.volodymyr.notecase.util.DBHandler;
import com.expenses.volodymyr.notecase.util.VolleySingleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vkret on 02.12.15.
 */
public class EditExpenseActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "EditExpenseActivity";

    private EditText name, price;
    private TextView dateTime;
    private ImageView save, delete, navigationArrow, logo, categoryArea;
    private GridView categoryGrid;
    private Product product;
    private Category category;
    private DBHandler dbHandler;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        int productId = getIntent().getIntExtra(TabViewExpenses.PRODUCT_ID_KEY, -1);
        dbHandler = DBHandler.getDbHandler(this);
        product = dbHandler.getProductById(productId);
        category = dbHandler.getCategoryById(product.getCategoryId());
        List<Category> categories = dbHandler.getAllCategories();


        name = (EditText) findViewById(R.id.edit_expense_name);
        price = (EditText) findViewById(R.id.edit_expense_price);
        dateTime = (TextView) findViewById(R.id.date_time);
        save = (ImageView) findViewById(R.id.action_item);
        delete = (ImageView) findViewById(R.id.action_item_delete);
        navigationArrow = (ImageView) findViewById(R.id.navigation_arrow);
        logo = (ImageView) findViewById(R.id.logo);
        categoryGrid = (GridView) findViewById(R.id.categoriesGrid);
        categoryArea = (ImageView) findViewById(R.id.edit_expense_category);

        categoryAdapter = new CategoryAdapter(this, categories, true);
        categoryGrid.setAdapter(categoryAdapter);
        categoryGrid.setOnItemClickListener(this);

        name.setText(product.getName());
        price.setText(String.format("%.2f", product.getPrice()));
        dateTime.setText(product.getCreated().toString());
        categoryArea.setImageResource(category.getImage());
        categoryArea.setBackgroundColor(category.getColor());


        save.setOnClickListener(this);
        delete.setVisibility(View.GONE);
        navigationArrow.setOnClickListener(this);
        logo.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_item:
                try {
                    String newName = name.getText().toString().trim();
                    double newPrice = Double.parseDouble(price.getText().toString().trim());
                    product.setName(newName);
                    product.setPrice(newPrice);
                    product.setCategoryId(category.getId());

                } catch (RuntimeException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Incorrect input", Toast.LENGTH_LONG).show();
                }

                String url = AppProperties.HOST + AppProperties.PORT + "/rest/product/update";
                Log.i(TAG, "Update product, url: " + url + ", product: " + product);
                Map<String, String> headers = new HashMap<>();
                GsonRequest<Product> gsonRequest = new GsonRequest<>(Request.Method.PUT, url, Product.class, headers,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object o) {
                                product.setDirty(false);
                                DBHandler.getDbHandler(getApplicationContext()).updateProduct(product);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Toast.makeText(getApplicationContext(), "Product sending failed", Toast.LENGTH_LONG).show();
                            }
                        }, product);

                dbHandler.updateProduct(product);
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();

                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(gsonRequest);


                break;
            case R.id.navigation_arrow:
            case R.id.logo:
                break;

        }
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        category = (Category) parent.getItemAtPosition(position);
        categoryArea.setImageResource(category.getImage());
        categoryArea.setBackgroundColor(category.getColor());
    }
}
