package com.expenses.volodymyr.notecase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.adapter.CategoryAdapter;
import com.expenses.volodymyr.notecase.entity.Category;
import com.expenses.volodymyr.notecase.util.DBHandler;

import java.util.List;

/**
 * Created by vkret on 02.12.15.
 */
public class ViewCategoryActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String CATEGORY_ADD = "add";
    public static final String CATEGORY_ID_KEY = "categoryId";
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_categories);

        ImageView navigationArrow = (ImageView) findViewById(R.id.navigation_arrow);
        ImageView logo = (ImageView) findViewById(R.id.logo);
        ImageView delete = (ImageView) findViewById(R.id.action_item_delete);

        navigationArrow.setOnClickListener(this);
        logo.setOnClickListener(this);
        delete.setVisibility(View.GONE);

        gridView = (GridView) findViewById(R.id.categoriesGrid);
        ImageView addCategoryButton = (ImageView) findViewById(R.id.action_item);
        addCategoryButton.setImageResource(R.drawable.ic_control_point_white_24dp);

        initCategories();

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
        initCategories();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    public void initCategories(){
        DBHandler dbHandler = DBHandler.getDbHandler(this);
        List<Category> categoryList = dbHandler.getAllCategories();
        ArrayAdapter<Category> adapter = new CategoryAdapter(this, categoryList, true);
        gridView.setAdapter(adapter);
    }
}
