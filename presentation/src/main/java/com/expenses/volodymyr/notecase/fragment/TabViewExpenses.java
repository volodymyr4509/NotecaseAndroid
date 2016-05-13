package com.expenses.volodymyr.notecase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by vkret on 02.12.15.
 */
public class TabViewExpenses extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "TabViewExpenses";
    public static final String PRODUCT_UUID_KEY = "productId";
    private ArrayAdapter<Product> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    //    private View view;
    private int checkedId;
    private ImageButton navigateLeft, navigateRight;
    private long begin, end;
    private Calendar calendar = GregorianCalendar.getInstance();
    private Snackbar snack;

    private ProductManager productManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating View fragment");

        View view = inflater.inflate(R.layout.tab_view_expenses, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        listView = (ListView) view.findViewById(R.id.costs_list);

        productManager = new ProductManagerImpl(getContext());

        RadioButton day = (RadioButton) view.findViewById(R.id.last_24_hours);
        RadioButton week = (RadioButton) view.findViewById(R.id.last_week);
        RadioButton month = (RadioButton) view.findViewById(R.id.last_month);

        //set onClickListener instead of onCheckedChangedListener because the last one calls onCheckedChanged twice
        day.setOnClickListener(this);
        week.setOnClickListener(this);
        month.setOnClickListener(this);
        checkedId = day.getId();

        navigateLeft = (ImageButton) view.findViewById(R.id.navigate_left);
        navigateLeft.setOnClickListener(this);
        navigateRight = (ImageButton) view.findViewById(R.id.navigate_right);
        navigateRight.setOnClickListener(this);

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        begin = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_YEAR, +1);
        end = calendar.getTimeInMillis();



        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Resuming View fragment");        snack = Snackbar.make(listView, "[" + new Date(begin) + ":" + new Date(end) + "]", Snackbar.LENGTH_LONG);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snack.getView().getLayoutParams();
        params.gravity = Gravity.TOP;
        snack.getView().setLayoutParams(params);
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
        intent.putExtra(PRODUCT_UUID_KEY, product.getUuid());
        Log.i(TAG, "Starting ViewExpenseActivity. ProductUuid = " + product.getUuid());
        startActivity(intent);
    }

    public void updateListView() {
        Log.i(TAG, "Updating ProductList");

        final Timestamp till = new Timestamp(end);
        final Timestamp since = new Timestamp(begin);

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
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, getResources().getConfiguration().locale);

        switch (v.getId()) {
            case R.id.navigate_left:
                moveLeft();
                break;
            case R.id.navigate_right:
                moveRight();
                break;
            case R.id.last_24_hours:
                begin = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_YEAR, +1);
                end = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_YEAR, -1);

                checkedId = v.getId();
                break;
            case R.id.last_week:
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
                end = calendar.getTimeInMillis();
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
                begin = calendar.getTimeInMillis();

                checkedId = v.getId();
                break;
            case R.id.last_month:
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                end = calendar.getTimeInMillis();
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                begin = calendar.getTimeInMillis();

                checkedId = v.getId();
                break;
        }
        snack.setText(df.format(begin) + " : " + df.format(end));

        snack.show();

        Log.v(TAG, "Pick time range: " + new Date(begin) + " : " + new Date(end));

        updateListView();
    }

    private void moveLeft() {
        end = begin;

        switch (checkedId) {
            case R.id.last_24_hours:
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                break;
            case R.id.last_week:
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                break;
            case R.id.last_month:
                calendar.add(Calendar.MONTH, -1);
                break;
        }

        //change moving direction left-right
        if (calendar.getTimeInMillis() == end) {
            moveLeft();
        }

        begin = calendar.getTimeInMillis();
    }

    private void moveRight() {
        begin = end;

        switch (checkedId) {
            case R.id.last_24_hours:
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                break;
            case R.id.last_week:
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case R.id.last_month:
                calendar.add(Calendar.MONTH, 1);
                break;
        }

        //change moving direction left-right
        if (calendar.getTimeInMillis() == begin) {
            moveRight();
        }

        end = calendar.getTimeInMillis();
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
