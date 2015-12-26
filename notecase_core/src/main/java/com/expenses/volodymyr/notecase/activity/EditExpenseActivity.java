package com.expenses.volodymyr.notecase.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
public class EditExpenseActivity extends Activity implements View.OnClickListener{
    private EditText name, price;
    private TextView dateTime;
    private ImageView save, navigationArrow, logo;
    private GridView categoryGrid;
    private Product product;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        Intent intent = getIntent();
        int productId = intent.getIntExtra(TabViewExpenses.PRODUCT_ID_KEY, -1);
        dbHandler = DBHandler.getDbHandler(this);
        product = dbHandler.getProductById(productId);
        List<Category> categories = dbHandler.getAllCategories();
        System.out.println(categories);

        name = (EditText) findViewById(R.id.edit_expense_name);
        price = (EditText) findViewById(R.id.edit_expense_price);
        dateTime = (TextView) findViewById(R.id.date_time);
        save = (ImageView) findViewById(R.id.action_item);
        navigationArrow = (ImageView)findViewById(R.id.navigation_arrow);
        logo = (ImageView)findViewById(R.id.logo);
        categoryGrid = (GridView) findViewById(R.id.categoriesGrid);

        categoryGrid.setAdapter(new CategoryAdapter(getApplicationContext(), categories, true));
        categoryGrid.setChoiceMode(GridView.CHOICE_MODE_SINGLE);

        name.setText(product.getName());
        price.setText(String.format("%.2f", product.getPrice()));
        dateTime.setText(product.getCreated().toString());

        //set default category
        int oldCategoryIndex = -1;
        for (int i = 0; i<categories.size(); i++){
            if (categories.get(i).getId() == product.getCategoryId()){
                oldCategoryIndex = i;
            }
        }
        categoryGrid.setSelection(oldCategoryIndex);

        save.setOnClickListener(this);
        navigationArrow.setOnClickListener(this);
        logo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.action_item:
                try {
                    String newName = name.getText().toString().trim();
                    double newPrice = Double.parseDouble(price.getText().toString().trim());
                    product.setName(newName);
                    product.setPrice(newPrice);
                    Object selectedItem = categoryGrid.getSelectedItem();
                    if (selectedItem instanceof Category){
                        Category selectedCategory = (Category) selectedItem;
                        product.setCategoryId(selectedCategory.getId());
                    }

                }catch (RuntimeException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Incorrect input", Toast.LENGTH_LONG).show();
                }
                dbHandler.updateProduct(product);
                finish();
                break;
            case R.id.navigation_arrow:
            case R.id.logo:
                finish();
                break;
        }
    }
}
