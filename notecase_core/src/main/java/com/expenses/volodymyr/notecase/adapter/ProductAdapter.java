package com.expenses.volodymyr.notecase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.entity.Product;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by vkret on 03.12.15.
 */
public class ProductAdapter extends ArrayAdapter<Product> {
    public ProductAdapter(Context context, List<Product> productList){
        super(context, 0, productList);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product, parent, false);
        }

        TextView productName = (TextView) convertView.findViewById(R.id.product_name);
        TextView productPrice = (TextView) convertView.findViewById(R.id.product_price);
        TextView productCreated = (TextView) convertView.findViewById(R.id.product_created);

        productName.setText(product.getName());
        productPrice.setText(String.valueOf(product.getPrice()));
        Timestamp created = product.getCreated();
        Date now = new Date();
        String formattedTimestamp;
        if ((now.getTime() - created.getTime()) < 1000*60*60*24){
            formattedTimestamp = new SimpleDateFormat("hh:mm:ss").format(created);
        }else {
            formattedTimestamp = new SimpleDateFormat("MM/dd/yyyy").format(created);
        }

        productCreated.setText(formattedTimestamp);

        return convertView;
    }
}
