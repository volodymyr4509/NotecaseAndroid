package com.expenses.volodymyr.notecase.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.activity.EditExpenseActivity;
import com.expenses.volodymyr.notecase.entity.Product;
import com.expenses.volodymyr.notecase.util.DBHandler;

import java.util.List;

/**
 * Created by vkret on 02.12.15.
 */
public class TabViewExpenses extends Fragment {
    public static final String PRODUCT_ID_KEY = "productId";
    private ArrayAdapter<Product> adapter;
    private List<Product> productList;
    private DBHandler dbHandler;
    private ListView listView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_view_expenses, container, false);
        listView = (ListView) view.findViewById(R.id.costsList);

        dbHandler = DBHandler.getDbHandler(getActivity());
        productList = dbHandler.getAllProducts();

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, productList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EditExpenseActivity.class);
                Product product = (Product) parent.getAdapter().getItem(position);
                intent.putExtra(PRODUCT_ID_KEY, product.getId());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        productList = dbHandler.getAllProducts();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, productList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        productList = dbHandler.getAllProducts();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, productList);
        listView.setAdapter(adapter);
    }


}
