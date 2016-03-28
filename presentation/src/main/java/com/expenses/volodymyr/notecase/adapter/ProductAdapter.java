package com.expenses.volodymyr.notecase.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.data.volodymyr.notecase.entity.Product;
import com.data.volodymyr.notecase.entity.User;
import com.domain.volodymyr.notecase.manager.UserManagerImpl;
import com.expenses.volodymyr.notecase.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by vkret on 03.12.15.
 */
public class ProductAdapter extends ArrayAdapter<Product> {
    private User owner;

    public ProductAdapter(Context context, List<Product> productList) {
        super(context, 0, productList);
        owner = new UserManagerImpl(context).getUserOwner();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product, parent, false);
        }

        TextView productName = (TextView) convertView.findViewById(R.id.product_name);
        TextView productPrice = (TextView) convertView.findViewById(R.id.product_price);
        TextView productCreated = (TextView) convertView.findViewById(R.id.product_created);

        if (owner != null && product.getUserId() == owner.getId()) {
            productName.setTextColor(Color.BLACK);
            productPrice.setTextColor(Color.BLACK);
            productCreated.setTextColor(Color.BLACK);
        } else {
            productName.setTextColor(Color.GRAY);
            productPrice.setTextColor(Color.GRAY);
            productCreated.setTextColor(Color.GRAY);
        }
        productName.setText(product.getName());

        //format double
        productPrice.setText(String.format("%.2f", product.getPrice()));
        Timestamp created = product.getCreated();
        String formattedTimestamp;

        formattedTimestamp = new SimpleDateFormat("MM/dd/yyyy").format(created);

        productCreated.setText(formattedTimestamp);

        return convertView;
    }
}
