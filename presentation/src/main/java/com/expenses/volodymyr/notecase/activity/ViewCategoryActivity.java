package com.expenses.volodymyr.notecase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.util.AuthenticationException;
import com.domain.volodymyr.notecase.manager.CategoryManager;
import com.domain.volodymyr.notecase.manager.CategoryManagerImpl;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.adapter.CategoryAdapter;
import com.expenses.volodymyr.notecase.util.SafeAsyncTask;

import java.util.List;

/**
 * Created by vkret on 02.12.15.
 */
public class ViewCategoryActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ViewCategoryActivity";
    public static final String CATEGORY_ID_KEY = "categoryId";

    private GridView gridView;
    private ImageView addCategoryButton;
    private ImageView syncCategoryButton;
    private ImageView navigationArrow;
    private ImageView logo;

    private CategoryManager categoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_categories);

        navigationArrow = (ImageView) findViewById(R.id.navigation_arrow);
        logo = (ImageView) findViewById(R.id.logo);
        syncCategoryButton = (ImageView) findViewById(R.id.action_item_left);
        addCategoryButton = (ImageView) findViewById(R.id.action_item_right);

        gridView = (GridView) findViewById(R.id.categoriesGrid);

        syncCategoryButton.setImageResource(R.drawable.ic_sync_white_24dp);
        addCategoryButton.setImageResource(R.drawable.ic_control_point_white_24dp);


        navigationArrow.setOnClickListener(this);
        logo.setOnClickListener(this);
        addCategoryButton.setOnClickListener(this);
        syncCategoryButton.setOnClickListener(this);

        categoryManager = new CategoryManagerImpl(getApplicationContext());

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
    protected void onStart() {
        initCategories();
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_item_left:
                new SafeAsyncTask<Void, Void, Boolean>(this) {
                    @Override
                    public Boolean doInBackgroundSafe() throws AuthenticationException {
                        return categoryManager.syncCategories();
                    }

                    @Override
                    protected void onPostExecute(Boolean success) {
                        if (success) {
                            initCategories();
                            Log.i(TAG, "Categories were successfully updated");
                        }
                    }
                }.execute();
                break;
            case R.id.action_item_right:
                Intent intent = new Intent(ViewCategoryActivity.this, AddEditCategoryActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_arrow:
            case R.id.logo:
                finish();
                break;
        }
    }

    public void initCategories() {
        new SafeAsyncTask<Void, Void, List<Category>>(this) {
            @Override
            public List<Category> doInBackgroundSafe() throws AuthenticationException {
                return categoryManager.getAllCategories();
            }

            @Override
            protected void onPostExecute(List<Category> categories) {
                ArrayAdapter<Category> adapter = new CategoryAdapter(getApplicationContext(), categories, true);
                gridView.setAdapter(adapter);
            }
        }.execute();
    }

}
