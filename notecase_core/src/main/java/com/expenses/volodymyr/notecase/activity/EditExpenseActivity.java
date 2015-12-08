package com.expenses.volodymyr.notecase.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    private Spinner categorySelector;
    private Button saveButton;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.expense_edit_toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);

        name = (EditText) findViewById(R.id.edit_expense_name);
        price = (EditText) findViewById(R.id.edit_expense_price);
        dateTime = (TextView) findViewById(R.id.date_time);
        categorySelector = (Spinner) findViewById(R.id.edit_category);
        saveButton = (Button) findViewById(R.id.save_button);

        name.setText(product.getName());
        price.setText(String.format("%.2f", product.getPrice()));
        dateTime.setText(product.getCreated().toString());

        ArrayAdapter<Category> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categorySelector.setAdapter(dataAdapter);
        //set default category
        int oldCategoryIndex = -1;
        for (int i = 0; i<categories.size(); i++){
            if (categories.get(i).getId() == product.getCategoryId()){
                oldCategoryIndex = i;
            }
        }
        categorySelector.setSelection(oldCategoryIndex);

        saveButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_button:
                try {
                    String newName = name.getText().toString().trim();
                    double newPrice = Double.parseDouble(price.getText().toString().trim());
                    product.setName(newName);
                    product.setPrice(newPrice);
                    Category selectedCategory = (Category)categorySelector.getSelectedItem();
                    if (selectedCategory != null){
                        product.setCategoryId(selectedCategory.getId());
                    }
                }catch (RuntimeException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Incorrect input", Toast.LENGTH_LONG).show();
                }
                dbHandler.updateProduct(product);
                finish();
        }
    }
}
