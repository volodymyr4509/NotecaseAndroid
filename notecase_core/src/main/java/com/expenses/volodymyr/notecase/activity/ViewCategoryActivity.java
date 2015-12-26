package com.expenses.volodymyr.notecase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.adapter.CategoryAdapter;
import com.expenses.volodymyr.notecase.entity.Category;
import com.expenses.volodymyr.notecase.util.DBHandler;

import java.util.List;

/**
 * Created by vkret on 02.12.15.
 */
public class ViewCategoryActivity extends AppCompatActivity {
    public static final String CATEGORY_ID_KEY = "categoryId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_categories);

        Toolbar toolbar = (Toolbar) findViewById(R.id.category_view_toolbar);
//        setSupportActionBar(toolbar);

        GridView gridView = (GridView) findViewById(R.id.categoriesGrid);
        ImageView addCategoryButton = (ImageView) findViewById(R.id.addNewCategory);

        DBHandler dbHandler = DBHandler.getDbHandler(this);
        List<Category> categoryList = dbHandler.getAllCategories();

        ArrayAdapter<Category> adapter = new CategoryAdapter(this, categoryList, true);

        gridView.setAdapter(adapter);

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewCategoryActivity.this, AddEditCategoryActivity.class);
                startActivity(intent);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewCategoryActivity.this, AddEditCategoryActivity.class);
                Category category = (Category) parent.getAdapter().getItem(position);
                intent.putExtra(CATEGORY_ID_KEY, category.getId());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        System.out.println("onResume viewCategory");
        super.onResume();
    }
}
