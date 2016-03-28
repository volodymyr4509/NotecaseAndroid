package com.expenses.volodymyr.notecase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import com.data.volodymyr.notecase.entity.Product;
import com.data.volodymyr.notecase.util.AuthenticationException;
import com.domain.volodymyr.notecase.manager.ProductManager;
import com.domain.volodymyr.notecase.manager.ProductManagerImpl;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.activity.ViewExpenseActivity;
import com.expenses.volodymyr.notecase.adapter.ProductAdapter;
import com.expenses.volodymyr.notecase.util.SafeAsyncTask;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by vkret on 02.12.15.
 */
public class TabViewExpenses extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "TabViewExpenses";
    public static final String PRODUCT_ID_KEY = "productId";
    private ArrayAdapter<Product> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private View view;
    private int checkedId;

    private ProductManager productManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating View fragment");

        view = inflater.inflate(R.layout.tab_view_expenses, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        listView = (ListView) view.findViewById(R.id.costs_list);

        productManager = new ProductManagerImpl(getContext());

        RadioButton last24 = (RadioButton) view.findViewById(R.id.last_24_hours);
        RadioButton lastWeek = (RadioButton) view.findViewById(R.id.last_week);
        RadioButton lastMonth = (RadioButton) view.findViewById(R.id.last_month);
        //set onClickListener instead of onCheckedChangedListener because the last one calls onCheckedChanged twice
        last24.setOnClickListener(this);
        lastWeek.setOnClickListener(this);
        lastMonth.setOnClickListener(this);
        checkedId = last24.getId();

        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Resuming View fragment");
        updateListView();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroying View fragment");
        super.onDestroy();
    }

    //AdapterView.OnItemClickListener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ViewExpenseActivity.class);
        Product product = (Product) parent.getAdapter().getItem(position);
        intent.putExtra(PRODUCT_ID_KEY, product.getUuid());
        Log.i(TAG, "Put ProductId = " + product.getUuid() + " to ViewExpenseActivity");
        startActivity(intent);
    }

    public void updateListView() {
        Log.i(TAG, "Updating ProductList");
        //last 24 hours by default
        long tillTimeMillis = System.currentTimeMillis();
        long sinceTimeMillis = tillTimeMillis - 24 * 60 * 60 * 1000;

        switch (checkedId) {
            case R.id.last_week:
                sinceTimeMillis = tillTimeMillis - 7 * 24 * 60 * 60 * 1000;
                break;
            case R.id.last_month:
                sinceTimeMillis = tillTimeMillis - (long) 31 * 24 * 60 * 60 * 1000;
                break;
        }

        final Timestamp till = new Timestamp(tillTimeMillis);
        final Timestamp since = new Timestamp(sinceTimeMillis);

        new SafeAsyncTask<Timestamp, Void, List<Product>>(getContext()) {
            @Override
            public List<Product> doInBackgroundSafe() throws AuthenticationException {
                return productManager.getAllProducts(since, till);
            }

            @Override
            protected void onPostExecute(List<Product> products) {
                Log.d(TAG, "Retrieved product list since: " + since + " till: " + till.toString() + ", size: " + products.size());
                adapter = new ProductAdapter(getContext(), products);
                listView.setAdapter(adapter);
            }
        }.execute();

    }

    @Override
    public void onClick(View v) {
        checkedId = v.getId();
        updateListView();
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "Sync Product list");
        new SafeAsyncTask<Void, Void, Boolean>(getContext()) {
            @Override
            public Boolean doInBackgroundSafe() throws AuthenticationException {
                return productManager.syncProducts();
            }
            @Override
            protected void onPostExecute(Boolean renderAgain) {
                swipeRefreshLayout.setRefreshing(false);
                if (renderAgain != null && renderAgain) {
                    updateListView();
                }
            }
        }.execute();
    }

}
