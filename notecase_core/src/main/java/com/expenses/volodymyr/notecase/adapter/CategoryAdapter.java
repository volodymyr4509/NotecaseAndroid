package com.expenses.volodymyr.notecase.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.entity.Category;

import java.util.List;

/**
 * Created by vkret on 07.12.15.
 */

public class CategoryAdapter extends ArrayAdapter<Category> {

    public CategoryAdapter(Context context, List<Category> productList) {
        super(context, 0, productList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category category = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_category, parent, false);
        }
        TextView categoryName = (TextView) convertView.findViewById(R.id.category_name);
        View categoryImage = convertView.findViewById(R.id.category_image);

        categoryImage.setAlpha((float) 0.5);
        categoryImage.setBackgroundColor(category.getColor());
        categoryImage.setBackgroundResource(category.getImage());

        categoryName.setText(category.getName());

        return convertView;
    }
}
