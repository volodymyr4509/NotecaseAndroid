package com.expenses.volodymyr.notecase.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.Toast;

import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.entity.Product;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.adapter.ImageGridAdapter;
import com.data.volodymyr.notecase.util.DBHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vkret on 02.12.15.
 */
public class AddEditCategoryActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, AdapterView.OnItemClickListener {
    private EditText categoryName;
    private SeekBar colorSeekBar;
    private ImageView resultImage, navigationArrow, logo, saveButton, delete;
    private GridView gridView;
    private int categoryId;
    private List<Integer> imagesIds;
    private int selectedImageId;
    private Category category;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        categoryName = (EditText) findViewById(R.id.category_name);
        colorSeekBar = (SeekBar) findViewById(R.id.color_picker);
        resultImage = (ImageView) findViewById(R.id.result_image);
        saveButton = (ImageView) findViewById(R.id.action_item);
        navigationArrow = (ImageView) findViewById(R.id.navigation_arrow);
        logo = (ImageView)findViewById(R.id.logo);
        gridView = (GridView) findViewById(R.id.select_image_grid);
        delete = (ImageView) findViewById(R.id.action_item_delete);

        getImagesId();
        ListAdapter adapter = new ImageGridAdapter(getApplicationContext(), imagesIds);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);


        Intent intent = getIntent();
        categoryId = intent.getIntExtra(ViewCategoryActivity.CATEGORY_ID_KEY, -1);
        resultImage.setBackgroundColor(Color.WHITE);

        if (categoryId != -1) {
            updateCategory(categoryId);
            delete.setOnClickListener(this);
        }else {
            //adding new category - no need in delete button
            delete.setVisibility(View.GONE);
        }

        colorSeekBar.setMax(256 * 7 - 1);
        colorSeekBar.setOnSeekBarChangeListener(this);

        //if categoryId<0 insert. Update otherwise
        saveButton.setOnClickListener(this);
        navigationArrow.setOnClickListener(this);
        logo.setOnClickListener(this);
    }

    public void updateCategory(int categoryId) {
        dbHandler = DBHandler.getDbHandler(this);
        category = dbHandler.getCategoryById(categoryId);
        resultImage.setBackgroundColor(category.getColor());
        selectedImageId = category.getImage();
        resultImage.setImageResource(category.getImage());
        categoryName.setText(category.getName());
        colorSeekBar.setProgress(category.getColor());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            int r = 0;
            int g = 0;
            int b = 0;

            if (progress < 256) {
                b = progress;
            } else if (progress < 256 * 2) {
                g = progress % 256;
                b = 256 - progress % 256;
            } else if (progress < 256 * 3) {
                g = 255;
                b = progress % 256;
            } else if (progress < 256 * 4) {
                r = progress % 256;
                g = 256 - progress % 256;
                b = 256 - progress % 256;
            } else if (progress < 256 * 5) {
                r = 255;
                g = 0;
                b = progress % 256;
            } else if (progress < 256 * 6) {
                r = 255;
                g = progress % 256;
                b = 256 - progress % 256;
            } else if (progress < 256 * 7) {
                r = 255;
                g = 255;
                b = progress % 256;
            }

            resultImage.setBackgroundColor(Color.argb(255, r, g, b));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_item:
                String newCategoryName = categoryName.getText().toString();
                ColorDrawable colorDrawable = (ColorDrawable) resultImage.getBackground();
                int newCategoryColor = colorDrawable.getColor();

                if (newCategoryName != null) {
                    Category newCategory = new Category(newCategoryName, newCategoryColor, selectedImageId);
                    DBHandler dbHandler = DBHandler.getDbHandler(getApplicationContext());
                    if (categoryId < 0) {
                        dbHandler.addCategory(newCategory);
                    } else {
                        newCategory.setId(categoryId);
                        dbHandler.updateCategory(newCategory);
                    }
                    Toast.makeText(getApplicationContext(), "CategoryDAO saved", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "CategoryDAO name should not be empty", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.action_item_delete:
                //delete not used category
                List<Product> products = dbHandler.getProductsByCategoryId(categoryId);
                if (products.size()>0){
                    new AlertDialog.Builder(AddEditCategoryActivity.this)
                            .setTitle("Delete category")
                            .setMessage("You have " + products.size() + " product(s) with category " + category.getName() + ". You need to remove all category references.")
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(R.drawable.alert)
                            .show();
                }else {
                    new AlertDialog.Builder(AddEditCategoryActivity.this)
                            .setTitle("Delete category")
                            .setMessage("Are you sure you want to delete this category?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dbHandler.deleteCategoryById(category.getId());
                                    Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(R.drawable.alert)
                            .show();
                }
                break;

            case R.id.navigation_arrow:
            case R.id.logo:
                finish();
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedImageId = imagesIds.get(position);
        resultImage.setImageResource(selectedImageId);
    }

    private void getImagesId() {

        imagesIds = new ArrayList<>();
        imagesIds.add(R.drawable.ic_attachment_black_24dp);
        imagesIds.add(R.drawable.ic_build_black_24dp);
        imagesIds.add(R.drawable.ic_business_black_24dp);
        imagesIds.add(R.drawable.ic_card_travel_black_24dp);
        imagesIds.add(R.drawable.ic_color_lens_white_24dp);
        imagesIds.add(R.drawable.ic_directions_walk_white_24dp);
        imagesIds.add(R.drawable.ic_explore_black_24dp);
        imagesIds.add(R.drawable.ic_local_car_wash_white_24dp);
        imagesIds.add(R.drawable.ic_local_shipping_white_24dp);
        imagesIds.add(R.drawable.ic_pets_black_24dp);
        imagesIds.add(R.drawable.ic_schedule_black_24dp);
        imagesIds.add(R.drawable.ic_security_white_24dp);
        imagesIds.add(R.drawable.ic_sim_card_white_24dp);
        imagesIds.add(R.drawable.ic_toys_white_24dp);
        imagesIds.add(R.drawable.ic_watch_white_24dp);
        imagesIds.add(R.drawable.ic_wb_sunny_white_24dp);
        imagesIds.add(R.drawable.ic_wifi_tethering_white_18dp);
        imagesIds.add(R.drawable.k_backet_192);

    }
}
