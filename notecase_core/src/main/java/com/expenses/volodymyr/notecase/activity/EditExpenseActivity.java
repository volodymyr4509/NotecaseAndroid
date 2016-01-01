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

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.adapter.CategoryAdapter;
import com.expenses.volodymyr.notecase.entity.Category;
import com.expenses.volodymyr.notecase.entity.Product;
import com.expenses.volodymyr.notecase.fragment.TabViewExpenses;
import com.expenses.volodymyr.notecase.util.DBHandler;

import java.util.List;

/**
 * Created by vkret on 02.12.15.
 */
public class EditExpenseActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private EditText name, price;
    private TextView dateTime;
    private ImageView save, navigationArrow, logo, categoryArea;
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

        //set default category


        save.setOnClickListener(this);
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
                dbHandler.updateProduct(product);
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
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
