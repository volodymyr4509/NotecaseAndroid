package com.expenses.volodymyr.notecase.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.entity.Product;
import com.data.volodymyr.notecase.util.AuthenticationException;
import com.domain.volodymyr.notecase.manager.CategoryManager;
import com.domain.volodymyr.notecase.manager.CategoryManagerImpl;
import com.domain.volodymyr.notecase.manager.ProductManager;
import com.domain.volodymyr.notecase.manager.ProductManagerImpl;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.adapter.CategoryAdapter;
import com.expenses.volodymyr.notecase.fragment.TabViewExpenses;
import com.expenses.volodymyr.notecase.util.SafeAsyncTask;

import java.util.List;

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
    private CategoryAdapter categoryAdapter;
    private ProductManager productManager;
    private CategoryManager categoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        name = (EditText) findViewById(R.id.edit_expense_name);
        price = (EditText) findViewById(R.id.edit_expense_price);
        dateTime = (TextView) findViewById(R.id.date_time);
        save = (ImageView) findViewById(R.id.action_item_right);
        delete = (ImageView) findViewById(R.id.action_item_left);
        navigationArrow = (ImageView) findViewById(R.id.navigation_arrow);
        logo = (ImageView) findViewById(R.id.logo);
        categoryGrid = (GridView) findViewById(R.id.categoriesGrid);
        categoryArea = (ImageView) findViewById(R.id.edit_expense_category);

        productManager = new ProductManagerImpl(getApplicationContext());
        categoryManager = new CategoryManagerImpl(getApplicationContext());




        save.setOnClickListener(this);
        delete.setVisibility(View.GONE);
        navigationArrow.setOnClickListener(this);
        logo.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        String productUuid = getIntent().getStringExtra(TabViewExpenses.PRODUCT_UUID_KEY);

        product = productManager.getProductByUuid(productUuid);
        category = categoryManager.getCategoryById(product.getCategoryId());
        List<Category> categories = categoryManager.getAllCategories();

        categoryAdapter = new CategoryAdapter(this, categories, true);
        categoryGrid.setAdapter(categoryAdapter);
        categoryGrid.setOnItemClickListener(this);
        super.onStart();
    }

    @Override
    protected void onResume() {
        name.setText(product.getName());
        price.setText(String.format("%.2f", product.getPrice()));
        dateTime.setText(product.getCreated().toString());
        categoryArea.setImageResource(category.getImage());
        categoryArea.setBackgroundColor(category.getColor());
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_item_right:
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
                new SafeAsyncTask<Void, Void, Void>(this){

                    @Override
                    public Void doInBackgroundSafe() throws AuthenticationException {
                        productManager.updateProduct(product);
                        finish();
                        return null;
                    }
                }.execute();

                break;
            case R.id.navigation_arrow:
            case R.id.logo:
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        category = (Category) parent.getItemAtPosition(position);
        categoryArea.setImageResource(category.getImage());
        categoryArea.setBackgroundColor(category.getColor());
    }

}
