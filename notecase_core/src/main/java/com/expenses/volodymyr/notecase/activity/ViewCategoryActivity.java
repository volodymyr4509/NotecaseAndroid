package com.expenses.volodymyr.notecase.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.entity.Category;
import com.expenses.volodymyr.notecase.util.DBHandler;

import java.util.List;

/**
 * Created by vkret on 02.12.15.
 */
public class ViewCategoryActivity extends Activity{
    public static final String CATEGORY_ID_KEY = "categoryId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_categories);

        ListView listView = (ListView) findViewById(R.id.categoriesList);
        Button addCategoryButton = (Button) findViewById(R.id.addNewCategory);

        DBHandler dbHandler = DBHandler.getDbHandler(this);
        List<Category> categoryList = dbHandler.getAllCategories();

        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoryList);

        listView.setAdapter(adapter);


        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewCategoryActivity.this, AddEditCategoryActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewCategoryActivity.this, AddEditCategoryActivity.class);
                Category category = (Category) parent.getAdapter().getItem(position);
                intent.putExtra(CATEGORY_ID_KEY, category.getId());
                startActivity(intent);
            }
        });

    }
}
