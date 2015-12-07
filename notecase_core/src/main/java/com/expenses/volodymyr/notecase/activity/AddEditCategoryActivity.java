package com.expenses.volodymyr.notecase.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.entity.Category;
import com.expenses.volodymyr.notecase.util.DBHandler;

/**
 * Created by vkret on 02.12.15.
 */
public class AddEditCategoryActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private EditText categoryName;
    private SeekBar colorSeekBar;
    private View resultColor;
    private Button saveButton;
    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        categoryName = (EditText) findViewById(R.id.category_name);
        colorSeekBar = (SeekBar) findViewById(R.id.color_picker);
        resultColor = findViewById(R.id.result_color);
        saveButton = (Button) findViewById(R.id.save_category);

        Intent intent = getIntent();
        categoryId = intent.getIntExtra(ViewCategoryActivity.CATEGORY_ID_KEY, -1);
        resultColor.setBackgroundColor(Color.BLACK);

        if (categoryId != -1) {
            updateCategory(categoryId);
        }

        colorSeekBar.setMax(256 * 7 - 1);
        colorSeekBar.setOnSeekBarChangeListener(this);

        //if categoryId<0 insert. Update otherwise
        saveButton.setOnClickListener(this);

    }

    public void updateCategory(int categoryId) {
        DBHandler dbHandler = DBHandler.getDbHandler(this);
        final Category category = dbHandler.getCategoryById(categoryId);
        resultColor.setBackgroundColor(category.getColor());
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

            resultColor.setBackgroundColor(Color.argb(255, r, g, b));
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
        String newCategoryName = categoryName.getText().toString();
        ColorDrawable colorDrawable = (ColorDrawable) resultColor.getBackground();
        int newCategoryColor = colorDrawable.getColor();
        if (newCategoryName != null) {
            Category newCategory = new Category(newCategoryName, newCategoryColor);
            DBHandler dbHandler = DBHandler.getDbHandler(getApplicationContext());
            if (categoryId < 0) {
                dbHandler.addCategory(newCategory);
            } else {
                newCategory.setId(categoryId);
                dbHandler.updateCategory(newCategory);
            }
            Toast.makeText(getApplicationContext(), "Category saved", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Category name should not be empty", Toast.LENGTH_LONG).show();
        }
    }
}
