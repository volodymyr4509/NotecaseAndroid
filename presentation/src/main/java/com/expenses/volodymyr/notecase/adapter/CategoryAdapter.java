package com.expenses.volodymyr.notecase.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.data.volodymyr.notecase.entity.Category;
import com.expenses.volodymyr.notecase.R;

import java.util.List;

/**
 * Created by vkret on 07.12.15.
 */

public class CategoryAdapter extends ArrayAdapter<Category> {

    private boolean namesEnabled;

    public CategoryAdapter(Context context, List<Category> categoryList, boolean namesEnabled) {
        super(context, 0, categoryList);
        this.namesEnabled = namesEnabled;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category category = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_category, parent, false);
        }

        ImageView categoryImage = (ImageView) convertView.findViewById(R.id.category_image);
        categoryImage.setImageResource(category.getImage());

        Drawable drawable = getContext().getResources().getDrawable(R.drawable.category_shape_small);
        drawable.setColorFilter(category.getColor(), PorterDuff.Mode.MULTIPLY);
        categoryImage.setBackground(drawable);

        if (namesEnabled) {
            TextView categoryName = (TextView) convertView.findViewById(R.id.category_name);
            categoryName.setText(category.getName());
        }

        return convertView;
    }
}
