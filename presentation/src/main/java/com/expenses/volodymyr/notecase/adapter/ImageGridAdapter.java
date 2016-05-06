package com.expenses.volodymyr.notecase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.expenses.volodymyr.notecase.R;

import java.util.List;

/**
 * Created by volodymyr on 13.12.15.
 */
public class ImageGridAdapter extends ArrayAdapter<Integer> {

    public ImageGridAdapter(Context context, List<Integer> resImages) {
        super(context, 0, resImages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Integer drawableId = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_category_image_right, parent, false);
        }

        ImageView categoryImage = (ImageView) convertView.findViewById(R.id.category_drawable_image);
        categoryImage.setImageResource(drawableId);

        return convertView;
    }
}
